import os
import warnings
from deployer.utils import get_random_file_name

with warnings.catch_warnings():
    warnings.simplefilter("ignore")
    import paramiko
    from paramiko.client import SSHClient

import socket
import time
from deployer.conf import SLEEP_TIMEOUT, MAX_WAIT_FOR_LOOPS
from deployer.errors import ArgumentsError
from deployer.connectors import AbstractConnector

__author__ = 'Giannis Giannakopoulos'


class VM:
    """
    Base VM class. This class implements the minimum actions available
    to a single VM
    """
    def __init__(self):
        self.cloud_connector = AbstractConnector()
        self.flavor_id = ''
        self.image_id = ''
        self.name = ''
        self.login_user = ''
        self.login_password = ''
        self.id = ''
        self.hostname = ''

    def create(self):
        """
        Creates a VM, with a specific name for a given flavor and image.
        :raise ArgumentsError: if there exist missing arguments
        """
        if self.name == '' or self.image_id == '' or self.flavor_id == '':
            raise ArgumentsError("name, image_id and flavor_id must be declared to start a VM!")
        response = self.cloud_connector.create_vm(name=self.name, image_id=self.image_id, flavor_id=self.flavor_id)
        self.login_user = response['user']
        self.login_password = response['password']
        self.id = response['id']
        self.hostname = response['hostname']

    def delete(self):
        """
        Forcefully destroy the VM .
        """
        self.cloud_connector.delete_vm(self.id)

    def run_command(self, command):
        """
        Execute an ssh command. It opens a new ssh connection.
        """
        sshclient = self.__create_ssh_client()
        sshclient.exec_command(command)
        sshclient.close()

    def run_script(self, script):
        """
        Transfer a script from the local fs and execute it to the VM.
        """
        sshclient = self.__create_ssh_client()
        sftp = sshclient.open_sftp()
        sftp.put(script, '/tmp/script-to-run')
        sftp.close()
        sshclient.exec_command("chmod +x /tmp/script-to-run")
        sshclient.exec_command("/tmp/script-to-run")
        sshclient.exec_command("rm /tmp/script-to-run")
        sshclient.close()

    def wait_until_visible(self):
        """
        This method blocks until a new SSH connections is established.
        The timeouts and number of tries are defined from configuration files.
        """
        self.__create_ssh_client().close()
        return

    def get_addresses(self, ip_version=None):
        return self.cloud_connector.get_server_addresses(self.id, ip_version)

    def __create_ssh_client(self):
        sshclient = SSHClient()
        sshclient.set_missing_host_key_policy(paramiko.WarningPolicy())
        for i in range(1, MAX_WAIT_FOR_LOOPS):
            try:
                sshclient.connect(hostname=self.hostname, port=22,
                                  username=self.login_user, password=self.login_password, timeout=SLEEP_TIMEOUT)
                return sshclient
            except socket.error:    # no route to host is expected here at first
                time.sleep(SLEEP_TIMEOUT)

    def get_file_content(self, remote_path):
        """
        Secure copy file from remote_path and get its contents.
        """
        local_path = "/tmp/"+get_random_file_name()
        sshclient = self.__create_ssh_client()
        sftp = sshclient.open_sftp()
        sftp.get(remote_path, local_path)
        sftp.close()
        f = open(local_path)
        content = f.read()
        f.close()
        os.remove(local_path)
        return content

    def put_file_content(self, file_content, remote_path):
        """
        Secure copy file contents to remote_path
        """
        local_path = "/tmp/"+get_random_file_name()
        sshclient = self.__create_ssh_client()
        sftp = sshclient.open_sftp()
        f = open(local_path, mode='w')
        f.write(file_content)
        f.flush()
        f.close()
        sftp.put(localpath=local_path, remotepath=remote_path)
        sftp.close()
        os.remove(local_path)

    def put_file(self, localpath, remotepath):
        sshclient = self.__create_ssh_client()
        sftp = sshclient.open_sftp()
        sftp.put(localpath=localpath, remotepath=remotepath)
        sftp.close()
