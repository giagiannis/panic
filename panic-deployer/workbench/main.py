import os
import sys
from deployer.connectors import OkeanosConnector
from deployer.components import VM
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


vm = VM()
vm.cloud_connector = con
vm.image_id = "78e96a57-2436-45c8-96b5-5eda9eb69be9"
vm.flavor_id = 150
vm.name = "tobedeleted"
sys.stdout.write("Creating VM...\t")
sys.stdout.flush()
vm.create()
sys.stdout.write("Done!\n")

print "\tHostname:\t"+vm.hostname
print "\tPassword:\t"+vm.login_password
sys.stdout.write("Waiting until VM is created...\t")
vm.wait_until_active()
sys.stdout.write("Done\n")
sys.stdout.write("Waiting until VM bootstraps...\t")
vm.wait_until_visible()
sys.stdout.write("Done!\n")
sys.stdout.write("Running scripts...\t")
vm.run_command("echo $(date) > /root/python_was_here")
vm.run_script('/home/giannis/script.sh')
sys.stdout.write("Done!\n")
print "\tVM with hostname "+vm.hostname+" is ready! Enjoy!"