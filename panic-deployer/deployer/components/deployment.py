import logging
from threading import Thread
from deployer.components.vmgroup import VMGroup
from deployer.errors import ArgumentsError
from deployer.utils import generate_ssh_key_pair

__author__ = 'Giannis Giannakopoulos'


class Deployment:
    """
    This class represents a deployment entity. It holds a number of VMGroups and
    it is responsible for the allocation and the orchestration of the cloud resources.
    """

    def __init__(self):
        self.inject_ssh_key_pair = False
        self.__vm_groups = list()
        self.cloud_connector = None
        self.name = ''

    def configure(self, description):
        """
        This method configures new VMgroup objects according to the received description.
        :param description:
        :return:
        """
        if self.cloud_connector is None:
            raise ArgumentsError("Connector must be set!")
        self.name = description['name']
        self.inject_ssh_key_pair = description['inject_ssh_keypair']
        for group in description['groups']:
            g = VMGroup()
            g.configure(group)
            g.cloud_connector = self.cloud_connector.clone()
            for ability, value in group['provider_abilities'].iteritems():
                setattr(g.cloud_connector, ability, value)
            self.__vm_groups.append(g)

    def launch(self):
        logging.getLogger("launch").info("Starting deployment")
        self.__spawn_threads('create')
        logging.getLogger("launch").info("VMs visible -- construcing and injecting key pairs")
        if self.inject_ssh_key_pair:
            keys = generate_ssh_key_pair(keys_prefix='foobar')
            self.__spawn_threads('inject_ssh_key', args=[keys['private'], keys['public']])

        logging.getLogger("launch").info("Ok -- setting /etc/hosts files")
        hosts = dict()
        for vmg in self.__vm_groups:
            for ip, host in vmg.get_addresses().iteritems():
                hosts[ip] = host
        for vmg in self.__vm_groups:
            vmg.set_hosts(hosts)
        logging.getLogger("launch").info("Launch is finished!")

    def execute_script(self):
        self.__spawn_threads('execute_script')

    def has_more_steps(self):
        for g in self.__vm_groups:
            if g.has_more_scripts():
                return True
        return False

    def terminate(self):
        pass

    def __spawn_threads(self, method_to_call, args=None):
        """
        :param method_to_call:
        :param args:
        """
        threads = []
        for vm in self.__vm_groups:
            if args is None:
                t = Thread(target=getattr(vm, method_to_call))
            else:
                t = Thread(target=getattr(vm, method_to_call), args=args)
            t.start()
            threads.append(t)
        for t in threads:
            t.join()