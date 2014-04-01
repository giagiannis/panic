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

package gr.ntua.ece.cslab.panic.server.containers.beans;

import com.owlike.genson.Genson;
import com.owlike.genson.TransformationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 * @author Giannis Giannakopoulos
 */
public class InputPointTest {
    
    private final Random rand;
    
    public InputPointTest() {
        this.rand = new Random();
    }

    @Test
    public void testEquality() {
        int count = rand.nextInt(20)+1;
        HashMap<String, Double> map = new HashMap<>();
        String key = "key";
        for(int i=0;i<count;i++) {
            map.put(key+i, rand.nextDouble());
        }
        
        InputPoint pointA = new InputPoint();
        pointA.setValues(map);
        InputPoint pointB = new InputPoint();
        pointB.setValues(map);
        Assert.assertEquals(pointA, pointB);               
    }
    
    @Test
    public void testSerialization() throws TransformationException, IOException {
        int count = rand.nextInt(20)+1;
        HashMap<String, Double> map = new HashMap<>();
        String key = "key";
        for(int i=0;i<count;i++) {
            map.put(key+i, rand.nextDouble());
        }
        
        InputPoint pointA = new InputPoint();
        pointA.setValues(map);
        
        Genson genson = new Genson();
        String json = genson.serialize(pointA);
        InputPoint pointB = genson.deserialize(json, InputPoint.class);
        Assert.assertEquals(pointA, pointB);
    }
    
    
}
