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

package gr.ntua.ece.cslab.panic.core.fresh.client;

import gr.ntua.ece.cslab.panic.core.fresh.tree.DecisionTree;

import java.io.PrintStream;
import java.util.List;

/**
 * Created by Giannis Giannakopoulos on 3/21/16.
 */
public class Client {

    protected static double mean(List<Double> list) {
        if(list.size()==0)
            return Double.NaN;
        double mean = 0.0;
        for(Double o  :list) {
            mean+= o;
        }
        return (list.size()==0?Double.NaN:mean/list.size());
    }

    protected static double variance(List<Double> list) {
        double mean = mean(list);
        double variance = 0.0;
        for(Double o : list) {
            variance+=(o-mean)*(o-mean);
        }
        return (list.size()==0?Double.NaN:variance/list.size());
    }


    protected static double percentile(List<Double> list, int rank) {
        int index = (int) Math.ceil((rank/100.0)*list.size());
        list.sort((a,b)->a.compareTo(b));
        return list.get(index-1);
    }

    protected static void printVariable(PrintStream out, List<Double> metric) {
        out.format("%.5f\t%.5f\t%.5f\t%.5f\t%.5f\t%.5f\t%.5f\t", mean(metric), variance(metric), percentile(metric, 1), percentile(metric, 25), percentile(metric, 50), percentile(metric, 75), percentile(metric, 100));
    }
}
