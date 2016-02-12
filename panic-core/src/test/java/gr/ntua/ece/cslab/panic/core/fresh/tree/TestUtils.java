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

package gr.ntua.ece.cslab.panic.core.fresh.tree;

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.utils.CSVFileManager;

import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * Class holding static methods to serve the test classes.
 * Created by Giannis Giannakopoulos on 2/12/16.
 */
public class TestUtils {

    public static List<OutputSpacePoint> getOutputSpacePoints() {
        return getOutputSpacePoints("sample-input.dat");
    }

    public static List<OutputSpacePoint> getOutputSpacePoints(String resourceName) {
        List<OutputSpacePoint> points = null;
        CSVFileManager manager = new CSVFileManager();
        URL url  = TestUtils.class.getClassLoader().getResource(resourceName);
        if(url!=null) {
            String file = url.getFile();
            manager.setFilename(file);
            points = manager.getOutputSpacePoints();
            Collections.shuffle(points);
            points = points.subList(0,100);
        }
        return points;
    }


}
