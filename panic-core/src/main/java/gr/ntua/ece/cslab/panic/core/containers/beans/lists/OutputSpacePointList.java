package gr.ntua.ece.cslab.panic.core.containers.beans.lists;

import gr.ntua.ece.cslab.panic.core.containers.beans.OutputSpacePoint;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 *
 * @author Giannis Giannakopoulos
 */
public class OutputSpacePointList {

    private List<OutputSpacePoint> list;

    // getters and setters
    public OutputSpacePointList(List<OutputSpacePoint> list) {
        this.list = list;
    }

    public OutputSpacePointList() {
        this.list = new LinkedList<>();
    }

    public List<OutputSpacePoint> getList() {
        return list;
    }

    public void setList(List<OutputSpacePoint> list) {
        this.list = list;
    }

    //serializers and deserializers
    public byte[] getBytes() {
        
        //estimate byte array
        int count = 4;
        for (OutputSpacePoint p : list) {
            count += Integer.SIZE / 8;
            count += p.getBytes().length;
        }

        ByteBuffer buffer = ByteBuffer.allocate(count);
        buffer.putInt(this.list.size());
        for (OutputSpacePoint p : list) {
            byte[] point = p.getBytes();
            buffer.putInt(point.length);
            buffer.put(point);
        }
        byte[] uncompressedArray = buffer.array();

        // compress byte array
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        deflater.setInput(uncompressedArray);
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream(uncompressedArray.length);
        deflater.finish();
        byte[] tempBuffer = new byte[1024];
        while(!deflater.finished()) {
            int length = deflater.deflate(tempBuffer);
            stream.write(tempBuffer, 0, length);
        }
        try {
            stream.close();
        } catch (IOException ex) {
            Logger.getLogger(OutputSpacePointList.class.getName()).log(Level.SEVERE, null, ex);
        }
        deflater.end();
        byte[] compressedData = stream.toByteArray();
        
        
        return compressedData;
    }

    public void parseBytes(byte[] bytes)  {
        // decomporess byte array
        Inflater inflater = new Inflater();
        inflater.setInput(bytes);
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream(bytes.length);
        byte[] tempBuffer = new byte[1024];
        while(!inflater.finished()) {
            try {
                int length = inflater.inflate(tempBuffer);
                stream.write(tempBuffer, 0, length);
            } catch (DataFormatException ex) {
                Logger.getLogger(OutputSpacePointList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            stream.close();
        } catch (IOException ex) {
            Logger.getLogger(OutputSpacePointList.class.getName()).log(Level.SEVERE, null, ex);
        }
        byte[] uncompressedBytes = stream.toByteArray();
        inflater.end();
        
        // deserialize raw byte array
        ByteBuffer buffer = ByteBuffer.wrap(uncompressedBytes);
        int listSize = buffer.getInt();

        for (int i = 0; i < listSize; i++) {
            int byteCount = buffer.getInt();
            byte[] ospBuffer = new byte[byteCount];
            buffer.get(ospBuffer);
            OutputSpacePoint newOSP = new OutputSpacePoint();
            newOSP.parseBytes(ospBuffer);
            this.list.add(newOSP);
        }
    }

    @Override
    public String toString() {
        return this.list.toString();
    }
}
