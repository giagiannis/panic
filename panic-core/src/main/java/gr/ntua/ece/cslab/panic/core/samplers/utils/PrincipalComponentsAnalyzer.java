package gr.ntua.ece.cslab.panic.core.samplers.utils;

import gr.ntua.ece.cslab.panic.beans.containers.EigenSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
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
 * This class analyzes the dataset based on their principal components.
 *
 * @author Giannis Giannakopoulos
 */
public class PrincipalComponentsAnalyzer {

    private int rank;
    private String[] labels;

    private DenseMatrix64F eigenVectorMatrix,
            eigenValueMatrix;
    private DenseMatrix64F data,
            meanMatrix,
            deviationMatrix,
            varianceMatrix,
            correlationMatrix;

    // Constructor, getters and setters
    public PrincipalComponentsAnalyzer() {
    }

    public int getRank() {
        return rank;
    }

    public DenseMatrix64F getMeanMatrix() {
        return meanMatrix;
    }

    public DenseMatrix64F getDeviationMatrix() {
        return deviationMatrix;
    }

    public DenseMatrix64F getVarianceMatrix() {
        return varianceMatrix;
    }

    public DenseMatrix64F getCorrelationMatrix() {
        return correlationMatrix;
    }

    public String[] getLabels() {
        return this.labels;
    }

    // public interface
    public void setInputData(List<OutputSpacePoint> points) {
        int rows = points.size();
        Collection<String> keys = points.get(0).getInputSpacePoint().getKeysAsCollection();
        int columns = keys.size() + 1;
        this.labels = new String[columns];

        int index = 0;
        for (String s : keys) {
            this.labels[index++] = s;
        }
        this.labels[index] = points.get(0).getKey();

        this.data = new DenseMatrix64F(rows, columns);
        this.meanMatrix = new DenseMatrix64F(1, columns);
        this.rank = (rows > columns ? columns : rows);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < labels.length - 1; j++) {
                this.data.set(i, j, points.get(i).getInputSpacePoint().getValue(this.labels[j]));
                this.meanMatrix.add(0, j, this.data.get(i, j));
            }

