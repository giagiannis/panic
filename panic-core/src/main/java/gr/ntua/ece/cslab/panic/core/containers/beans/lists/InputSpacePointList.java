package gr.ntua.ece.cslab.panic.core.containers.beans.lists;

import gr.ntua.ece.cslab.panic.core.containers.beans.InputSpacePoint;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * Ordering preserving list of InputSpacePoints
 * @author Giannis Giannakopoulos
 */
public class InputSpacePointList implements Serializable {
    private List<InputSpacePoint> list;

    // getters and setters
    public InputSpacePointList() {
        this.list  = new LinkedList<>();
    }
    
    public InputSpacePointList(List<InputSpacePoint> list) {
        this.list = list;
    }

    public List<InputSpacePoint> getList() {
        return list;
    }

    public void setList(List<InputSpacePoint> list) {
        this.list = list;
    }
    
    // serializers and deserializers
    public byte[] getBytes() {
        int count = 4;
        for(InputSpacePoint p : list) {
            count+=Integer.SIZE/8;
            count += p.getBytes().length;
        }
        
        ByteBuffer buffer = ByteBuffer.allocate(count);
        buffer.putInt(this.list.size());
        for(InputSpacePoint p: list) {
            byte[] point = p.getBytes();
            buffer.putInt(point.length);
            buffer.put(point);
        }
        return buffer.array();
    }
    
    public void parseBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int listSize = buffer.getInt();
        
        for(int i=0;i<listSize;i++) {
            int byteCount = buffer.getInt();
            byte[] ispBuffer = new byte[byteCount];
            buffer.get(ispBuffer);
            InputSpacePoint newISP = new InputSpacePoint();
            newISP.parseBytes(ispBuffer);
            this.list.add(newISP);
        }
    }

    @Override
    public String toString() {
        return this.list.toString();
    }
    
}
