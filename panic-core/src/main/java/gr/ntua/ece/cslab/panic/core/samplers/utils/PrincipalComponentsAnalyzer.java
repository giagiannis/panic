/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.ntua.ece.cslab.panic.core.samplers.utils;

import gr.ntua.ece.cslab.panic.core.containers.beans.EigenSpacePoint;
import gr.ntua.ece.cslab.panic.core.containers.beans.InputSpacePoint;
import gr.ntua.ece.cslab.panic.core.containers.beans.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.samplers.UniformSampler;
import gr.ntua.ece.cslab.panic.core.utils.CSVFileManager;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.interfaces.decomposition.SingularValueDecomposition;
import org.ejml.ops.CommonOps;
import org.ejml.ops.SingularOps;

/**
 *
 * @author Giannis Giannakopoulos
 */
public class PrincipalComponentsAnalyzer {

    private int rank;
    
    private DenseMatrix64F 
            eigenVectorMatrix, 
            eigenValueMatrix;
    private DenseMatrix64F 
            data,
            meanMatrix,
            deviationMatrix,
            varianceMatrix,
            correlationMatrix;

    public PrincipalComponentsAnalyzer() {
    }

    public int getRank() {
        return rank;
    }

    public void setInputData(List<OutputSpacePoint> points) {
        int rows = points.size();
        Collection<String> keys = points.get(0).getInputSpacePoint().getKeysAsCollection();
        int columns = keys.size() + 1;
        this.data = new DenseMatrix64F(rows, columns);
        this.meanMatrix = new DenseMatrix64F(1, columns);
        this.rank = (rows>columns?columns:rows);
        for (int i = 0; i < rows; i++) {
            int j = 0;
            for (String s : keys) {
                this.data.set(i, j, points.get(i).getInputSpacePoint().getValue(s));
                this.meanMatrix.add(0, j, this.data.get(i, j));
                j += 1;
            }

            this.data.set(i, j, points.get(i).getValue());
            this.meanMatrix.add(0, j, this.data.get(i, j));
        }
        for (int j = 0; j < columns; j++) {
            this.meanMatrix.div(j, this.data.numRows);
        }
        
        
        this.deviationMatrix = new DenseMatrix64F(1, columns);
        for(int j = 0; j < columns; j++) {
            double sum = 0;
            for(int i = 0; i < rows; i++) {
                sum += Math.pow(this.data.get(i, j)-this.meanMatrix.get(0, j), 2);
            }
            sum = Math.sqrt(sum);
            this.deviationMatrix.set(0, j, sum);
        }
    }

    public void calculateVarianceMatrix() throws Exception {
        if(this.data == null || this.data.numCols<=0 || this.data.numRows <=0) {
            throw  new Exception("Data table not initialized!");
        }
        varianceMatrix = new DenseMatrix64F(rank, rank);
        for (int j1 = 0; j1 < rank; j1++) {
            for (int j2 = j1; j2 < rank; j2++) {
                double sum = 0.0;
                for (int i = 0; i < this.data.numRows; i++) {
                    sum += 
                            (this.data.get(i, j1) - this.meanMatrix.get(j1)) * 
                            (this.data.get(i, j2) - this.meanMatrix.get(j2));
                }
                sum /= (this.data.numRows - 1);
                varianceMatrix.set(j1, j2, sum);
                varianceMatrix.set(j2, j1, sum);
            }
        }
    }
    
    public void calculateCorrelationMatrix() throws Exception {
        if(this.data == null || this.data.numCols<=0 || this.data.numRows <=0) {
            throw  new Exception("Data table not initialized!");
        }
        if(this.varianceMatrix == null || this.varianceMatrix.numCols<=0 || this.varianceMatrix.numRows <=0) {
            throw  new Exception("Variance matrix not initialized!");
        }
        
        correlationMatrix = new DenseMatrix64F(rank, rank);
        for (int j1 = 0; j1 < rank; j1++) {
            for (int j2 = j1; j2 < rank; j2++) {
                correlationMatrix.set(j1, j2, this.varianceMatrix.get(j1, j2)/
                        (this.deviationMatrix.get(0, j1)*this.deviationMatrix.get(0,j2)));
                correlationMatrix.set(j2, j1, this.varianceMatrix.get(j1, j2)/
                        (this.deviationMatrix.get(0, j1)*this.deviationMatrix.get(0,j2)));
            }
        }
        
    }

    private void calculateBase(DenseMatrix64F matrixToDecompose) {
        SingularValueDecomposition<DenseMatrix64F> svd = 
                DecompositionFactory.svd(
                        matrixToDecompose.numRows, 
                        matrixToDecompose.numCols, 
                        false, 
                        true, 
                        false);
        svd.decompose(matrixToDecompose);
        DenseMatrix64F W, Vt;
        Vt=svd.getV(null, false);
        W=svd.getW(null);
        SingularOps.descendingOrder(null, false, W, Vt, false);
        this.eigenValueMatrix = W;
        this.eigenVectorMatrix = Vt;
    }
    
    public void calculateBaseWithVarianceMatrix() {
        this.calculateBase(this.varianceMatrix);
    }
    
    public void calculateBaseWithCorrelationMatrix() {
        this.calculateBase(this.correlationMatrix);
    }

    /**
     * This method returns the eigenvalue of the specified order (starting from
     * 1).
     *
     * @param order
     * @return
     */
    public double getEigenValue(int order) {
        if (order > this.getRank() - 1) {
            return Double.MAX_EXPONENT;
        }
        return eigenValueMatrix.get(order, order);
    }

    public EigenSpacePoint getEigenVector(int order) {
        return new EigenSpacePoint(CommonOps.extractRow(eigenVectorMatrix, order, null).getData());
    }

    public EigenSpacePoint getEigenSpacePoint(OutputSpacePoint outputSpacePoint) {
        double[] value = outputSpacePoint.getDoubles();
        DenseMatrix64F pointMatrix = DenseMatrix64F.wrap(value.length, 1, value);
        DenseMatrix64F transMean  = new DenseMatrix64F(this.meanMatrix.numCols, this.meanMatrix.numRows);
        CommonOps.transpose(this.meanMatrix, transMean);
        CommonOps.subtract(pointMatrix, transMean, pointMatrix);
        DenseMatrix64F result = new DenseMatrix64F(value.length, 1);
        CommonOps.mult(this.eigenVectorMatrix, pointMatrix,result);
        
        
        EigenSpacePoint point = new EigenSpacePoint();
        point.setData(result.getData());
        point.setKeys(outputSpacePoint);
        return point;
    }

    
    public static void main(String[] args) throws Exception {
        CSVFileManager file = new CSVFileManager();
        file.setFilename(args[0]);

        UniformSampler sampler = new UniformSampler();
        sampler.setDimensionsWithRanges(file.getDimensionRanges());
        sampler.setSamplingRate(1);
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
        
        comps.calculateBaseWithVarianceMatrix();
        
        for(int i=0;i< comps.getRank();i++) {
            System.out.println(comps.getEigenVector(i).toStringCSV());
        }
        
        System.out.println(comps.eigenValueMatrix);
        System.out.println(comps.eigenVectorMatrix);
        sampler = new UniformSampler();
        sampler.setDimensionsWithRanges(file.getDimensionRanges());
        sampler.setSamplingRate(1);
        sampler.configureSampler();
        
        while(sampler.hasMore()) {
            InputSpacePoint sample = sampler.next();
            OutputSpacePoint point = file.getActualValue(sample);
            System.out.println(comps.getEigenSpacePoint(point).toStringCSV());
        }
        
    }
}
