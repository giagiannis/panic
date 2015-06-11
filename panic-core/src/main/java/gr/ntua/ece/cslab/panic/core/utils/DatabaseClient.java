package gr.ntua.ece.cslab.panic.core.utils;

import gr.ntua.ece.cslab.panic.core.containers.beans.InputSpacePoint;
import gr.ntua.ece.cslab.panic.core.containers.beans.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.containers.beans.lists.InputSpacePointList;
import gr.ntua.ece.cslab.panic.core.containers.beans.lists.OutputSpacePointList;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class used by the client classes to write the results into an sqlite
 * database.
 *
 * @author Giannis Giannakopoulos
 */
public class DatabaseClient {

    private String databaseName;
    private Connection connection;

    // constructor, getters and setters
    public DatabaseClient() {
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    // methods to manage connections and DB
    /**
     * Opens a new connection to the specified database.
     *
     * @return
     */
    public boolean openConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.databaseName);
            this.connection.setAutoCommit(false);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(DatabaseClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    /**
     * Close the connection.
     *
     * @return
     */
    public boolean closeConnection() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    // db API
    /**
     * Inserts a new experiment into the database
     *
     * @param samplingRate
     * @param inputFile
     * @return the id of the newly created experiment
     */
    public Integer insertExperiment(double samplingRate, String inputFile) {
        try {
            String sql = "INSERT INTO experiments "
                    + "(experiment_date, experiment_time, sampling_rate, input_file) "
                    + "VALUES "
                    + "(DATE(), TIME(), %.5f, '%s');";
            String sqlQuery = String.format(sql, samplingRate, inputFile);
            Statement stmt = this.connection.createStatement();
            stmt.executeUpdate(sqlQuery);
            stmt.getGeneratedKeys().next();
            int id = stmt.getGeneratedKeys().getInt(1);
            this.connection.commit();
            return id;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;
    }

    /**
     * Insert a new metric to the database.
     *
     * @param experimentId
     * @param model
     * @param sampler
     * @param mse
     * @param mae
     * @param deviation
     * @param r
     */
    public void insertExperimentMetrics(Integer experimentId, String model, String sampler, Double mse, Double mae, Double deviation, Double r) {
        try {
            Statement stmt = this.connection.createStatement();
            String sql = "INSERT INTO metrics"
                    + "(experiment_id,"
                    + "model, "
                    + "sampler, "
                    + "mean_square_error, "
                    + "mean_average_error, "
                    + "deviation, "
                    + "coefficient_of_determination) "
                    + "VALUES"
                    + "(%d, '%s', '%s', %.5f, %.5f, %.5f, %.5f)";
            String sqlQuery = String.format(sql, experimentId, model, sampler, mse, mae, deviation, r);
            stmt.executeUpdate(sqlQuery);
            stmt.close();
            this.connection.commit();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Inserts a BLOB containing the models picked by the sampler
     * @param experimentId
     * @param sampler
     * @param samples 
     */
    public void insertSampledPoints(Integer experimentId, String sampler, List<InputSpacePoint> samples) {
        String sql = "INSERT INTO samples (experiment_id, sampler, list) VALUES ( %d, '%s', ? )";
        String sqlFormatted = String.format(sql, experimentId, sampler);
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlFormatted);
            byte[] bytes = new InputSpacePointList(samples).getBytes();
            stmt.setBinaryStream(1, new ByteArrayInputStream(bytes), bytes.length);
            stmt.executeUpdate();
            stmt.close();
            this.connection.commit();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * Method used to insert new model predictions into the database. The predictions
     * have the form of a compressed byte array, containing all the predicted values
     * @param experimentId
     * @param model
     * @param sampler
     * @param points 
     */
    public void insertModelPredictions(Integer experimentId, String model, String sampler, List<OutputSpacePoint> points) {
        String sql = "INSERT INTO model_predictions (experiment_id, model, sampler, list) VALUES ( %d, '%s', '%s', ? )";
        String sqlFormatted = String.format(sql, experimentId, model, sampler);
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sqlFormatted);
            byte[] bytes = new OutputSpacePointList(points).getBytes();
            stmt.setBinaryStream(1, new ByteArrayInputStream(bytes), bytes.length);
            stmt.executeUpdate();
            stmt.close();
            this.connection.commit();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
