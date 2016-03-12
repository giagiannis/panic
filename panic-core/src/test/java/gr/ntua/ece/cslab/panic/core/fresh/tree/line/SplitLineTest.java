
/*
 * Copyright 2016 Giannis Giannakopoulos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package gr.ntua.ece.cslab.panic.core.fresh.tree.line;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by Giannis Giannakopoulos on 3/9/16.
 */
public class SplitLineTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testCreation() throws Exception {
        // create a line with lambda equals to 1
        Random rand = new Random();
        double value = rand.nextDouble();
        InputSpacePoint p1 = new InputSpacePoint();
        p1.addDimension("x", 0.0);
        p1.addDimension("y", 0.0);

        InputSpacePoint p2 = new InputSpacePoint();
        p2.addDimension("x", 1.0);
        p2.addDimension("y", 1.0);


        SplitLine line = new SplitLine(p1, p2, "x", "y");

        for(int i=0;i<100;i++) {
//            SplitLine.Point point = new SplitLine.Point(rand.nextDouble(), rand.nextDouble());
            InputSpacePoint point = new InputSpacePoint();
            point.addDimension("x", rand.nextDouble());
            point.addDimension("y", rand.nextDouble());

            if(point.getValue("x") > point.getValue("y"))
                assertEquals(line.comparePoint(point), -1);
            else if(point.getValue("x") < point.getValue("y"))
                assertEquals(line.comparePoint(point), 1);
            else
                assertEquals(line.comparePoint(point), 0);
        }
    }
}