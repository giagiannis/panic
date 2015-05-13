/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.ntua.ece.cslab.panic.core.samplers.utils;

import gr.ntua.ece.cslab.panic.core.containers.beans.EigenSpacePoint;
import gr.ntua.ece.cslab.panic.core.containers.beans.OutputSpacePoint;
import java.util.Collection;
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

    private double values[][];
    private double mean[];
    private DenseMatrix64F V, W;

    public PrincipalComponents() {
    }

    public void setInputData(List<OutputSpacePoint> points) {
        int rows = points.size();
        Collection<String> keys = points.get(0).getInputSpacePoint().getKeysAsCollection();
        int columns = keys.size() + 1;

        this.values = new double[rows][columns];
        this.mean = new double[columns];

        for (int i = 0; i < rows; i++) {
            int j = 0;
            for (String s : keys) {
                this.values[i][j] = points.get(i).getInputSpacePoint().getValue(s);
                this.mean[j] += this.values[i][j];
                j += 1;
            }
            this.values[i][j] = points.get(i).getValue();
            this.mean[j] += this.values[i][j];
        }
        for (int j = 0; j < columns; j++) {
            this.mean[j]/=rows;
            for (int i = 0; i < rows; i++) {
                this.values[i][j] = this.values[i][j]-this.mean[j];
            }
        }
//        DenseMatrix64F v = new DenseMatrix64F(1, columns);
//        CommonOps.extract(V_t, 0, 0 + 1, 0, columns, v, 0, 0);
//        System.out.println(v);


    }

    public void calculateBase() {
        int rows = this.values.length, columns = this.values[0].length;
        SimpleMatrix A = new SimpleMatrix(values);
        SingularValueDecomposition<DenseMatrix64F> svd
                = DecompositionFactory.svd(rows, columns, false, true, false);
        svd.decompose(A.getMatrix());
        V = svd.getV(null, true);
        W = svd.getW(null);
        SingularOps.descendingOrder(null, false, W, V, true);
    }
    
    public EigenSpacePoint outputSpaceToEigenSpace(OutputSpacePoint point) {
        DenseMatrix64F res = new DenseMatrix64F(this.mean.length,1 );
        DenseMatrix64F vec = DenseMatrix64F.wrap(this.toDoubleArray(point).length,1 , this.toDoubleArray(point));
        DenseMatrix64F meanMatrix = DenseMatrix64F.wrap(this.mean.length,1 , this.mean);
        CommonOps.subtract(vec, meanMatrix, vec);
        CommonOps.mult(this.V, vec, res);
        EigenSpacePoint result = new EigenSpacePoint();
        result.setData(res.getData());
        result.setKeys(point);
        return result;
    }
    
    public OutputSpacePoint eigenSpaceToOutputSpace(EigenSpacePoint point) {
        DenseMatrix64F res = new DenseMatrix64F(this.mean.length,1 );
        DenseMatrix64F vec = DenseMatrix64F.wrap(point.getData().length,1 , point.getData());
        DenseMatrix64F meanMatrix = DenseMatrix64F.wrap(this.mean.length,1 , this.mean);
        
        CommonOps.multTransA(this.V, vec, res);
        CommonOps.add(res, meanMatrix, res);
        return new OutputSpacePoint(point, res.getData());

    }
    
    private double[] toDoubleArray(OutputSpacePoint point) {
        double[] results = new double[point.getInputSpacePoint().getKeysAsCollection().size()+1];
        int index=0;
        for(String key : point.getInputSpacePoint().getKeysAsCollection()) {
            results[index++] = point.getInputSpacePoint().getValue(key);
        }
        results[index] = point.getValue();
        return results;
    }
}
