/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.ntua.ece.cslab.panic.client;

import gr.ntua.ece.cslab.panic.beans.lists.ApplicationInfoList;
import gr.ntua.ece.cslab.panic.client.conf.ClientConfiguration;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.bind.JAXB;

/**
 *
 * @author giannis
 */
public class ApplicationClient extends AbstractClient {
    
    public ApplicationInfoList listApplications() throws IOException {
        String returnValue = this.issueRequest("GET", "application/", null);
        StringReader reader = new StringReader(returnValue);
        return JAXB.unmarshal(reader, ApplicationInfoList.class);
    }
    
    public static void main(String[] args) throws IOException {
        ApplicationClient client = new ApplicationClient();
        client.setConfiguration(new ClientConfiguration("localhost", 9999));
        System.out.println(client.listApplications().getApplications());
    }
}
