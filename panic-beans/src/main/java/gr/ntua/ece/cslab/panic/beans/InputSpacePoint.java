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

package gr.ntua.ece.cslab.panic.beans;

import java.util.Map;

/**
 * This class represents an Input Space Point as a multidimensional point.
 * @author Giannis Giannakopoulos
 * @see MultiPoint
 */
public class InputSpacePoint extends MultiPoint {
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        StringBuilder strBuild = new StringBuilder();
        strBuild.append("(");
        for(String key: this.getKeysAsCollection()){
            strBuild.append(this.getValue(key));
            strBuild.append(", ");
        }
        strBuild.delete(strBuild.length()-2,strBuild.length());
        strBuild.append(")");
        return strBuild.toString();
    }
    
    public String toStringCSVFormat(){
        StringBuilder strBuild = new StringBuilder();
        for(String key: this.getKeysAsCollection()){
            strBuild.append(this.getValue(key));
            strBuild.append("\t");
        }
        strBuild.delete(strBuild.length()-1,strBuild.length());
        return strBuild.toString();
    }
    public InputSpacePoint getClone() {
        InputSpacePoint point = new InputSpacePoint();
        for(Map.Entry<String, Double> e:this.getValues().entrySet()) {
            point.addDimension(e.getKey(), e.getValue());
        }
        return point;
    }
    
    
}
