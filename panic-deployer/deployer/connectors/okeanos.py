from kamaki.clients import ClientError
from kamaki.clients.astakos import AstakosClient
from kamaki.clients.cyclades import CycladesClient, CycladesNetworkClient
from sys import stderr
from deployer.connectors.generic import AbstractConnector

__author__ = 'Giannis Giannakopoulos'


class OkeanosConnector(AbstractConnector):
    """
    Okeanos connector.
    """

    def __init__(self):
        AbstractConnector.__init__(self)
        self.__cyclades = None
        self.__network_client = None
        self.attach_public_ipv4 = False
        self.private_network = -1

    def authenticate(self, authentication=None):
        """

        :param authentication:
        :return:
        """
        if self.__cyclades is not None:
            return True
        try:
            authcl = AstakosClient(authentication['URL'], authentication['TOKEN'])
            authcl.authenticate()
            self.__cyclades = CycladesClient(authcl.get_service_endpoints('compute')['publicURL'],
                                             authentication['TOKEN'])
            self.__network_client = CycladesNetworkClient(authcl.get_service_endpoints('network')['publicURL'],
                                                          authentication['TOKEN'])
        except ClientError:
            stderr.write('Connector initialization failed')
            return False
        return True

    def create_vm(self, name, flavor_id, image_id):
        """

        :param name:
        :param flavor_id:
        :param image_id:
        :return:
        """
        networks = []
        if self.attach_public_ipv4:
            networks.append({'uuid': self.__create_floating_ip()})
        if self.private_network != -1:
            networks.append({'uuid': self.private_network})

        response = self.__cyclades.create_server(name=name, flavor_id=flavor_id, image_id=image_id, networks=networks)
        ret_value = dict()
        ret_value['password'] = response['adminPass']
        ret_value['id'] = response['id']
        ret_value['user'] = response['metadata']['users']
        ret_value['hostname'] = 'snf-' + str(response['id']) + '.vm.okeanos.grnet.gr'
        return ret_value

    def delete_vm(self, server_id):
        """

        :param server_id:
        :return:
        """
        return self.__cyclades.delete_server(server_id)

    def list_vms(self):
        """


        :return:
        """
        return self.__cyclades.list_servers()

    def get_status(self, vm_id):
        """

        :param vm_id:
        :return:
        """
        return self.__cyclades.get_server_details(vm_id)

    def get_server_addresses(self, vm_id, ip_version=None):
        addresses = self.__cyclades.get_server_details(vm_id)['addresses']
        results = []
        while len(addresses) > 0:
            key, value = addresses.popitem()
            if ip_version is None or value['version'] == ip_version:
                results.append(value[0]['addr'])
        return results

    def __create_floating_ip(self):
        response = self.__network_client.create_floatingip()
        return response['floating_network_id']

    def create_private_network(self):
        response = self.__network_client.create_network(type='MAC_FILTERED', name='Deployment network')
        self.__network_client.create_subnet(
            network_id=response['id'],
            enable_dhcp=True,
            cidr='192.168.0.0/24'
        )
        return response['id']

    def clone(self):
        new_connector = OkeanosConnector()
        new_connector.attach_public_ipv4 = self.attach_public_ipv4
        new_connector.private_network = self.private_network
        new_connector.__network_client = self.__network_client
        new_connector.__cyclades = self.__cyclades
        return new_connector
