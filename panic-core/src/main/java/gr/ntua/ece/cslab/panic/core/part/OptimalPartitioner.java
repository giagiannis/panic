package gr.ntua.ece.cslab.panic.core.part;

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.eval.Metrics;
import gr.ntua.ece.cslab.panic.core.models.IsoRegression;
import gr.ntua.ece.cslab.panic.core.utils.CSVFileManager;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that receives a list of points and returns two disjoint sets of space points.
 * Created by Giannis Giannakopoulos on 1/12/16.
 */
public class OptimalPartitioner {
    private List<OutputSpacePoint> outputSpacePoints;
    private List<OutputSpacePoint> leftSublist, rightSublist;
    private int lowerSetSizeLimit = 1;

    public OptimalPartitioner() {

    }

    public List<OutputSpacePoint> getOutputSpacePoints() {
        return outputSpacePoints;
    }

    public void setOutputSpacePoints(List<OutputSpacePoint> outputSpacePoints) {
        this.outputSpacePoints = outputSpacePoints;
    }

    public List<OutputSpacePoint> getLeftSublist() {
        return leftSublist;
    }

    public List<OutputSpacePoint> getRightSublist() {
        return rightSublist;
    }

    public int getLowerSetSizeLimit() {
        return lowerSetSizeLimit;
    }

    public void setLowerSetSizeLimit(int lowerSetSizeLimit) {
        this.lowerSetSizeLimit = lowerSetSizeLimit;
    }

    public void configure() {
        double maxIndex = Math.pow(2.0, (double)this.outputSpacePoints.size());
        Solution optimal = null;
        for(int i=0;i<maxIndex;i++) {
            Solution s = this.checkCombination(i);
            if(s!=null && (optimal ==null || s.getError()<=optimal.getError()))
                optimal = s;
        }
        this.leftSublist = optimal.getLeftSublist();
        this.rightSublist = optimal.getRightSublist();
    }

    private Solution checkCombination(Integer id) {
        // sanity check
        String binaryString = this.translateIndex(id);
        int countZeros = 0, countOnes = 0;
        for(char c:binaryString.toCharArray()) {
            if(c == '1')
                countOnes++;
            else
                countZeros++;
        }
        if(countOnes<this.lowerSetSizeLimit||countZeros<this.lowerSetSizeLimit) {
            return null;
        }

        // prepare lists
        List<OutputSpacePoint> rightList = new LinkedList<>(), leftList = new LinkedList<>();
        int i=0;
        for(char c:binaryString.toCharArray()) {
            (c=='1'?leftList:rightList).add(this.getOutputSpacePoints().get(i++));
        }

        // train models
        double error = 0.0;

        IsoRegression model = new IsoRegression();
        model.configureClassifier();
        try {
            model.feed(rightList);
            model.train();
        } catch (Exception e) {
            e.printStackTrace();
        }
        error+= Metrics.meanSquareError(model,rightList);
//        error+= CrossValidation.meanSquareError(model.getClass(),rightList);

        model = new IsoRegression();
        model.configureClassifier();
        try {
            model.feed(leftList);
            model.train();
        } catch (Exception e) {
            e.printStackTrace();
        }
        error+= Metrics.meanSquareError(model,leftList);
//        error+= CrossValidation.meanSquareError(model.getClass(),leftList);

        return new Solution(leftList, rightList, error);
    }


    private String translateIndex(int id) {
        String binaryString= Integer.toBinaryString(id);
        String padding = new String();
        for(int i=binaryString.length();i<this.outputSpacePoints.size();i++) {
            padding+="0";
        }
        return padding+binaryString;
    }

    private Integer translateIndex(String id) {
        return Integer.parseInt(id,2);
    }

    public static void main(String[] args) {
        CSVFileManager manager = new CSVFileManager();
        manager.setFilename(args[0]);
        List<OutputSpacePoint> o = manager.getOutputSpacePoints();
        Collections.shuffle(o);
        o = o.subList(0,20);

        OptimalPartitioner part = new OptimalPartitioner();
        part.setOutputSpacePoints(o);
        part.setLowerSetSizeLimit(args.length>2?new Integer(args[1]):1);
        part.configure();

        System.out.println(part.getLeftSublist());
        System.out.println(part.getRightSublist());
    }


    private class Solution {
        private List<OutputSpacePoint> leftSublist, rightSublist;
        private double error;

        public Solution() {

        }

        public Solution(List<OutputSpacePoint> leftSublist, List<OutputSpacePoint> rightSublist, double error) {
            this.leftSublist = leftSublist;
            this.rightSublist = rightSublist;
            this.error = error;
        }

        public List<OutputSpacePoint> getLeftSublist() {
            return leftSublist;
        }

        public void setLeftSublist(List<OutputSpacePoint> leftSublist) {
            this.leftSublist = leftSublist;
        }

        public List<OutputSpacePoint> getRightSublist() {
            return rightSublist;
        }

        public void setRightSublist(List<OutputSpacePoint> rightSublist) {
            this.rightSublist = rightSublist;
        }

        public double getError() {
            return error;
        }

        public void setError(double error) {
            this.error = error;
        }

        @Override
        public String toString() {
            return String.format("(%d, %d) [%.5f]", leftSublist.size(), rightSublist.size(), error);
        }
    }
}
