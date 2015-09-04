package gr.ntua.ece.cslab.panic.core.samplers.utils;

import com.itemanalysis.psychometrics.factoranalysis.EstimationMethod;
import com.itemanalysis.psychometrics.factoranalysis.ExploratoryFactorAnalysis;
import com.itemanalysis.psychometrics.factoranalysis.RotationMethod;
import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.samplers.UniformSampler;
import gr.ntua.ece.cslab.panic.core.utils.CSVFileManager;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.ejml.data.DenseMatrix64F;

/**
 *
 * @author Giannis Giannakopoulos
 */
public class FactorAnalysis {
    
    public static void main(String[] args) throws Exception {
        CSVFileManager file = new CSVFileManager();
        file.setFilename(args[0]);

        UniformSampler sampler = new UniformSampler();
        sampler.setDimensionsWithRanges(file.getDimensionRanges());
        sampler.setSamplingRate(.1);
        sampler.configureSampler();

        List<OutputSpacePoint> points = new LinkedList<>();
        while (sampler.hasMore()) {
            InputSpacePoint sample = sampler.next();
            points.add(file.getActualValue(sample));
        } 

        PrincipalComponentsAnalyzer comps = new PrincipalComponentsAnalyzer();
        comps.setInputData(points);
        comps.calculateVarianceMatrix();
        comps.calculateCorrelationMatrix();
        DenseMatrix64F priorMat = comps.getCorrelationMatrix();
        int dim = priorMat.numCols;
        RealMatrix matrix = new BlockRealMatrix(dim, dim);
        for(int i=0;i<dim;i++) {
            for(int j=0;j<dim;j++) {
                matrix.setEntry(i, j, priorMat.get(i, j));
            }
        }
//        System.out.println(comps.getCorrelationMatrix());
//        System.out.println(matrix);
        
        comps.calculateBaseWithCorrelationMatrix();

        ExploratoryFactorAnalysis fa = new ExploratoryFactorAnalysis(matrix, 1);
        fa.estimateParameters(EstimationMethod.PRINCOMP,RotationMethod.CLUSTER);
//        System.out.println(fa.getFactorMethod());ice
        System.out.println(fa.printOutput(4));
//        fa.printOutput();
    }
    
}
