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

package gr.ntua.ece.cslab.panic.core.fresh.analyzers;

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;

import java.util.List;

/**
 * Analyzer factory returns an Analyzer object of the specified type.
 * Created by Giannis Giannakopoulos on 2/24/16.
 */
public class AnalyzerFactory {

    public Analyzer create(String type, List<OutputSpacePoint> points) {
        Analyzer analyzer = null;
        switch (type) {
            case "pca":
                analyzer = new PCAnalyzer(points);
                break;
            case "regression":
                analyzer = new RegressionAnalyzer(points);
                break;
            default:
                break;
        }
        return analyzer;
    }
}
