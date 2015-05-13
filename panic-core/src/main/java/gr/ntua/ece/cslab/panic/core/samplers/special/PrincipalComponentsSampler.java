package gr.ntua.ece.cslab.panic.core.samplers.special;

import gr.ntua.ece.cslab.panic.core.containers.beans.EigenSpacePoint;
import gr.ntua.ece.cslab.panic.core.containers.beans.InputSpacePoint;
import gr.ntua.ece.cslab.panic.core.containers.beans.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractAdaptiveSampler;
import gr.ntua.ece.cslab.panic.core.samplers.utils.BorderPointsEstimator;
import gr.ntua.ece.cslab.panic.core.samplers.utils.PrincipalComponents;
import gr.ntua.ece.cslab.panic.core.utils.CSVFileManager;
import java.util.LinkedList;
import java.util.List;

/**
 * This class implements a Principal Components sampler, which identifies the
 * principal components of the sampled/deployed space and choose points pointing
 * to the most important aspects of the data.
 *
 * @author Giannis Giannakopoulos
 */
public class PrincipalComponentsSampler extends AbstractAdaptiveSampler {

    private final BorderPointsEstimator borderPoints;
    private final List<InputSpacePoint> chosenPoints;
    private final List<OutputSpacePoint> valuesReceived;

    public PrincipalComponentsSampler() {
        borderPoints = new BorderPointsEstimator();
        this.chosenPoints = new LinkedList<>();
        this.valuesReceived = new LinkedList<>();
    }

    public void addOutputSpacePoint(OutputSpacePoint point) {
        this.valuesReceived.add(point);
    }

    @Override
    public void configureSampler() {
        super.configureSampler(); //To change body of generated methods, choose Tools | Templates.
        borderPoints.setRanges(ranges);
        borderPoints.estimatePoints();
    }

    @Override
    public InputSpacePoint next() {
        super.next();
        InputSpacePoint point;
        if (borderPoints.hasMorePoints()) {
            point = borderPoints.getBorderPoint();
        } else {
            point = null;
        }
        this.chosenPoints.add(point);
        return point;
    }

    public void performPCAOnSampledPoints() {
        PrincipalComponents pc = new PrincipalComponents();
        pc.setInputData(valuesReceived);
        pc.calculateBase();
        for(OutputSpacePoint o : valuesReceived) {
            EigenSpacePoint eigenPoint = pc.outputSpaceToEigenSpace(o);
            OutputSpacePoint outPoint= pc.eigenSpaceToOutputSpace(eigenPoint);
            System.out.println(o+" transformed to "+eigenPoint+" and then back to "+outPoint);
        }
    }

    public static void main(String[] args) {
        CSVFileManager file = new CSVFileManager();
        file.setFilename(args[0]);

        PrincipalComponentsSampler sampler = new PrincipalComponentsSampler();
        sampler.setDimensionsWithRanges(file.getDimensionRanges());

        sampler.configureSampler();
        sampler.setSamplingRate(0.1);
        while (sampler.hasMore()) {
            InputSpacePoint s = sampler.next();
            if (s != null) {
                sampler.addOutputSpacePoint(file.getActualValue(s));
            } else {
                break;
            }
            System.out.println(s);
        }

        sampler.performPCAOnSampledPoints();
    }
}
