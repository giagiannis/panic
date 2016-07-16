
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

package gr.ntua.ece.cslab.panic.core.samplers.special;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.core.models.Model;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractAdaptiveSampler;

import java.util.Random;

/**
 * Uncertainty sampler is a typical example of Active Learning.
 * Created by Giannis Giannakopoulos on 7/8/16.
 */
public class UncertaintySampler extends AbstractAdaptiveSampler         {

    private Model model;
    private static int FIRST_PHASE=10;
    private Random random;

    private double[] preScores, postScores;

    public UncertaintySampler() {
        super();
        random = new Random();
    }

    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    public InputSpacePoint next() {
        super.next();
        InputSpacePoint result = null;
        int randomIndex = random.nextInt(this.maxChoices);
        if(this.pointsPicked<FIRST_PHASE) {
            result = this.getPointById(randomIndex);
            if(this.pointsPicked == FIRST_PHASE-1)
                this.preScores = this.getCurrentScores();
        } else {
          this.postScores = this.getCurrentScores();
            double maxVariance = 0.0;
            int pointIndex = -1;
            for(int i=0;i<this.maxChoices;i++) {
                double currentVar = Math.abs(this.preScores[i] - this.postScores[i]);
                if(currentVar>maxVariance) {
                    pointIndex = i;
                    maxVariance = currentVar;
                }
            }
            result = this.getPointById(pointIndex);
            this.preScores = this.postScores;
        }

        // keep two scores for each
        return result;
    }

    private double[] getCurrentScores() {
        double[] scores = new double[this.maxChoices];
        for(int i=0;i<this.maxChoices;i++) {
            InputSpacePoint current = this.getPointById(i);
            try {
                scores[i]=this.model.getPoint(current).getValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return  scores;
    }
}