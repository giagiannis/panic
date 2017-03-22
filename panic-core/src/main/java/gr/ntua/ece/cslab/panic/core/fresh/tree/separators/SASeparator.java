/*
 * Copyright 2017 Giannis Giannakopoulos
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

package gr.ntua.ece.cslab.panic.core.fresh.tree.separators;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.fresh.structs.DeploymentSpace;
import gr.ntua.ece.cslab.panic.core.fresh.tree.line.SplitLine;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeTestNode;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SASeparator utilizes a Simulated Annealing formulation
 * Created by Giannis Giannakopoulos on 3/20/17.
 */
public class SASeparator implements Separator{

    private List<String> keyOrdering;
    private DecisionTreeLeafNode input;
    private DecisionTreeTestNode result;
    private String scriptPath;
    /**
     * Default contructor of the class that holds
     * @param node
     */
    public SASeparator(DecisionTreeLeafNode node) {
        this.input = node;
        if(this.input.getPoints().size()>0) {
            this.keyOrdering = this.input.getPoints().get(0).getInputSpacePoint().getKeysAsCollection().parallelStream().collect(Collectors.toList());
        }
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    @Override
    public void separate() {
        if (this.input.getPoints().size()<=0) {
            return;
        }
        String tempFileName="/tmp/file-"+Integer.toHexString(new Random().nextInt())+".csv";
        try {
            this.serializePointsToFile(tempFileName);
            SplitLine line = this.executeScript(tempFileName);
            List<OutputSpacePoint> left =
                    this.input.getPoints().
                            parallelStream().
                            filter(u -> line.lessOrEqual(u.getInputSpacePoint())).
                            collect(Collectors.toList());
            List<OutputSpacePoint> right =
                    this.input.getPoints().
                            parallelStream().
                            filter(u -> !line.lessOrEqual(u.getInputSpacePoint())).
                            collect(Collectors.toList());
            Set<InputSpacePoint> leftDS = this.input.getDeploymentSpace().getPoints().
                    parallelStream().
                    filter(u-> line.lessOrEqual(u)).
                    collect(Collectors.toSet());
            Set<InputSpacePoint> rightDS = this.input.getDeploymentSpace().getPoints().
                    parallelStream().
                    filter(u->!line.lessOrEqual(u)).
                    collect(Collectors.toSet());
            this.result = new DecisionTreeTestNode(
                    line,   this.input.getDeploymentSpace(),
                    new DecisionTreeLeafNode(left,  new DeploymentSpace(leftDS)),
                    new DecisionTreeLeafNode(right, new DeploymentSpace(rightDS)));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new File(tempFileName).delete();
    }

    @Override
    public DecisionTreeTestNode getResult() {
        return this.result;
    }

    /**
     *
     * @param flag
     */
    @Override
    public void setAxisParallelSplits(boolean flag) {
        // does nothing
    }

    /**
     * Method used to serialize the deployed Outputspace points to a file in order to execute SA.
     * @param filename the name of the file to write the points to
     */
    private void serializePointsToFile(String filename) throws FileNotFoundException {
        PrintStream out = new PrintStream(filename);
        for(String key: this.keyOrdering) {
            out.printf("%s\t", key);
        }
        out.printf("%s\n", "y");
        for (OutputSpacePoint p : this.input.getPoints()) {
            for(String key: this.keyOrdering) {
                out.printf("%.5f\t", p.getInputSpacePoint().getValue(key));
            }
            out.printf("%.5f\n", p.getValue());
}
        out.flush();
        out.close();
    }

    private SplitLine executeScript(String fileName) throws IOException, InterruptedException {
        Process p = new ProcessBuilder(this.scriptPath, fileName).start();
        int exitStatus = p.waitFor();
        InputStream stream = p.getInputStream();
        if (exitStatus != 0) {
            stream = p.getErrorStream();
        }
        StringBuffer buf = new StringBuffer();
        int current;
        while ((current = stream.read()) > 0) {
            buf.append((char) current);
        }
        List<Double> outputValues =Arrays.stream(buf.toString().trim().split(" ")).map(u->new Double(u)).collect(Collectors.toList());
        Map<String, Double> lineCoefficients = new HashMap<>();
        for(int i=0;i<this.keyOrdering.size();i++) {
            lineCoefficients.put(this.keyOrdering.get(i), outputValues.get(i));
        }
        lineCoefficients.put("constant", outputValues.get(this.keyOrdering.size()));
        return new SplitLine(lineCoefficients);
    }
}
