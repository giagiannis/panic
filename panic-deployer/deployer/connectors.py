from kamaki.clients import ClientError
from kamaki.clients.astakos import AstakosClient
from kamaki.clients.cyclades import CycladesClient

from sys import stderr

__author__ = 'Giannis Giannakopoulos'


class AbstractConnector:
    """
    Abstract Connector is the IaaS connectors API. Each connector will override
    this class.
    """

    def __init__(self):
        pass

    def authenticate(self, authentication=None):
        raise NotImplemented

    def create_vm(self, name, flavor_id, image_id):
        """

        :param name:
        :param flavor_id:
        :param image_id:
        """
        raise NotImplemented

    def delete_vm(self, vm_id):
        """

        :param vm_id:
        :raise NotImplemented:
        """
        raise NotImplemented

    def list_vms(self):
        """


        :raise NotImplemented:
        """
        raise NotImplemented

    def get_status(self, vm_id):
        """

        :param vm_id:
        :raise NotImplemented:
        """
        raise NotImplemented

    def get_server_addresses(self, vm_id):
        raise NotImplemented


class OkeanosConnector(AbstractConnector):
    """
    Okeanos connector.
    """

    def __init__(self):
        AbstractConnector.__init__(self)
        self.cyclades = None
        self.attach_public_ipv4 = False

    def authenticate(self, authentication=None):
        """

        :param authentication:
        :return:
        """
        if self.cyclades is not None:
            return True
        try:
            authcl = AstakosClient(authentication['URL'], authentication['TOKEN'])
            authcl.authenticate()
            self.cyclades = CycladesClient(authcl.get_service_endpoints('compute')['publicURL'],
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
        networks = None if self.attach_public_ipv4 else []
        response = self.cyclades.create_server(name=name, flavor_id=flavor_id, image_id=image_id, networks=networks)
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
        return self.cyclades.delete_server(server_id)

    def list_vms(self):
        """


        :return:
        """
        return self.cyclades.list_servers()

    def get_status(self, vm_id):
        """

        :param vm_id:
        :return:
        """
        return self.cyclades.get_server_details(vm_id)

    def get_server_addresses(self, vm_id, ip_version=None):
        addresses = self.cyclades.get_server_details(vm_id)['addresses']
        results = []
        while len(addresses) > 0:
            key, value = addresses.popitem()
            if ip_version is None or value['version'] == ip_version:
                results.append(value[0]['addr'])
        return results


