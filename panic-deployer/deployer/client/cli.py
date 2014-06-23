from optparse import OptionParser
from deployer.client.abilities import configure_logger, parse_description_file, configure_connector, start_deployment, \
    load_state_file, save_state_file, terminate_deployment

__author__ = 'Giannis Giannakopoulos'


def configure_options():
    parser = OptionParser()
    parser.add_option("-d",
                      "--desc",
                      dest='description',
                      type="string",
                      help="The application description json")

    parser.add_option("-s",
                      "--save-state",
                      dest='statefile_save',
                      type='string',
                      help='the file to save the deployment state')

    parser.add_option("-l",
                      "--load-state",
                      dest='statefile_load',
                      type='string',
                      help='the json statefile you want to load')

    parser.add_option("-a",
                      "--actions",
                      dest='action',
                      choices=['launch', 'terminate'],
                      help="action to do")

    (options, args) = parser.parse_args()
    return (options, parser)


def endpoint():
    (options, parser) = configure_options()
    if options.description is None:
        parser.error("please provide a description file")
    if options.action is None:
        parser.error("please provide an action")

    configure_logger()
    description = parse_description_file(description_file_path=options.description)
    cloud_connector = configure_connector(description['provider'])

    if options.action == "launch":
        deployment = start_deployment(cloud_connector, description)
        if options.statefile_save is not None:
            save_state_file(deployment, options.statefile_save)

    if options.action == 'terminate':
        if options.statefile_load is not None:
            deployment = load_state_file(options.statefile_load, cloud_connector)
            terminate_deployment(deployment)

if __name__ == "__main__":
    endpoint()

