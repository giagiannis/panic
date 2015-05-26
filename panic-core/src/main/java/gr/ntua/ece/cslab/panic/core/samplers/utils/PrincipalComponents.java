/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.ntua.ece.cslab.panic.core.samplers.utils;

import gr.ntua.ece.cslab.panic.core.containers.beans.EigenSpacePoint;
import gr.ntua.ece.cslab.panic.core.containers.beans.InputSpacePoint;
import gr.ntua.ece.cslab.panic.core.containers.beans.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.samplers.RandomSampler;
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
import org.ejml.simple.SimpleMatrix;

/**
 *
 * @author giannis
 */
public class PrincipalComponents {

    private DenseMatrix64F eigenVectorMatrix, eigenValueMatrix;
    private int rank;

    private DenseMatrix64F data,
            varianceMatrix,
            meanMatrix;

    public PrincipalComponents() {
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

    public void calculateBase() {
        SingularValueDecomposition<DenseMatrix64F> svd = 
                DecompositionFactory.svd(this.varianceMatrix.numRows, 
                        this.varianceMatrix.numCols, false, true, false);
        svd.decompose(this.varianceMatrix);
        System.out.println(svd.getV(null, true));
        System.out.println(svd.getW(null));
    }

//    public EigenSpacePoint outputSpaceToEigenSpace(OutputSpacePoint point) {
////        System.out.println(this.eigenValueMatrix);
////        DenseMatrix64F res = new DenseMatrix64F(this.eigenVectorMatrix.numRows, 1);
////        DenseMatrix64F vec = DenseMatrix64F.wrap(this.toDoubleArray(point).length, 1, this.toDoubleArray(point));
////        DenseMatrix64F meanMatrix = DenseMatrix64F.wrap(this.mean.length, 1, this.mean);
////        CommonOps.subtract(vec, meanMatrix, vec);
////        CommonOps.mult(this.eigenVectorMatrix, vec, res);
////        EigenSpacePoint result = new EigenSpacePoint();
////        result.setData(res.getData());
////        result.setKeys(point);
////        return result;
//    }
//
//    public OutputSpacePoint eigenSpaceToOutputSpace(EigenSpacePoint point) {
////        DenseMatrix64F res = new DenseMatrix64F(this.mean.length, 1);
////        DenseMatrix64F vec = DenseMatrix64F.wrap(point.getData().length, 1, point.getData());
////        DenseMatrix64F meanMatrix = DenseMatrix64F.wrap(this.mean.length, 1, this.mean);
////
////        CommonOps.multTransA(this.eigenVectorMatrix, vec, res);
////        CommonOps.add(res, meanMatrix, res);
////        return new OutputSpacePoint(point, res.getData());
//
//    }

    /**
     * This method returns the eigenvalue of the specified order (starting from
     * 1).
     *
     * @param order
     * @return
     */
    public double getEigenValue(int order) {
        int maxDimension = (eigenValueMatrix.getNumCols() > eigenValueMatrix.getNumRows() ? eigenValueMatrix.getNumRows() : eigenValueMatrix.getNumCols());
        if (order > maxDimension - 1) {
            return Double.MAX_EXPONENT;
        }
        return eigenValueMatrix.get(order, order);
    }

    public EigenSpacePoint getEigenVector(int order) {
        DenseMatrix64F res = new DenseMatrix64F(1, eigenVectorMatrix.getNumCols());
        CommonOps.extractRow(eigenVectorMatrix, order, res);
        return new EigenSpacePoint(res.getData());
    }

    // util
    private double[] toDoubleArray(OutputSpacePoint point) {
        double[] results = new double[point.getInputSpacePoint().getKeysAsCollection().size() + 1];
        int index = 0;
        for (String key : point.getInputSpacePoint().getKeysAsCollection()) {
            results[index++] = point.getInputSpacePoint().getValue(key);
        }
        results[index] = point.getValue();
        return results;
    }

    public static void main(String[] args) throws Exception {
        CSVFileManager file = new CSVFileManager();
        file.setFilename(args[0]);

        UniformSampler sampler = new UniformSampler();
        sampler.setDimensionsWithRanges(file.getDimensionRanges());
        sampler.setSamplingRate(1.0);
        sampler.configureSampler();

        List<OutputSpacePoint> points = new LinkedList<>();
        while (sampler.hasMore()) {
            InputSpacePoint sample = sampler.next();
            points.add(file.getActualValue(sample));
        }
//        System.out.println(points.size());
//        
        PrincipalComponents comps = new PrincipalComponents();
        comps.setInputData(points);
        comps.calculateVarianceMatrix();
        comps.calculateBase();
//        for(int i=0;i<comps.getRank();i++) {
//            System.out.format("Rank %d: eigenvalue: %.5f with vector %s\n", i, comps.getEigenValue(i), comps.getEigenVector(i));
//        }
//        comps.eigenVectorMatrix = CommonOps.extract(comps.eigenVectorMatrix, 0, 2, 0, comps.eigenVectorMatrix.numRows);
//        
//        sampler.configureSampler();
//        for(int i=0;i<30;i++) {
//            System.out.println(comps.outputSpaceToEigenSpace(file.getActualValue(sampler.next())).toStringCSV());
//        }

    }
}
