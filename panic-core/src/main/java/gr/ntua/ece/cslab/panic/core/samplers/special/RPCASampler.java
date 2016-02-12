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

//import gr.ntua.ece.cslab.panic.core.containers.beans.InputSpacePoint;
//import gr.ntua.ece.cslab.panic.core.containers.beans.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.partitioners.SingleDimensionPartitioner;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractAdaptiveSampler;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractSampler;
import gr.ntua.ece.cslab.panic.core.samplers.LatinHypercubeSampler;
import gr.ntua.ece.cslab.panic.core.analyzers.deprec.LoadingsAnalyzer;
import gr.ntua.ece.cslab.panic.core.analyzers.deprec.PrincipalComponentsAnalyzer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Giannis Giannakopoulos
 */
public class RPCASampler extends AbstractAdaptiveSampler {

    private AbstractSampler unbiasedSampler;
    private AbstractSampler biasedSampler;
    private Integer numberOfPhases = 4;
    private Integer samplesPerPhase;

    private final List<HashMap<String, List<Double>>> rangesToExamine;
    private final List<HashMap<String, List<Double>>> rangesExamined;
    private HashMap<String, List<Double>> currentRanges;

    public RPCASampler() {
        super();
        this.rangesToExamine = new LinkedList<>();
        this.rangesExamined = new LinkedList<>();
    }

    public Integer getNumberOfPhases() {
        return numberOfPhases;
    }

    public void setNumberOfPhases(Integer numberOfPhases) {
        this.numberOfPhases = numberOfPhases;
    }

    @Override
    public void configureSampler() {
        super.configureSampler();

        if (this.configuration.containsKey("samplesPerPhase")) {
            this.samplesPerPhase = new Integer(this.configuration.get("samplesPerPhase"));
        } else {
            this.samplesPerPhase = (int) Math.round((this.maxChoices * this.samplingRate) / this.numberOfPhases);
        }
        this.unbiasedSampler = new LatinHypercubeSampler();
        this.unbiasedSampler.setDimensionsWithRanges(this.ranges);
        this.unbiasedSampler.setPointsToPick(samplesPerPhase);
        this.unbiasedSampler.configureSampler();
        this.currentRanges = this.ranges;
//        System.err.println("Configuration:\t "+this.currentRanges);
//        System.out.println(this.configuration);
//        System.out.println("samplesPerPhase: "+this.configuration.get("samplesPerPhase"));
//        }
    }

    @Override
    public InputSpacePoint next() {
        super.next();
        InputSpacePoint sample;
        if (this.unbiasedSampler.hasMore()) {
            sample = this.unbiasedSampler.next();
        } else if (this.biasedSampler != null && this.biasedSampler.hasMore()) {
            sample = this.biasedSampler.next();
        } else {
            // revalidation phase
            LoadingsAnalyzer analyzer = this.performPCA(this.currentRanges);
            String[] ordering = analyzer.getInputDimensionsOrder();
            
            SingleDimensionPartitioner partitioner = new SingleDimensionPartitioner();
            partitioner.setRanges(this.currentRanges);
            partitioner.setDimensionKey(ordering[0]);
            partitioner.configurePartitioner();
            
            if(partitioner.getHigherRegion()!=null && !SingleDimensionPartitioner.filterPoints(this.outputSpacePoints, partitioner.getHigherRegion()).isEmpty()) {
                this.rangesToExamine.add(partitioner.getHigherRegion());
            }
            
            if(partitioner.getLowerRegion()!=null && !SingleDimensionPartitioner.filterPoints(this.outputSpacePoints, partitioner.getLowerRegion()).isEmpty()) {
                this.rangesToExamine.add(partitioner.getLowerRegion());
            }

            this.rangesExamined.add(this.currentRanges);
            this.currentRanges = this.rangesToExamine.remove(0);
            
            
            this.biasedSampler = new LatinHypercubeSampler();
            this.biasedSampler.setDimensionsWithRanges(this.currentRanges);
            this.biasedSampler.setPointsToPick(this.samplesPerPhase);
            this.biasedSampler.configureSampler();
            sample = this.biasedSampler.next();
        }
        return sample;
    }

    private LoadingsAnalyzer performPCA(Map<String, List<Double>> currentRange) {
        PrincipalComponentsAnalyzer analyzer = new PrincipalComponentsAnalyzer();
        List<OutputSpacePoint> data = SingleDimensionPartitioner.filterPoints(outputSpacePoints, currentRange);
        analyzer.setInputData(data);
        try {
            analyzer.calculateVarianceMatrix();
            analyzer.calculateCorrelationMatrix();
            analyzer.calculateBaseWithVarianceMatrix();
        } catch (Exception ex) {
            Logger.getLogger(RPCASampler.class.getName()).log(Level.SEVERE, null, ex);
        }
        int numberOfPC = 2;
        LoadingsAnalyzer loadingsAnalyzer = analyzer.getLoadingsAnalyzer(numberOfPC);
        loadingsAnalyzer.setPcWeights(analyzer.getPCWeights());
        return loadingsAnalyzer;
    }

    @Override
    public void addOutputSpacePoint(OutputSpacePoint outputSpacePoint) {
//        System.err.println(outputSpacePoint);
        super.addOutputSpacePoint(outputSpacePoint); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
