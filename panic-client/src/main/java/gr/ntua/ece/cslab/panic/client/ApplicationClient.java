/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.ntua.ece.cslab.panic.client;

import gr.ntua.ece.cslab.panic.beans.lists.ApplicationInfoList;
import gr.ntua.ece.cslab.panic.beans.api.ApplicationInfo;
import gr.ntua.ece.cslab.panic.beans.api.ProfilingJobInfo;
import gr.ntua.ece.cslab.panic.client.conf.ClientConfiguration;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXB;

/**
 *
 * @author giannis
 */
public class ApplicationClient extends AbstractClient {

	public String APPLICATION_ROOT_URL="application/";
	
	/**
	 * Lists the applications that have been described in the server
	 * @return
	 */
    public ApplicationInfoList listApplications() {
        String returnValue;
        try{
        	returnValue = this.issueRequest("GET", APPLICATION_ROOT_URL, null);
        } catch (Exception e) {
        	return null;
        }
        StringReader reader = new StringReader(returnValue);
        return JAXB.unmarshal(reader, ApplicationInfoList.class);
    }
    
    /**
     * Returns the application structure, given the application id.
     * @param id
     * @return
     */
    public ApplicationInfo getApplicationInfo(String id) {
    	String returnValue;
    	try{ 
    		returnValue = this.issueRequest("GET", APPLICATION_ROOT_URL+id+"/", null);
    	} catch (Exception e) {
    		return null;
    	}
    	StringReader reader = new StringReader(returnValue);
    	return JAXB.unmarshal(reader, ApplicationInfo.class);
    }
    
    /**
     * Creates a new application and an object is returned containing the application id.
     * @param appInfo
     * @return
     */
    public ApplicationInfo createApplication(ApplicationInfo appInfo) {
    	String returnValue;
    	try{ 
    		StringWriter writer = new StringWriter();
    		JAXB.marshal(appInfo, writer);
    		String content=writer.toString();
    		returnValue = this.issueRequest("PUT", APPLICATION_ROOT_URL, content);
    	} catch (Exception e) {
    		return null;
    	}
    	StringReader reader = new StringReader(returnValue);
    	return JAXB.unmarshal(reader, ApplicationInfo.class);
    }
    
    
    /**
     * Deletes the application based on its id.
     * @param id
     * @return
     */
    public boolean deleteApplication(String id) {
    	try{ 
    		this.issueRequest("DELETE", APPLICATION_ROOT_URL+id+"/", null);
    	} catch (Exception e) {
//    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
    
    /**
     * Creates a new profile
     * @param id
     * @param profilingJobInfo
     * @return
     */
    public ProfilingJobInfo profile(String id, ProfilingJobInfo profilingJobInfo) {
    	String returnValue;
    	try {
    		StringWriter writer = new StringWriter();
    		JAXB.marshal(profilingJobInfo, writer);
    		returnValue = this.issueRequest("POST", APPLICATION_ROOT_URL+id+"/profile/", writer.toString());
    	} catch (Exception e) {
    		return null;
    	}
    	StringReader reader  = new StringReader(returnValue);
    	return JAXB.unmarshal(reader, ProfilingJobInfo.class);
    }
    
    
    public static void main(String[] args) throws Exception {
        ApplicationClient client = new ApplicationClient();
        client.setConfiguration(new ClientConfiguration("localhost", 9999));
        System.out.println(client.listApplications().getApplications());
//        BatchTrainParameters params  = new BatchTrainParameters();
//        params.getSamples().getList().add(e)
//        client.batchTrain("5669b2fa-2fd8-4d44-b675-5f0d7571251e", );
    }
}
