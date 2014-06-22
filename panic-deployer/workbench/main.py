import os
import paramiko
from paramiko.client import SSHClient
import sys
import time

from deployer.components.vmgroup import VMGroup
from deployer.connectors import OkeanosConnector
from deployer.conf import CLOUD_TOKEN, CLOUD_URL

__author__ = 'Giannis Giannakopoulos'


sys.stdout = os.fdopen(sys.stdout.fileno(), 'w', 0)

sys.stdout.write("Initializing ~okeanos connector...\t")
timestamp = time.time()
con = OkeanosConnector()
auth = dict()
con.authenticate({'URL': CLOUD_URL, 'TOKEN': CLOUD_TOKEN})
private_network_id = con.create_private_network()
con.private_network = private_network_id
sys.stdout.write("Done! ["+str(time.time()-timestamp)+" sec]\n")

sys.stdout.write("Configuring VM group...\t")
timestamp = time.time()
group = VMGroup()
group.flavor = 150
group.image = "78e96a57-2436-45c8-96b5-5eda9eb69be9"
group.name_prefix = "cluster"
group.cloud_connector = con.clone()
group.cloud_connector.attach_public_ipv4 = True
group.multiplicity = 2
f = open('/home/giannis/script.sh')
data = f.read()
f.close()
group.scripts = [data]
sys.stdout.write("Done! ["+str(time.time()-timestamp)+" sec]\n")

sys.stdout.write("Creating VMs...\t")
timestamp = time.time()
group.create()
sys.stdout.write("Done! ["+str(time.time()-timestamp)+" sec]\n")

sys.stdout.write("Injecting ssh keys...\t")
timestamp = time.time()
group.inject_ssh_key(
    private_key_path="/tmp/keys/3ksv2sxbounsnfufofih",
    public_key_path="/tmp/keys/3ksv2sxbounsnfufofih.pub")
sys.stdout.write("Done! ["+str(time.time()-timestamp)+" sec]\n")

sys.stdout.write("Executing script...\t")
timestamp = time.time()
group.execute_script()
sys.stdout.write("Done! ["+str(time.time()-timestamp)+" sec]\n")

#response = raw_input("Do your debugging and then hit enter to destroy stuff...")

sys.stdout.write("Deleting the VMs...\t")
timestamp = time.time()
group.delete()
con.cleanup()
sys.stdout.write("Done! ["+str(time.time()-timestamp)+" sec]\n")
