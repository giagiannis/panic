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

/**
 * SplitLine is  a linear line in the form y = l*x+c.
 * Created by Giannis Giannakopoulos on 3/9/16.
 */
public class SplitLine {

    private final String xDimensionsLabel;
    private final String yDimensionsLabel;
    private Type type;
    private double lambda, c;

    public enum Type{
        HORIZONTAL, VERTICAL, NORMAL
    }

    public SplitLine(InputSpacePoint p1, InputSpacePoint p2, String xDimensionLabel, String yDimensionLabel) {
        this.xDimensionsLabel = xDimensionLabel;
        this.yDimensionsLabel = yDimensionLabel;
        double dy = p1.getValue(yDimensionLabel) - p2.getValue(yDimensionLabel);
        double dx = p1.getValue(xDimensionLabel) - p2.getValue(xDimensionLabel);
        if(dx==0.0 && dy==0.0) {
            System.err.println("We can't do this");
            System.exit(1);
        } else if(dx==0.0) {
            this.type = Type.VERTICAL;
            c = p1.getValue(xDimensionLabel);
        } else if(dy==0.0) {
            this.type = Type.HORIZONTAL;
            c = p1.getValue(yDimensionLabel);
        } else {
            this.type  = Type.NORMAL;
            lambda = dy/dx;
            c = p1.getValue(yDimensionLabel) - lambda*p1.getValue(xDimensionLabel);
        }
    }

    public int comparePoint(InputSpacePoint p) {
        double lineValueToCompare = 0;
        double pointValueToCompare = 0;
        switch (this.type) {
            case HORIZONTAL:
                pointValueToCompare = p.getValue(yDimensionsLabel);
                lineValueToCompare = this.c;
                break;
            case VERTICAL:
                pointValueToCompare = p.getValue(xDimensionsLabel);
                lineValueToCompare = this.c;
                break;
            case NORMAL:
                lineValueToCompare = lambda * p.getValue(xDimensionsLabel) + c;
                pointValueToCompare = p.getValue(yDimensionsLabel);
                break;
        }
        if(pointValueToCompare > lineValueToCompare) {
            return 1;
        } else if(pointValueToCompare < lineValueToCompare) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        if (this.type.equals(Type.HORIZONTAL)) {
            return String.format("y = %.5f\n", c);
        } else if (this.type.equals(Type.VERTICAL)) {
            return String.format("x = %.5f\n", c);
        } else if (this.type.equals(Type.NORMAL)) {
            return String.format("y = %.5f * x + %.5f", lambda, c);
        }
        return super.toString();
    }
}
