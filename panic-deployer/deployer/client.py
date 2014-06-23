import json
import os
from deployer.components.deployment import Deployment
from deployer.connectors.okeanos import OkeanosConnector

__author__ = 'Giannis Giannakopoulos'

from optparse import OptionParser


def replace_script_path_with_content(description, path_prefix):
    """
    This function replaces the script paths with script contents
    :param description:
    :return:
    """
    groups = description['groups']
    for g in groups:
        scripts = g['scripts']
        for s in scripts:
            if s.has_key('path'):
                f = open(path_prefix+"/"+s['path'])
                con = f.read()
                f.close()
                s['content'] = con
                s.pop("path", None)
    return description


def main():
    parser = OptionParser()
    parser.add_option("-d",
                      "--desc",
                      dest='description',
                      type="string",
                      help="The application description json")

    (options, args) = parser.parse_args()
    if options.description is None:
        parser.error("Description not given")
        exit(1)

    f = file(options.description)
    json_str = f.read()
    f.close()
    description = json.loads(json_str)
    # for key, value in description.iteritems():
    #     print key

    parsed = replace_script_path_with_content(
        description,
        os.path.dirname(os.path.abspath(options.description))
    )

    con = OkeanosConnector()
    con.authenticate(description['provider']['auth'])
    con.private_network = con.create_private_network()
    dep = Deployment()
    dep.cloud_connector = con
    dep.configure(parsed)
    dep.launch()

    while dep.has_more_steps():
        dep.execute_script()



if __name__=="__main__":
    main()