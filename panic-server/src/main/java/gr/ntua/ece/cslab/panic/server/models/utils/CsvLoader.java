/*
 * Copyright 2014 Giannis Giannakopoulos.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gr.ntua.ece.cslab.panic.server.models.utils;

import au.com.bytecode.opencsv.CSVReader;
import gr.ntua.ece.cslab.panic.server.containers.beans.InputSpacePoint;
import gr.ntua.ece.cslab.panic.server.containers.beans.OutputSpacePoint;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Giannis Giannakopoulos
 */
public class CsvLoader {

    private String filename;
    private int numberOfInputDimensions;
    private int outputDimensionIndex;
    private char delimiter = '\t';
    
    public CsvLoader() {
        
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getNumberOfInputDimensions() {
        return numberOfInputDimensions;
    }

    public void setNumberOfInputDimensions(int numberOfInputDimensions) {
        this.numberOfInputDimensions = numberOfInputDimensions;
    }

    public char getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(char delimiter) {
        this.delimiter = delimiter;
    }

    public int getOutputDimensionIndex() {
        return outputDimensionIndex;
    }

    public void setOutputDimensionIndex(int outputDimensionIndex) {
        this.outputDimensionIndex = outputDimensionIndex;
    }    
    
    public List<OutputSpacePoint> getOutputSpacePoints() {
        List<OutputSpacePoint> results = null;
        try {
            CSVReader reader = new CSVReader(new FileReader(filename), delimiter, '#', 1);
            String[] line;
            results = new LinkedList<>();
            while((line = reader.readNext())!=null) {
                OutputSpacePoint point = new OutputSpacePoint();
                point.setInputSpacePoint(new InputSpacePoint());
                for(int i=0;i<numberOfInputDimensions;i++)
                    point.getInputSpacePoint().addDimension("x"+(i+1), new Double(line[i]));
                point.setValue("y", new Double(line[outputDimensionIndex]));
                results.add(point);
            }
            reader.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return results;
    }
    
    public List<InputSpacePoint> getInputSpacePoints() {
        List<OutputSpacePoint> points = this.getOutputSpacePoints();
        List<InputSpacePoint> results = new LinkedList<>();
        for(OutputSpacePoint p :points)
            results.add(p.getInputSpacePoint());
        return results;
    }
    public static void main(String[] args) {
        CsvLoader loader = new CsvLoader();
        loader.setFilename(args[0]);
        loader.setNumberOfInputDimensions(1);
        loader.setOutputDimensionIndex(1);
        System.out.println(loader.getOutputSpacePoints());
    }
}
