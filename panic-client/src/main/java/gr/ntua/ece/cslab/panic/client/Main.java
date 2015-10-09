/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.ntua.ece.cslab.panic.client;

import gr.ntua.ece.cslab.panic.beans.api.ApplicationInfo;
import gr.ntua.ece.cslab.panic.beans.containers.DeploymentSpace;
import gr.ntua.ece.cslab.panic.client.conf.ClientConfiguration;
import java.io.StringWriter;
import javax.xml.bind.JAXB;
//import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author Giannis Giannakopoulos
 */
public class Main {
    
    public static void main(String[] args) throws Exception {
        if(args.length<2) {
            System.err.println("Please provide section and command to begin!");
            System.exit(1);
        }
        String host = "";
        int port = -1;
        if(System.getProperty("panic.server.url")==null) {
            System.err.println("Please provide -Dpanic.server.url parameter!");
            System.exit(1);
        } else {
            String url=System.getProperty("panic.server.url");
            host = url.split(":")[0];
            port  = new Integer(url.split(":")[1]);
        }
        String section = args[0];
        String command = args[1];
        if(section.equals("application")) {
            ApplicationClient client = new ApplicationClient();
            client.setConfiguration(new ClientConfiguration(host, port));
            switch (command) {
                case "list":
                    System.out.println(client.listApplications().getApplications());
                    break;
                case "create":
                    if(args.length!=3) {
                        System.err.println("Usage: application create [filename]");
                        System.exit(0);
                    } 
                    ApplicationInfo info = new ApplicationInfo();
                    info.setName("asd");
                    DeploymentSpace space = new DeploymentSpace();
                    space.addValue("hello", 1.0);
                    space.addValue("hello", 2.0);
                    space.addValue("world", 1.0);
                    info.setDeploymentSpace(space);
                    StringWriter a = new StringWriter();
                    JAXB.marshal(info, a);
                    System.out.println(a.toString());
                    System.out.println(info);
                    break;
                case "show":
//                    throw new NotImplementedException();
                case "batch-train":
//                    throw new NotImplementedException();
                case "start-profiling":
//                    throw new NotImplementedException();
                case "stop-profiling":
//                    throw new NotImplementedException();
                default:
                    System.err.println("Usage: application <command>\n"
                            + "\tCommand may be one of: "
                            + "[list|create|show|batch-train|start-profiling|stop-profiling]");
                    break;
            }
        }
    }
}
