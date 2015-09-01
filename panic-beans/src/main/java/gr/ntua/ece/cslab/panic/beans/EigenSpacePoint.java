package gr.ntua.ece.cslab.panic.beans;


/**
 * EigenSpacePoint holds data from the eigenspace
 * @author Giannis Giannakopoulos
 */
public class EigenSpacePoint {
    
    private double[] data;
    private String[] keys;

    public EigenSpacePoint() {
    }

    public EigenSpacePoint(double[] data) {
        this.data = data;
    }
    
    public double[] getData() {
        return data;
    }

    public void setData(double[] data) {
        this.data = data;
    }

    /**
     * Get the keys of the respective OutputSpacePoints
     * @return 
     */
    public String[] getKeys() {
        return keys;
    }

    /**
     * Set the keys of the respective OutputSpacePoint
     * @param keys 
     */
    public void setKeys(String[] keys) {
        this.keys = keys;
    }
    
    public void setKeys(OutputSpacePoint point) {
        this.keys = new String[point.getInputSpacePoint().getKeysAsCollection().size()+1];
        int index=0;
        for(String k : point.getInputSpacePoint().getKeysAsCollection())
            this.keys[index++] = k;
        this.keys[index++] = point.getKey();
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[");
        for(int i=0;i<this.data.length-1;i++) {
            buffer.append(String.format("%.2f", this.data[i]));
            buffer.append(", ");
        }
        buffer.append(String.format("%.2f", this.data[this.data.length-1]));
        buffer.append("]");
        return buffer.toString();
    }
    
    public String toStringCSV() {
        StringBuilder buffer = new StringBuilder();
//        buffer.append("[");
        for(int i=0;i<this.data.length-1;i++) {
            buffer.append(String.format("%.5f", this.data[i]));
            buffer.append("\t");
        }
        buffer.append(String.format("%.5f", this.data[this.data.length-1]));
//        buffer.append("]");
        return buffer.toString();
    }
    
    
}
