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

package gr.ntua.ece.cslab.panic.core.eval;

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.models.Model;

import java.util.List;

/**
 * Class providing metrics that evaluate the model accuracy
 * Created by Giannis Giannakopoulos on 1/12/16.
 */
public class Metrics {

    public static double meanSquareError(Model m, List<OutputSpacePoint> testPoints) {
        if (testPoints.size() < 1)
            return Double.NaN;
        double error = 0.0;
        try {
            for (OutputSpacePoint p : testPoints) {
                m.getPoint(p.getInputSpacePoint());
                double val = m.getPoint(p.getInputSpacePoint()).getValue() - p.getValue();
                error += val * val;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return error / testPoints.size();
    }

    public static double meanAbsoluteError(Model m, List<OutputSpacePoint> testPoints) {
        if (testPoints.size() < 1)
            return Double.NaN;
        double error = 0;
        for (OutputSpacePoint p : testPoints) {
            try {
                error += Math.abs(m.getPoint(p.getInputSpacePoint()).getValue() - p.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return error / testPoints.size();
    }
}
