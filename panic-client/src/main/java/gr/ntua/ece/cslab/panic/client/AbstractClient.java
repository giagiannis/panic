/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.ntua.ece.cslab.panic.client;

import gr.ntua.ece.cslab.panic.beans.lists.ApplicationInfoList;
import gr.ntua.ece.cslab.panic.beans.rest.ApplicationInfo;
import gr.ntua.ece.cslab.panic.client.conf.ClientConfiguration;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.logging.Logger;import javax.xml.bind.JAXB;
;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

/**
 *
 * @author Giannis Giannakopoulos
 */
public class AbstractClient {
    
    private ClientConfiguration configuration;

    public AbstractClient() {
    }

    public ClientConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ClientConfiguration configuration) {
        this.configuration = configuration;
    }
    
    
    protected String issueRequest(String requestType, String document, String input) throws MalformedURLException, IOException {
        String urlString = "http://" + configuration.getHost() + ":" + configuration.getPort() + "/" + document;
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod(requestType);
        con.setRequestProperty("accept", "application/xml");
        con.setRequestProperty("Content-type", "application/xml");

        switch (requestType) {
            case "GET":
                con.setDoInput(true);
                break;
            case "POST":
                con.setDoInput(true);
                con.setDoOutput(true);
                break;
            case "PUT":
                con.setDoOutput(true);
                con.setDoInput(true);
                break;
        }

        if (input != null) {
            OutputStream out = con.getOutputStream();
            out.write(input.getBytes());
        }

        int responseCode = con.getResponseCode();
        Logger.getLogger("Orchestrator client").info("Response code of request is " + responseCode);
        StringBuilder builder = new StringBuilder();

        try (InputStream in = con.getInputStream()) {
            byte[] buffer = new byte[1024];
            int count;
            while ((count = in.read(buffer)) != -1) {
                builder.append(new String(buffer, 0, count));
            }
        }
        return builder.toString();

    }
}
