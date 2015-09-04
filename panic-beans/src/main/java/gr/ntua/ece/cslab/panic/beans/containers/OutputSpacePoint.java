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

package gr.ntua.ece.cslab.panic.beans.containers;

import java.nio.ByteBuffer;

/**
 *
 * @author Giannis Giannakopoulos
 */
public final class OutputSpacePoint  {
    
    private InputSpacePoint inputSpacePoint;
    private double value;
    private String key;

    public OutputSpacePoint() {
    }

    public OutputSpacePoint(InputSpacePoint inputSpacePoint, double value, String key) {
        this.inputSpacePoint = inputSpacePoint;
        this.value = value;
        this.key = key;
    }
    
    public OutputSpacePoint(EigenSpacePoint eigenPoint, double[] data) {
         this.inputSpacePoint = new InputSpacePoint();
        for(int i=0;i<eigenPoint.getData().length-1;i++) {
            this.inputSpacePoint.addDimension(eigenPoint.getKeys()[i], data[i]);
        }
        this.setKey(eigenPoint.getKeys()[eigenPoint.getKeys().length-1]);
        this.setValue(data[data.length-1]);
    }
    
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    
    public void setValue(String key, Double value) {
        this.setKey(key);
        this.setValue(value);
    }

    public InputSpacePoint getInputSpacePoint() {
        return inputSpacePoint;
    }

    public void setInputSpacePoint(InputSpacePoint inputSpacePoint) {
        this.inputSpacePoint = inputSpacePoint;
    }
    
    
    /**
     * Always returns the performance value last
     * @return 
     */
    public double[] getDoubles() {
        double[] result = new double[this.inputSpacePoint.getKeysAsCollection().size()+1];
        int i=0;
        for(String s:this.inputSpacePoint.getKeysAsCollection())
            result[i++] = this.inputSpacePoint.getValue(s);
        result[i++] = this.value;
        return result;
    }
    @Override
    public String toString() {
        return this.inputSpacePoint.toString()+" -> ("+ String.format("%.4f", this.value)+")";
    }
    
    // serializers and deserializers
    public byte[] getBytes() {
        byte[] inputSpacePointBytes = this.inputSpacePoint.getBytes();
        
        int count = 0;
        count += Integer.SIZE/8;
        count += this.key.getBytes().length;
        count += Double.SIZE/8;
        count += Integer.SIZE/8;
        count += inputSpacePointBytes.length;
        
        ByteBuffer buffer = ByteBuffer.allocate(count);
        buffer.putInt(this.key.getBytes().length);
        buffer.put(this.key.getBytes());
        buffer.putDouble(this.value);
        buffer.putInt(inputSpacePointBytes.length);
        buffer.put(inputSpacePointBytes);
        
        return buffer.array();
    }
    
    public void parseBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        
        int keySize = buffer.getInt();
        byte[] keyBytes = new byte[keySize];
        buffer.get(keyBytes);
        this.key = new String(keyBytes);
        this.value = buffer.getDouble();
        int inputSpacePointSize = buffer.getInt();
        byte[] inputSpacePointBytes = new byte[inputSpacePointSize];
        buffer.get(inputSpacePointBytes);
        this.inputSpacePoint = new InputSpacePoint();
        this.inputSpacePoint.parseBytes(inputSpacePointBytes);
    }    
}