            this.data.set(i, columns - 1, points.get(i).getValue());
            this.meanMatrix.add(0, columns - 1, this.data.get(i, columns - 1));
        }
        for (int j = 0; j < columns; j++) {
            this.meanMatrix.div(j, this.data.numRows);
        }

        this.deviationMatrix = new DenseMatrix64F(1, columns);
        for (int j = 0; j < columns; j++) {
            double sum = 0;
            for (int i = 0; i < rows; i++) {
                sum += Math.pow(this.data.get(i, j) - this.meanMatrix.get(0, j), 2);
            }
            sum = Math.sqrt(sum);
            this.deviationMatrix.set(0, j, sum);
        }
    }

    /**
     * Calculates the Variance-Covariance matrix
     *
     * @throws Exception
     */
    public void calculateVarianceMatrix() throws Exception {
        if (this.data == null || this.data.numCols <= 0 || this.data.numRows <= 0) {
            throw new Exception("Data table not initialized!");
        }
        varianceMatrix = new DenseMatrix64F(rank, rank);
        for (int j1 = 0; j1 < rank; j1++) {
            for (int j2 = j1; j2 < rank; j2++) {
                double sum = 0.0;
                for (int i = 0; i < this.data.numRows; i++) {
                    sum
                            += (this.data.get(i, j1) - this.meanMatrix.get(j1))
                            * (this.data.get(i, j2) - this.meanMatrix.get(j2));
                }
                sum /= (this.data.numRows - 1);
                varianceMatrix.set(j1, j2, sum);
                varianceMatrix.set(j2, j1, sum);
            }
        }
    }

    /**
     * Calculates the Correlation matrix
     *
     * @throws Exception
     */
    public void calculateCorrelationMatrix() throws Exception {
        if (this.data == null || this.data.numCols <= 0 || this.data.numRows <= 0) {
            throw new Exception("Data table not initialized!");
        }
        if (this.varianceMatrix == null || this.varianceMatrix.numCols <= 0 || this.varianceMatrix.numRows <= 0) {
            throw new Exception("Variance matrix not initialized!");
        }

        correlationMatrix = new DenseMatrix64F(rank, rank);
        for (int j1 = 0; j1 < rank; j1++) {
            for (int j2 = j1; j2 < rank; j2++) {
                correlationMatrix.set(j1, j2, this.varianceMatrix.get(j1, j2)
                        / (this.deviationMatrix.get(0, j1) * this.deviationMatrix.get(0, j2)));
                correlationMatrix.set(j2, j1, this.varianceMatrix.get(j1, j2)
                        / (this.deviationMatrix.get(0, j1) * this.deviationMatrix.get(0, j2)));
            }
        }

    }

    /**
     * Calculates the Principal Components based on the Variance matrix
     */
    public void calculateBaseWithVarianceMatrix() {
        this.calculateBase(this.varianceMatrix);
    }

    /**
     * Calculates the Principal Components based on the Correlation matrix.
     */
    public void calculateBaseWithCorrelationMatrix() {
        this.calculateBase(this.correlationMatrix);
    }

    /**
     * Calculates the Principal Components based on the original Data matrix.
     */
    public void calculateBaseWithDataMatrix() {
        this.calculateBase(this.data);
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

    /**
     * Returns the EigenVector of the specified order
     *
     * @param order The order of the EigenVector to be returned
     * @return The EigenVector
     */
    public EigenSpacePoint getEigenVector(int order) {
        return new EigenSpacePoint(CommonOps.extractRow(eigenVectorMatrix, order, null).getData());
    }

    /**
     * Transforms an OutputSpacePoint to an EigenSpace point based on the basis
     * calculated before.
     *
     * @param outputSpacePoint
     * @return
     */
    public EigenSpacePoint getEigenSpacePoint(OutputSpacePoint outputSpacePoint) {
        double[] value = outputSpacePoint.getDoubles();
        DenseMatrix64F pointMatrix = DenseMatrix64F.wrap(value.length, 1, value);
        DenseMatrix64F transMean = new DenseMatrix64F(this.meanMatrix.numCols, this.meanMatrix.numRows);
        CommonOps.transpose(this.meanMatrix, transMean);
        CommonOps.subtract(pointMatrix, transMean, pointMatrix);
        DenseMatrix64F result = new DenseMatrix64F(value.length, 1);
        CommonOps.mult(this.eigenVectorMatrix, pointMatrix, result);

        EigenSpacePoint point = new EigenSpacePoint();
        point.setData(result.getData());
        point.setKeys(outputSpacePoint);
        return point;
    }

    /**
     * Transforms an OutputSpacePoint to an EigenSpace point based on the basis
     * calculated before keeping only the first numComponents Principal
     * Components.
     *
     * @param outputSpacePoint
     * @param numComponents
     * @return
     */
    public EigenSpacePoint getEigenSpacePoint(OutputSpacePoint outputSpacePoint, int numComponents) throws Exception {
        if (numComponents > rank || numComponents < 1) {
            throw new Exception("numComponents should be between 1 and rank");
        }

        double[] value = outputSpacePoint.getDoubles();
        DenseMatrix64F pointMatrix = DenseMatrix64F.wrap(value.length, 1, value);

        DenseMatrix64F transMean = new DenseMatrix64F(this.meanMatrix.numCols, this.meanMatrix.numRows);
        CommonOps.transpose(this.meanMatrix, transMean);
        CommonOps.subtract(pointMatrix, transMean, pointMatrix);
        DenseMatrix64F result = new DenseMatrix64F(value.length, 1);
        CommonOps.mult(this.eigenVectorMatrix, pointMatrix, result);

        double[] resultArray = new double[numComponents];
        int i = 0;
        for (double d : result.getData()) {
            resultArray[i++] = d;
            if (i == numComponents) {
                break;
            }
        }

        EigenSpacePoint point = new EigenSpacePoint();
        point.setData(resultArray);
        point.setKeys(outputSpacePoint);
        return point;
    }

    public double getEigenValueScore(int order) {
        double sum = 0.0;
        for (int i = 0; i < this.getRank(); i++) {
            sum += this.getEigenValue(i);
        }
        return this.getEigenValue(order) / sum;
    }

    public double getEigenValueAggregatedScore(int order) {
        double globalSum = 0.0, partialSum = 0.0;
        for (int i = 0; i < this.getRank(); i++) {
            globalSum += this.getEigenValue(i);
            if (i <= order) {
                partialSum += this.getEigenValue(i);
            }
        }
        return partialSum / globalSum;
    }

    /**
     * Method that returns the loadings of the analysis for a specific principal
     * component and a specific input dimension.
     *
     * @param princinalComponentOrder
     * @param dimension
     * @return
     */
    public double getLoading(int princinalComponentOrder, int dimension) {
        return this.getEigenVector(princinalComponentOrder).getData()[dimension - 0];
    }

    /**
     * Returns the weight of each PC, sorted by their id.
     *
     * @return
     */
    public double[] getPCWeights() {
        double[] weights = new double[rank];
        double sum = 0.0;
        for (int i = 0; i < this.getRank(); i++) {
            sum += this.getEigenValue(i);
        }
        for (int i = 0; i < this.getRank(); i++) {
            weights[i] = this.getEigenValue(i) / sum;
        }

        return weights;
    }

    /**
     * Returns the weight of each PC, upto the specified order.
     *
     * @param components
     * @return
     */
    public double[] getPCWeights(int components) {
        double[] primary = this.getPCWeights();
        double[] result = new double[(components < primary.length ? components : primary.length)];
        System.arraycopy(primary, 0, result, 0, result.length);
        return result;
    }

    public LoadingsAnalyzer getLoadingsAnalyzer(int principalComponents) {
        LoadingsAnalyzer analyzer = new LoadingsAnalyzer();
        double[][] loadings = new double[rank][rank];
        for (int i = 0; i < rank; i++) {
            for (int j = 0; j < principalComponents; j++) {
                loadings[i][j] = this.getLoading(j, i);
            }
        }
        analyzer.setLoadings(loadings);
        analyzer.setDimensionLabels(labels);
        return analyzer;
    }

    // utilities and helper methods
    private void calculateBase(DenseMatrix64F matrixToDecompose) {
        SingularValueDecomposition<DenseMatrix64F> svd
                = DecompositionFactory.svd(
                        matrixToDecompose.numRows,
                        matrixToDecompose.numCols,
                        false,
                        true,
                        false);
        svd.decompose(matrixToDecompose);
        DenseMatrix64F W, Vt;
        Vt = svd.getV(null, true);
        W = svd.getW(null);
        SingularOps.descendingOrder(null, false, W, Vt, true);
        Vt.reshape(this.rank, this.rank);
        W.reshape(this.rank, this.rank);
        this.eigenValueMatrix = W;
        this.eigenVectorMatrix = Vt;
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

        comps.calculateBaseWithDataMatrix();
//        comps.calculateBaseWithVarianceMatrix();
//        comps.calculateBaseWithCorrelationMatrix();

        Integer numberOfEigenVectorToPrint = 2;
        Integer count = comps.getEigenVector(0).getData().length;
        String[] keys = comps.getLabels();
//        System.err.println("PC1\tPC2\n");
        for (int i = 0; i < count; i++) {
            // the zeros are used for gnuplot usage
            System.out.print(keys[i]+"\t0\t0\t");
            for (int j = 0; j < numberOfEigenVectorToPrint; j++) {
                System.out.print(comps.getEigenVector(j).getData()[i]+"\t");
            }
            System.out.println("");
        }
        
        LoadingsAnalyzer anal = comps.getLoadingsAnalyzer(numberOfEigenVectorToPrint);
        anal.setPcWeights(comps.getPCWeights());
//        System.err.println(anal.getPcWeights()[0]);
        System.err.println("Loadings\n"+anal);
        
        int i=0;
        System.err.println("Distances");
        for(String s:anal.getDimensionLabels()) {
            System.err.format("%s:\t%.5f\n", s, anal.getDistance(i++));
        }
        
        System.err.println("Angles");
        i=0;
        for(String s:keys) {
            System.err.format("%s:\t%.5f\n", s, anal.getAngle(i++));
        }
//        System.err.println("EigenValues info");
//        for(int i=0;i<comps.getRank();i++) {
//            System.err.format("%d\t%.5f\t%.5f\t%.5f\n", i, comps.getEigenValue(i), comps.getEigenValueScore(i),comps.getEigenValueAggregatedScore(i));
//        }
//        
//        for(int i=0;i<comps.getRank();i++) {
//            System.out.print((i+1)+"\t");
//            for(int j=0;j<2;j++) {
//                System.out.print(comps.getEigenVector(j).getData()[i]+"\t");
//            }
//            System.out.println("");
//        }
//        
//        System.out.println("");
//        System.out.println("");
//        
//        for(OutputSpacePoint p :points) {
//            System.out.println(comps.getEigenSpacePoint(p, 2).toStringCSV());
//        }
    }
}
