from kamaki.cli.config import Config
from kamaki.clients import ClientError
from kamaki.clients.astakos import AstakosClient
from kamaki.clients.cyclades import CycladesClient
from sys import stderr

__author__ = 'Giannis Giannakopoulos'


cnf = Config()
CLOUD = cnf.get("global", "default_cloud")
AUTH_URL = cnf.get_cloud(CLOUD, 'url')
AUTH_TOKEN = cnf.get_cloud(CLOUD, 'token')


try:
    auth = AstakosClient(AUTH_URL, AUTH_TOKEN)
    auth.authenticate()
except ClientError:
    stderr.write("Cannot authenticate using your .kamakirc")
    exit(1)


compute = auth.get_service_endpoints('compute')
print compute

cyclades = CycladesClient(compute['publicURL'], AUTH_TOKEN)

personality = []

#personality.append(
#    dict(contents=)
#)



