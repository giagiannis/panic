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
import gr.ntua.ece.cslab.panic.core.utils.CSVFileManager;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.interfaces.decomposition.SingularValueDecomposition;
import org.ejml.ops.SingularOps;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Analyzer based on PCA.
 * Created by Giannis Giannakopoulos on 2/24/16.
 */
public class PCAnalyzer extends Analyzer {
    final List<String> dimensionOrdering;

    private double[][] eigenVectors;
    private double[] eigenValues;

    public PCAnalyzer(List<OutputSpacePoint> samples) {
        super(samples);
        this.dimensionOrdering = new LinkedList<>(this.samples.get(0).getInputSpacePoint().getKeysAsCollection());
        this.dimensionOrdering.add(samples.get(0).getKey());
    }

    @Override
    public void analyze() {
        double[][] raw = this.convertData();
        double[]  mean = this.mean(raw);
        double[][] data = this.subtract(raw, mean);

        this.executePCA(data);

        // we have eigenvalues and eigenvectors now
        double[] pcWeights = this.pcWeights();

        for(int i=0;i<this.dimensionOrdering.size();i++) {
            for(int j=0;j<this.dimensionOrdering.size();j++) {
                String dim1 = this.dimensionOrdering.get(i);
                String dim2 = this.dimensionOrdering.get(j);
                this.distances.get(dim1).put(dim2, this.distance(i,j,pcWeights,2));
            }
        }
    }

    double[][] convertData() {
        // convert data to double[][]
        double[][] data = new double[this.samples.size()][this.dimensionOrdering.size()];
        for(int i=0;i<data.length;i++) {
            for(int j=0;j<data[0].length-1;j++) {
                data[i][j] = this.samples.get(i).getInputSpacePoint().getValue(this.dimensionOrdering.get(j));
            }
            data[i][data[i].length-1] = this.samples.get(i).getValue();
        }
        return data;
    }

    double[] mean(double[][] data) {
        if(data.length==0) {
            return  null;
        }
        double[] mean = new double[data[0].length];
        for(int i=0;i<data.length;i++) {
            for(int j=0;j<data[i].length;j++) {
                mean[j]+=data[i][j];
            }
        }
        for(int j=0;j<mean.length;j++) {
            mean[j]/=data.length;
        }
        return mean;
    }

    double[][] subtract(double[][] original, double[] vector) {
        double[][] data = new double[original.length][original[0].length];
        for(int i=0;i<data.length;i++) {
            for(int j=0;j<data[0].length;j++) {
                data[i][j] = original[i][j] - vector[j];
            }
        }
        return data;
    }

    void executePCA(double[][] data) {
        DenseMatrix64F dataMatrix = new DenseMatrix64F(data.length, data[0].length);
        for(int i=0;i<data.length;i++) {
            for(int j=0;j<data[0].length;j++) {
                dataMatrix.set(i,j,data[i][j]);
            }
        }

        SingularValueDecomposition<DenseMatrix64F> svd =
                DecompositionFactory.svd(dataMatrix.numRows, dataMatrix.numCols, false, true, false);
        svd.decompose(dataMatrix);

        DenseMatrix64F V_t = svd.getV(null, true);  // eigenvectors here
        DenseMatrix64F W = svd.getW(null);          // eigenvalues here
        W.reshape(W.numCols, W.numCols, true);      // transform this into a square matrix
        SingularOps.descendingOrder(null, false, W, V_t, true);

        // set eigenValues
        this.eigenValues = new double[W.numRows];
        for(int i=0;i<W.numRows;i++) {
            this.eigenValues[i]=W.get(i,i);
        }

        //set eigenvectors - one at each row
        this.eigenVectors = new double[V_t.numRows][V_t.numCols];
        for(int i=0;i<this.eigenVectors.length;i++) {
            for(int j=0;j<this.eigenVectors.length;j++) {
                this.eigenVectors[i][j] = V_t.get(i,j);
            }
        }
    }

    double[][] getEigenVectors() {
        return this.eigenVectors;
    }

    double[] getEigenValues() {
        return this.eigenValues;
    }

    private void printCoordinates(PrintStream writer) {
        int numPC = 2;
        for(int j=0;j<this.eigenVectors.length;j++){
                writer.format("%s\t",this.dimensionOrdering.get(j));
                for(int i=0;i<numPC;i++) {
                    System.out.format("%.5f\t", this.eigenVectors[i][j]);
                }
            writer.println();
        }
    }

    double[] pcWeights() {
        double pcWeights[] = new double[this.eigenValues.length];
        double sum = 0.0;
        for(double d : this.eigenValues)
            sum+=d;
        for(int i=0;i<this.eigenValues.length;i++) {
            pcWeights[i] = this.eigenValues[i]/sum;
        }
        return pcWeights;
    }

    public static void main(String[] args) {
        if(args.length<1) {
            System.err.println("I need an input file to analyze!");
            System.exit(1);
        }
        CSVFileManager manager = new CSVFileManager();
        manager.setFilename(args[0]);
        PCAnalyzer analyzer = new PCAnalyzer(manager.getOutputSpacePoints());
        analyzer.analyze();
        analyzer.printCoordinates(System.out);
    }

    private double distance(int firstDimension, int secondDimension, double[] weights, int numPC) {
        double sum = 0.0;
        for(int pc = 0; pc<numPC; pc++) {
            double diff = this.eigenVectors[pc][firstDimension]-this.eigenVectors[pc][secondDimension];
            sum += diff*diff*weights[pc];
        }
        return Math.sqrt(sum);
    }

}
