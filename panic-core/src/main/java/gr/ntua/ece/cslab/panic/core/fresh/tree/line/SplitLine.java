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

    private final String xDimensionLabel;
    private final String yDimensionLabel;
    private Type type;
    private double lambda, c;

    public enum Type{
        HORIZONTAL, VERTICAL, NORMAL
    }

//    public SplitLine(Double x1, Double x2, Double y1, Double y2, String xDimensionLabel, String yDimensionLabel) {
//        InputSpacePoint p1 = new InputSpacePoint();
//        p1.addDimension(xDimensionLabel, x1);
//        p1.addDimension(yDimensionLabel, y1);
//
//        InputSpacePoint p2 = new InputSpacePoint();
//        p2.addDimension(xDimensionLabel, x2);
//        p2.addDimension(yDimensionLabel, y2);
//
//
//        this(p1,p2,xDimensionLabel, yDimensionLabel);
//    }

    public SplitLine(InputSpacePoint p1, InputSpacePoint p2, String xDimensionLabel, String yDimensionLabel) {
        this.xDimensionLabel = xDimensionLabel;
        this.yDimensionLabel = yDimensionLabel;
//        System.out.format("SplitLine.SplitLine: p1: %s, p2:%s, p1-x:%.5f, p1-y:%.5f, p2-x:%.5f, p2-y: %.5f\n", p1,p2,
//                p1.getValue(xDimensionLabel), p1.getValue(yDimensionLabel),
//                p2.getValue(xDimensionLabel), p2.getValue(yDimensionLabel));

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
                pointValueToCompare = p.getValue(yDimensionLabel);
                lineValueToCompare = this.c;
                break;
            case VERTICAL:
                pointValueToCompare = p.getValue(xDimensionLabel);
                lineValueToCompare = this.c;
                break;
            case NORMAL:
                lineValueToCompare = lambda * p.getValue(xDimensionLabel) + c;
                pointValueToCompare = p.getValue(yDimensionLabel);
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
        if (this.type.equals(Type.HORIZONTAL)) {
            return String.format("%s = %.5f", yDimensionLabel, c);
        } else if (this.type.equals(Type.VERTICAL)) {
            return String.format("%s = %.5f", xDimensionLabel, c);
        } else if (this.type.equals(Type.NORMAL)) {
            return String.format("%s = %.5f * %s + %.5f", yDimensionLabel, lambda, xDimensionLabel, c);
        }
        return super.toString();
    }
}
