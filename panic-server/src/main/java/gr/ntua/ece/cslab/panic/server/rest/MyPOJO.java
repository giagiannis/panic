/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gr.ntua.ece.cslab.panic.server.rest;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import org.json.JSONObject;

/**
 *
 * @author giannis
 */

@XmlRootElement(name = "root")
public class MyPOJO implements Serializable{
    
    public int id;
    public String name;

    public MyPOJO() {
    
    }

    public MyPOJO(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public JSONObject getJSON(){
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("id", id);
        return obj;
    }
}
