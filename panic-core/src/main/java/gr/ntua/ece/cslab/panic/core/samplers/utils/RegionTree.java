package gr.ntua.ece.cslab.panic.core.samplers.utils;

import gr.ntua.ece.cslab.panic.core.partitioners.RandomPartitioner;
import gr.ntua.ece.cslab.panic.core.utils.CSVFileManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * A binary tree containing the ordering of the deployment subspaces
 */
public class RegionTree {

    private RegionTreeNode root, currentNode;
    private List<RegionTreeNode> nodesToVisit=null;
    private int listIndex;

    public RegionTree() {
        this.nodesToVisit = new ArrayList<RegionTreeNode>();
        this.root = null;
        this.currentNode = null;
        this.listIndex = -1;
    }

    public RegionTreeNode addChild(RegionTreeNode newNode) {
        return this.addChild(this.currentNode, newNode);
    }
    public RegionTreeNode addChild(RegionTreeNode currentNode, RegionTreeNode newNode) {
        if(currentNode==null) {
            this.root = newNode;
            this.root.setLevel(0);
        } else {
            newNode.setLevel(currentNode.getLevel()+1);
            if (currentNode.getLeftChild() == null) {
                currentNode.setLeftChild(newNode);
            } else if (currentNode.getRightChild() == null) {
                currentNode.setRightChild(newNode);
            } else {
                System.err.println("Cannot append third child into a binary tree!!");
            }
        }
        this.nodesToVisit.add(newNode);
        return newNode;
    }

    /**
     * Adds a child under the specified node
     *
     * @param currentNode
     * @param region
     * @return
     */
    public RegionTreeNode addChild(RegionTreeNode currentNode, HashMap<String, List<Double>> region) {
        RegionTreeNode newNode = new RegionTreeNode();
        newNode.setRegion(region);
        newNode.setFather(currentNode);
        if (currentNode == null) {
            if (this.root != null) {
                System.err.println("Overwriting the root!");
            }
            this.root = (RegionTreeNode)newNode;
            this.root.setLevel(0);
        } else {
            newNode.setLevel(currentNode.getLevel() + 1);
            if (currentNode.getLeftChild() == null) {
                currentNode.setLeftChild(newNode);
            } else if (currentNode.getRightChild() == null) {
                currentNode.setRightChild(newNode);
            } else {
                System.err.println("Cannot append third child into a binary tree!!");
                System.exit(1);
            }
        }
        this.nodesToVisit.add(newNode);
        return newNode;
//		System.err.format("%50s added child %50s\n",currentNode,newNode);
    }

    /**
     * Adds a child under the currentNode of the structure
     *
     * @param region
     * @return 
     */
    public RegionTreeNode addChild(HashMap<String, List<Double>> region) {
        return this.addChild(this.currentNode, region);
    }

    /**
     * Returns the current {@link RegionTreeNode}
     *
     * @return
     */
    public RegionTreeNode getCurrent() {
        return this.currentNode;
    }

    /**
     * Moves the pointer to the next {@link RegionTreeNode}
     */
    public void next() {
        this.listIndex += 1;
        if(this.listIndex<this.nodesToVisit.size())
            this.currentNode = this.nodesToVisit.get(this.listIndex);
        else
            this.currentNode = null;
    }
    
//    private void clearNotVisited() {
//        for (int i = this.listIndex + 1; i < this.nodesToVisit.size(); i++) {
//            RegionTreeNode current = this.nodesToVisit.get(i);
//            if (current.getLoadingsAnalyzer() == null) {
//                RegionTreeNode father = current.getFather();
//                father.setLeftChild(null);
//                father.setRightChild(null);
//            }
//        }
//
//    }

    public List<RegionTreeNode> getNodesByLevel(int level) {
        List<RegionTreeNode> result = new LinkedList<>();
        for (RegionTreeNode n : this.nodesToVisit) {
            if (n.getLevel() == level) {
                result.add(n);
            }
        }
        return result;
    }

    /**
     * Prints the content of the tree in a DFS order
     *
     * @return
     */
    public List<RegionTreeNode> getDFSOrdering() {
        List<RegionTreeNode> order = new LinkedList<>();
        LinkedList<RegionTreeNode> toVisit = new LinkedList<>();
        toVisit.add(root);

        while (!toVisit.isEmpty()) {
            RegionTreeNode current = toVisit.pop();
            if (true) {
                order.add(current);
                if (current.getLeftChild() != null) {
                    toVisit.push(current.getLeftChild());
                }
                if (current.getRightChild() != null) {
                    toVisit.push(current.getRightChild());
                }
            }
        }
        return order;
    }

    /**
     * Prints the content of the tree in a BFS order
     *
     * @return
     */
    /**
     * Prints the content of the tree in a DFS order
     *
     * @return
     */
    public List<RegionTreeNode> getBFSOrdering() {
        List<RegionTreeNode> order = new LinkedList<>();
        LinkedList<RegionTreeNode> toVisit = new LinkedList<>();
        toVisit.add(root);

        while (!toVisit.isEmpty()) {
            RegionTreeNode current = toVisit.pop();
            if (true) {
                order.add(current);
                if (current.getLeftChild() != null) {
                    toVisit.add(current.getLeftChild());
                }
                if (current.getRightChild() != null) {
                    toVisit.add(current.getRightChild());
                }
            }
        }
        return order;
    }

    public List<RegionTreeNode> getLeaves() {
        List<RegionTreeNode> order = new LinkedList<>();
        LinkedList<RegionTreeNode> toVisit = new LinkedList<>();
        toVisit.add(root);

        while (!toVisit.isEmpty()) {
            RegionTreeNode current = toVisit.pop();
            if (true) {
                if (current.isLeaf()) {
                    order.add(current);
                }
                if (current.getLeftChild() != null) {
                    toVisit.add(current.getLeftChild());
                }
                if (current.getRightChild() != null) {
                    toVisit.add(current.getRightChild());
                }
            }
        }
        return order;
    }
    
    public List<HashMap<String, List<Double>>> getLeafRegions() {
        List<HashMap<String, List<Double>>> leaves = new LinkedList<>();
        for (RegionTreeNode n : this.getLeaves()) {
            leaves.add(n.getRegion());
        }
        return leaves;
    }

    public static void main(String[] args) {
        CSVFileManager file = new CSVFileManager();
        file.setFilename(args[0]);

        RegionTree tree = new RegionTree();
        tree.addChild(file.getDimensionRanges());
        tree.next();

        for (int i = 1; i < 20; i++) {
            RandomPartitioner part = new RandomPartitioner();
            part.setRanges(tree.getCurrent().getRegion());
            part.configurePartitioner();
            
            if(new Random().nextInt()%5!=0) {
                tree.addChild(part.getHigherRegion());
                tree.addChild(part.getLowerRegion());
            }
            tree.next();
            if(tree.getCurrent()==null)
                break;
        }

        for (RegionTreeNode n : tree.getDFSOrdering()) {
            for (int i = 0; i < n.getLevel(); i++) {
                System.out.print("    ");
            }
            System.out.println(n);
        }

        System.out.println("===========================");

        for (RegionTreeNode n : tree.getBFSOrdering()) {
            for (int i = 0; i < n.getLevel(); i++) {
                System.out.print("    ");
            }
            System.out.println(n);
        }

        System.out.println("===========================");

        for (RegionTreeNode n : tree.getLeaves()) {
            for (int i = 0; i < n.getLevel(); i++) {
                System.out.print("    ");
            }
            System.out.println(n);
        }
    }
}
