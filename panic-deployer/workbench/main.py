import os
import sys

from deployer.components.vmgroup import VMGroup
from deployer.connectors import OkeanosConnector
from deployer.conf import CLOUD_TOKEN, CLOUD_URL

__author__ = 'Giannis Giannakopoulos'


sys.stdout = os.fdopen(sys.stdout.fileno(), 'w', 0)


sys.stdout.write("Initializing ~okeanos connector...\t")
con = OkeanosConnector()
auth = dict()
auth['URL'] = CLOUD_URL
auth['TOKEN'] = CLOUD_TOKEN
con.authenticate(auth)
sys.stdout.write("Done!\n")


group = VMGroup()
group.flavor = 150
group.image = "78e96a57-2436-45c8-96b5-5eda9eb69be9"
group.name_prefix = "cluster"
group.cloud_connector = con.clone()
group.cloud_connector.attach_public_ipv4 = True
group.multiplicity = 3

f = open('/home/giannis/script.sh')
data = f.read()
f.close()

group.scripts = [data]

group.create()

group.execute_script()
#con.attach_public_ipv4 = False
#con.private_network = con.create_private_network()
#
#vm = VM()
#vm.cloud_connector = con
#vm.image_id = "78e96a57-2436-45c8-96b5-5eda9eb69be9"
#vm.flavor_id = 150
#vm.name = "tobedeleted"
#sys.stdout.write("Creating VM...\t")
#sys.stdout.flush()
#vm.create()
#sys.stdout.write("Done!\n")
#
#print "\tHostname:\t"+vm.hostname
#print "\tPassword:\t"+vm.login_password
#sys.stdout.write("Waiting until VM is created...\t")
#vm.wait_until_active()
#sys.stdout.write("Done\n")
#sys.stdout.write("Waiting until VM bootstraps...\t")
#vm.wait_until_visible()
#sys.stdout.write("Done!\n")
#sys.stdout.write("Running scripts...\t")
#f = open('/home/giannis/script.sh')
#data = f.read()
#f.close()
#vm.run_command(data)
#sys.stdout.write("Done!\n")
#print "\tVM with hostname "+vm.hostname+" is ready! Enjoy!" + str(vm.get_addresses())
#
#vm = VM()
#vm.cloud_connector = con
#vm.image_id = "78e96a57-2436-45c8-96b5-5eda9eb69be9"
#vm.flavor_id = 150
#vm.name = "tobedeleted"
#sys.stdout.write("Creating VM...\t")
#sys.stdout.flush()
#vm.create()
#sys.stdout.write("Done!\n")
#
#print "\tHostname:\t"+vm.hostname
#print "\tPassword:\t"+vm.login_password
#sys.stdout.write("Waiting until VM is created...\t")
#vm.wait_until_active()
#sys.stdout.write("Done\n")
#sys.stdout.write("Waiting until VM bootstraps...\t")
#vm.wait_until_visible()
#sys.stdout.write("Done!\n")
#sys.stdout.write("Running scripts...\t")
#f = open('/home/giannis/script.sh')
#data = f.read()
#f.close()
#vm.run_command(data)
#sys.stdout.write("Done!\n")
#print "\tVM with hostname "+vm.hostname+" is ready! Enjoy!" + str(vm.get_addresses())
#
#vm = VM()
#vm.cloud_connector = con
#vm.image_id = "78e96a57-2436-45c8-96b5-5eda9eb69be9"
#vm.flavor_id = 150
#vm.name = "tobedeleted"
#sys.stdout.write("Creating VM...\t")
#sys.stdout.flush()
#vm.create()
#sys.stdout.write("Done!\n")
#
#print "\tHostname:\t"+vm.hostname
#print "\tPassword:\t"+vm.login_password
#sys.stdout.write("Waiting until VM is created...\t")
#vm.wait_until_active()
#sys.stdout.write("Done\n")
#sys.stdout.write("Waiting until VM bootstraps...\t")
#vm.wait_until_visible()
#sys.stdout.write("Done!\n")
#sys.stdout.write("Running scripts...\t")
#f = open('/home/giannis/script.sh')
#data = f.read()
#f.close()
#vm.run_command(data)
#sys.stdout.write("Done!\n")
#print "\tVM with hostname "+vm.hostname+" is ready! Enjoy!" + str(vm.get_addresses())
#
#vm = VM()
#vm.cloud_connector = con
#vm.image_id = "78e96a57-2436-45c8-96b5-5eda9eb69be9"
#vm.flavor_id = 150
#vm.name = "tobedeleted"
#sys.stdout.write("Creating VM...\t")
#sys.stdout.flush()
#vm.create()
#sys.stdout.write("Done!\n")
#
#print "\tHostname:\t"+vm.hostname
#print "\tPassword:\t"+vm.login_password
#sys.stdout.write("Waiting until VM is created...\t")
#vm.wait_until_active()
#sys.stdout.write("Done\n")
#sys.stdout.write("Waiting until VM bootstraps...\t")
#vm.wait_until_visible()
#sys.stdout.write("Done!\n")
#sys.stdout.write("Running scripts...\t")
#f = open('/home/giannis/script.sh')
#data = f.read()
#f.close()
#vm.run_command(data)
#sys.stdout.write("Done!\n")
#print "\tVM with hostname "+vm.hostname+" is ready! Enjoy!" + str(vm.get_addresses())
#
#con.attach_public_ipv4 = True
#vm = VM()
#vm.cloud_connector = con
#vm.image_id = "78e96a57-2436-45c8-96b5-5eda9eb69be9"
#vm.flavor_id = 150
#vm.name = "tobedeleted"
#sys.stdout.write("Creating VM...\t")
#sys.stdout.flush()
#vm.create()
#sys.stdout.write("Done!\n")
#
#print "\tHostname:\t"+vm.hostname
#print "\tPassword:\t"+vm.login_password
#sys.stdout.write("Waiting until VM is created...\t")
#vm.wait_until_active()
#sys.stdout.write("Done\n")
#sys.stdout.write("Waiting until VM bootstraps...\t")
#vm.wait_until_visible()
#sys.stdout.write("Done!\n")
#sys.stdout.write("Running scripts...\t")
#f = open('/home/giannis/script.sh')
#data = f.read()
#f.close()
#vm.run_command(data)
#sys.stdout.write("Done!\n")
#print "\tVM with hostname "+vm.hostname+" is ready! Enjoy!" + str(vm.get_addresses())