/*
 * Copyright 2014 Giannis Giannakopoulos.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gr.ntua.ece.cslab.panic.beans.api;

import gr.ntua.ece.cslab.panic.beans.containers.DeploymentSpace;
import java.io.Serializable;
import java.util.UUID;
import javax.xml.bind.annotation.XmlRootElement;
/**
 *
 * @author Giannis Giannakopoulos
 */
@XmlRootElement
public class ApplicationInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private DeploymentSpace deploymentSpace;

    public ApplicationInfo() {
        this.id = UUID.randomUUID().toString();
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeploymentSpace getDeploymentSpace() {
        return deploymentSpace;
    }

    public void setDeploymentSpace(DeploymentSpace deploymentSpace) {
        this.deploymentSpace = deploymentSpace;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s, %s)", 
                this.id, this.name, this.deploymentSpace);
    }
    
    
}