from threading import Thread
from deployer.components.vm import VM
from deployer.connectors.generic import AbstractConnector
from deployer.errors import ArgumentsError

__author__ = 'Giannis Giannakopoulos'


class VMGroup:
    """
    VMGroup is a composite component. It represents a simple way to package
    a group of VMs and treat them in a similar manner.
    """
    def __init__(self):
        self.cloud_connector = AbstractConnector()
        self.vms = []
        self.scripts = list()
        self.multiplicity = 1
        self.flavor = ''
        self.image = ''
        self.name_prefix = ''

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
            self.vms.append(vm)
        self.__spawn_threads('create')
        for vm in self.vms:
            vm.wait_until_visible()

    def execute_script(self):
        """
        This method executes the next script in the queue that it should be executed.
        """
        current_script = self.scripts.
        self.__spawn_threads('run_command', args=[current_script])

    def __spawn_threads(self, method_to_call, args=None):
        threads = []
        for vm in self.vms:
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