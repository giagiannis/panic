import os
from threading import Thread
from deployer.components.vm import VM
from deployer.connectors.generic import AbstractConnector
from deployer.errors import ArgumentsError
from deployer.utils import get_random_file_name

__author__ = 'Giannis Giannakopoulos'


class VMGroup:
    """
    VMGroup is a composite component. It represents a simple way to package
    a group of VMs and treat them in a similar manner.
    """
    def __init__(self):
        self.cloud_connector = AbstractConnector()
        self.scripts = list()
        self.multiplicity = 1
        self.flavor = ''
        self.image = ''
        self.name_prefix = ''
        self.__script_index = 0
        self.__vms = []

    def create(self):
        """
        This method creates VMs and blocks until they become visible from the Orchestrator
        """
        if self.flavor == '' or self.image == '' or self.name_prefix == '':
            raise ArgumentsError("I need flavor, image and name_prefix for the vm group and set cloud connector!")
        for i in range(1, self.multiplicity+1):
            vm = VM()
            vm.cloud_connector = self.cloud_connector
            vm.image_id = self.image
            vm.flavor_id = self.flavor
            vm.name = self.name_prefix+str(i)
            self.__vms.append(vm)
        self.__spawn_threads('create')
        self.__spawn_threads('wait_until_visible')

    def execute_script(self):
        """
        This method executes the next script in the queue that it should be executed.
        """
        if self.__script_index >= len(self.scripts):
            return
        current_script = self.scripts[self.__script_index]
        self.__script_index += 1
        self.__spawn_threads('run_command', args=[current_script])

    def inject_ssh_key(self, private_key_path, public_key_path):
        """
        This method injects a previously created SSH keypair to the VMs of the group. This keypair is used as default
        from the VMs. The public key is placed into authorized_keys file
        """
        self.__spawn_threads('run_command', args=["mkdir -p /root/.ssh"])
        self.__spawn_threads('put_file', args=[private_key_path, "/root/.ssh/id_rsa"])
        self.__spawn_threads('put_file', args=[public_key_path, "/root/.ssh/id_rsa.pub"])
        self.__spawn_threads('run_command', args=["cat /root/.ssh/id_rsa.pub >> /root/.ssh/authorized_keys &&"
                                                  "chmod 700 /root/.ssh/ && chmod 600 /root/.ssh/id_rsa && "
                                                  "echo \"StrictHostKeyChecking no\" > /root/.ssh/config"])
        #for vm in self.__vms:
        #    vm.run_command("mkdir -p /root/.ssh")
        #    vm.put_file(localpath=private_key_path, remotepath="/root/.ssh/id_rsa")
        #    vm.put_file(localpath=public_key_path, remotepath="/root/.ssh/id_rsa.pub")
        #    vm.run_command("cat /root/.ssh/id_rsa.pub >> /root/.ssh/authorized_keys ")
        #    vm.run_command("chmod 700 /root/.ssh/ && chmod 600 /root/.ssh/id_rsa")
        #    vm.run_command("echo \"StrictHostKeyChecking no\" > /root/.ssh/config")

    def delete(self):
        self.__spawn_threads('delete')

    def __spawn_threads(self, method_to_call, args=None):
        threads = []
        for vm in self.__vms:
            if args is None:
                t = Thread(target=getattr(vm, method_to_call))
            else:
                t = Thread(target=getattr(vm, method_to_call), args=args)
            t.start()
            threads.append(t)
        for t in threads:
            t.join()


def main():
    print "foofootos"
    group = VMGroup()
    t = Thread(target=group.create)
    t.start()
    t.join()
    del t

if __name__ == "__main__":
    main()