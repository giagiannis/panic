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
package gr.ntua.ece.cslab.panic.server.models;

import java.io.File;
import java.io.IOException;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Resample;

/**
 *
 * @author Giannis Giannakopoulos
 */
public abstract class AbsractModel {

    //each classifier must have instances
    protected Instances instances;

    // TODO: throw it away when in online mode..
    protected Instances instancesForTraining;

    public void setInputFile(String inputFile) throws IOException {
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File(inputFile));
        this.instances = loader.getDataSet();
        this.instances.setClassIndex(instances.numAttributes() - 1);
    }

    public void sampleDataSet(double percentage) throws Exception {
        Resample resampler = new Resample();
        resampler.setInputFormat(instances);
        resampler.setSampleSizePercent(percentage);
        this.instancesForTraining = Filter.useFilter(this.instances, resampler);
    }

}
