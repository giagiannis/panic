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

package gr.ntua.ece.cslab.panic.core.fresh.budget;

import gr.ntua.ece.cslab.panic.core.fresh.tree.DecisionTree;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeNode;

import java.util.Properties;

/**
 * Tree budget distributes equally budget according to the type:
 *  <ul>
 *      <li>coefficient</li>
 *      <li>length</li>
 *  </ul>
 * Created by Giannis Giannakopoulos on 2/18/16.
 */
public class TreeBudget extends Budget {
    private final double coefficient;
    private final int treeLength;

    private double perLevelConstant;
    public TreeBudget(DecisionTree tree, Properties properties, Integer totalBudget) {
        super(tree, properties, totalBudget);
        this.coefficient = new Double(this.properties.getProperty("coefficient"));
        this.treeLength = new Integer(this.properties.getProperty("length"));
//        this.budget = new Integer(this.properties.getProperty("points"));
    }

    @Override
    public void configure() {
        double sum =0.0;
        for(int i=0;i<this.treeLength;i++) {
            sum+=Math.pow(this.coefficient, i);
        }
        this.perLevelConstant = this.totalBudget/sum;
    }

    @Override
    public int estimate(DecisionTreeNode node) {
        int level = 0;
        DecisionTreeNode n = node;
        while(n.getFather()!=null){
            n = n.getFather();
            level+=1;
        }
        return (int) (this.getLevelBudget(level)/Math.pow(2, level));
    }

    @Override
    public int estimate(DecisionTreeNode node, DecisionTree tree) {
        return this.estimate(node);
    }

    private int getLevelBudget(int level) {
        return (int) Math.round(Math.pow(this.coefficient,level)  * this.perLevelConstant);
    }
}
