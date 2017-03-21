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

import java.util.HashMap;
import java.util.Map;

/**
 * SplitLine is a multidimensional line.
 * Created by Giannis Giannakopoulos on 3/9/16.
 */
public class SplitLine {
    private Map<String, Double> coefficients;

    /**
     * Constructor used to generate a 2D SplitLine based on 2 points.
     * @param p1 the first InputSpace point
     * @param p2 the second InputSpace point
     * @param xDimensionLabel the label of the horizontal dim
     * @param yDimensionLabel the label of the vertical dim
     */
    public SplitLine(InputSpacePoint p1, InputSpacePoint p2, String xDimensionLabel, String yDimensionLabel) {
        double lambda, c;
        double dy = p1.getValue(yDimensionLabel) - p2.getValue(yDimensionLabel);
        double dx = p1.getValue(xDimensionLabel) - p2.getValue(xDimensionLabel);
        this.coefficients = new HashMap<>();
        if(dx==0.0 && dy==0.0) {
            System.err.println("SplitLine.SplitLine: Cannot create a line with one point!");
            System.exit(1);
        } else if(dx==0.0) {
            this.coefficients.put(yDimensionLabel, 0.0);
            this.coefficients.put(xDimensionLabel, 1.0);
            this.coefficients.put("constant", -p1.getValue(xDimensionLabel));
        } else if(dy==0.0) {
            this.coefficients.put(yDimensionLabel, 1.0);
            this.coefficients.put(xDimensionLabel, 0.0);
            this.coefficients.put("constant", -p1.getValue(xDimensionLabel));
        } else {
            lambda = dy/dx;
            c = p1.getValue(yDimensionLabel) - lambda*p1.getValue(xDimensionLabel);
            this.coefficients.put(yDimensionLabel, 1.0);
            this.coefficients.put(xDimensionLabel, -lambda);
            this.coefficients.put("constant", -c);
        }
    }

    /**
     * Constructor used to set coefficients of the split line in the form:<br/>
     * h1 * x1 + h2 * x2 + ... + hn * xn + h_{n+1} = 0
     * @param coefficients
     */
    public SplitLine(Map<String, Double> coefficients) {
        this.coefficients = coefficients;
    }

    public int comparePoint(InputSpacePoint p) {
        return this.getLineValue(p).compareTo(0.0);
    }

    /**
     * Method  alias to:
     * this.comparePoint(point)==-1 || this.comparePoint(point)==0
     * @param point
     * @return
     */
    public boolean lessOrEqual(InputSpacePoint point) {
        int compValue = this.comparePoint(point);
        return ((compValue==-1) || (compValue==0));
    }

    @Override
    public String toString() {
        return this.coefficients.toString();
    }

    public static int fuzzyCompare(SplitLine line, InputSpacePoint p, Double epsilon) {
            return new Double(Math.abs(line.getLineValue(p))).compareTo(epsilon);
    }

    private Double getLineValue(InputSpacePoint p) {
        Double pointValue = 0.0;
        for (Map.Entry<String, Double> kv : this.coefficients.entrySet()) {
            double val = 1.0;
            if (!kv.getKey().equals("constant")) {
                val = p.getValue(kv.getKey());
            }
            pointValue += kv.getValue() * val;
        }
        return pointValue;
    }
}
