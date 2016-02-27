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
package gr.ntua.ece.cslab.panic.core.utils;

import au.com.bytecode.opencsv.CSVReader;
import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Giannis Giannakopoulos
 */
public class CSVFileManager {

    private String filename;
    private int numberOfInputDimensions;
    private int outputDimensionIndex;
    private char delimiter = '\t';
    private String[] dimensionNames;
    private int quoteLines = 0;
    private Set<InputSpacePoint> unavailablePoints;

    private HashMap<InputSpacePoint, OutputSpacePoint> hashMap;

    public CSVFileManager() {
        this.unavailablePoints = new HashSet<>();
    }

    public CSVFileManager(String filename) {
        this.setFilename(filename);
        this.unavailablePoints = new HashSet<>();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String buffer = "";
            while (reader.ready()) {
                buffer = reader.readLine();
                if (!buffer.trim().startsWith("#") && buffer.trim().length() != 0)
                    break;
                else
                    quoteLines += 1;
            }
            this.dimensionNames = buffer.split("\t");
            this.numberOfInputDimensions = this.dimensionNames.length - 1;
            this.outputDimensionIndex = this.dimensionNames.length - 1;
            this.hashMap = new HashMap<>();
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(CSVFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Default number equals to number of column to CSV -1.
     *
     * @return number of input dimensions
     */
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

    /**
     * Default number equals to number of columns to CSV -1.
     *
     * @return the index of output dimension
     */
    public int getOutputDimensionIndex() {
        return outputDimensionIndex;
    }

    public void setOutputDimensionIndex(int outputDimensionIndex) {
        this.outputDimensionIndex = outputDimensionIndex;
    }

    public List<OutputSpacePoint> getOutputSpacePoints() {
        List<OutputSpacePoint> results = null;
        try {
            CSVReader reader = new CSVReader(new FileReader(filename), delimiter, '#', this.quoteLines + 1);
            String[] line;
            results = new LinkedList<>();
            while ((line = reader.readNext()) != null) {
                boolean sanityCheck = true;
                OutputSpacePoint point = new OutputSpacePoint();
                point.setInputSpacePoint(new InputSpacePoint());
                for (int i = 0; i < numberOfInputDimensions; i++) {
                    point.getInputSpacePoint().addDimension(this.dimensionNames[i], new Double(line[i]));
                }
                if (!line[outputDimensionIndex].equals("")) {
                    point.setValue(this.dimensionNames[this.dimensionNames.length - 1], new Double(line[outputDimensionIndex]));
                    results.add(point);
                    this.hashMap.put(point.getInputSpacePoint(), point);
                } else {
                    this.unavailablePoints.add(point.getInputSpacePoint());
                }
            }
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(CSVFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return results;
    }

    public List<InputSpacePoint> getInputSpacePoints() {
        List<OutputSpacePoint> points = this.getOutputSpacePoints();
        List<InputSpacePoint> results = new LinkedList<>();
        for (OutputSpacePoint p : points) {
            results.add(p.getInputSpacePoint());
        }
        return results;
    }

    /**
     * Method returning the district values of each dimension. Useful for
     * samplers.
     *
     * @return the dimension regions
     */
    public HashMap<String, List<Double>> getDimensionRanges() {
        HashMap<String, Set<Double>> temp = new HashMap<>();
        List<InputSpacePoint> points = this.getInputSpacePoints();
        for (InputSpacePoint p : points) {
            for (String key : p.getKeysAsCollection()) {
                if (temp.get(key) == null) {
                    temp.put(key, new HashSet<>());
                }
                temp.get(key).add(p.getValue(key));
            }
        }
        HashMap<String, List<Double>> results = new HashMap<>();
        for (String s : temp.keySet()) {
            results.put(s, new LinkedList<>(temp.get(s)));
        }
        return results;
    }

    public OutputSpacePoint getActualValue(InputSpacePoint point) {
        return this.hashMap.get(point);
    }

    public Set<InputSpacePoint> getUnavailablePoints() {
        return unavailablePoints;
    }
}
