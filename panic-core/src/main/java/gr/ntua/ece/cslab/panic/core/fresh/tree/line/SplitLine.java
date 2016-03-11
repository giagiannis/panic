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

    private Type type;
    private double lambda, c;

    public enum Type{
        HORIZONTAL, VERTICAL, NORMAL
    }
    
    /**
     * Point used to construct a line
     */
    public static class Point {
        private double x, y;

        /**
         * Simple 2D point value
         * @param x x coefficient (horizontal axis)
         * @param y y coefficient (vertical axis)
         */
        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Constructor used to cast an InputSpacePoint to a 2D point
         * @param point original InputSpacePoint
         * @param xDim x dimension name
         * @param yDim y dimension name
         */
        public Point(InputSpacePoint point, String xDim, String yDim) {
            this.x = point.getValue(xDim);
            this.y = point.getValue(yDim);
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
        @Override
        public String toString() {
            return String.format("(%.5f, %.5f)", x, y);
        }
    }


    public SplitLine(Point p1, Point p2) {
        double dy = p1.getY() - p2.getY();
        double dx = p1.getX() - p2.getX();
        if(dx==0.0 && dy==0.0) {
            System.err.println("We can't do this");
            System.exit(1);
        } else if(dx==0.0) {
            this.type = Type.VERTICAL;
            c = p1.getX();
        } else if(dy==0.0) {
            this.type = Type.HORIZONTAL;
            c = p1.getY();
        } else {
            this.type  = Type.NORMAL;
            lambda = dy/dx;
            c = p1.getY() - lambda*p1.getX();
        }
    }

    public int comparePoint(Point p) {
        double lineValueToCompare = 0;
        double pointValueToCompare = 0;
        switch (this.type) {
            case HORIZONTAL:
                pointValueToCompare = p.getX();
                lineValueToCompare = this.c;
                break;
            case VERTICAL:
                pointValueToCompare = p.getY();
                lineValueToCompare = this.c;
                break;
            case NORMAL:
                lineValueToCompare = lambda * p.getX() + c;
                pointValueToCompare = p.getY();
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
