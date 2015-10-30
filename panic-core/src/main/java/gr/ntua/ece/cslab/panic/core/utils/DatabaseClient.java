package gr.ntua.ece.cslab.panic.core.utils;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.lists.InputSpacePointList;
import gr.ntua.ece.cslab.panic.beans.lists.OutputSpacePointList;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
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

	private String username, password, databaseHost = "localhost";

	private String url;

	// constructor, getters and setters
	public DatabaseClient() {
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDatabaseHost() {
		return databaseHost;
	}

	public void setDatabaseHost(String databaseHost) {
		this.databaseHost = databaseHost;
	}

	// methods to manage connections and DB
	/**
	 * Opens a new connection to the specified database.
	 *
	 * @return
	 */
	public boolean openConnection() {
		try {
			if ((this.username == null || this.username.equals(""))
					&& (this.password == null || this.password.equals(""))) {
				Class.forName("org.sqlite.JDBC");
				this.url = "jdbc:sqlite:" + this.databaseName;
				this.connection = DriverManager.getConnection(this.url);
				this.connection.setAutoCommit(false);
			} else { // MySQL
				this.url = "jdbc:mysql://" + databaseHost + ":3306/" + databaseName + "?user=" + this.username
						+ "&password=" + this.password;
				Class.forName("com.mysql.jdbc.Driver");
				this.connection = DriverManager.getConnection(this.url);
				this.connection.setAutoCommit(false);
			}
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
	 * @param configurations
	 * @return the id of the newly created experiment
	 */
	public Integer insertExperiment(double samplingRate, String inputFile, String configurations) {
		try {
			Statement stmt = this.connection.createStatement();
			if (this.url.contains("mysql")) {
				String sql = "INSERT INTO experiments "
						+ "(experiment_date, experiment_time, sampling_rate, input_file, configurations) " + "VALUES "
						+ "(DATE(NOW()), TIME(NOW()), %.5f, '%s', '%s');";
				String sqlQuery = String.format(sql, samplingRate, inputFile, configurations);
				stmt.executeUpdate(sqlQuery, Statement.RETURN_GENERATED_KEYS);
			} else {
				String sql = "INSERT INTO experiments "
						+ "(experiment_date, experiment_time, sampling_rate, input_file, configurations) " + "VALUES "
						+ "(DATE(), TIME(), %.5f, '%s', '%s');";
				String sqlQuery = String.format(sql, samplingRate, inputFile, configurations);
				stmt.executeUpdate(sqlQuery);
			}
			ResultSet rs = stmt.getGeneratedKeys();
			rs.next();
			int id = rs.getInt(1);
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
	public void insertExperimentMetrics(Integer experimentId, String model, String sampler, Double mse, Double mae,
			Double deviation, Double r) {
		try {
			Statement stmt = this.connection.createStatement();
			String sql = "INSERT INTO metrics" + "(experiment_id," + "model, " + "sampler, " + "mean_square_error, "
					+ "mean_average_error, " + "deviation, " + "coefficient_of_determination) " + "VALUES"
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
	 *
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
	 * Method used to insert new model predictions into the database. The
	 * predictions have the form of a compressed byte array, containing all the
	 * predicted values
	 *
	 * @param experimentId
	 * @param model
	 * @param sampler
	 * @param points
	 */
	public void insertModelPredictions(Integer experimentId, String model, String sampler,
			List<OutputSpacePoint> points) {
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

	/**
	 * Returns the sampled points of a sampler
	 *
	 * @param id
	 * @return
	 */
	public List<InputSpacePoint> getSampledPoints(Integer id) {
		String sql = "SELECT list FROM samples WHERE id = %d";
		String sqlFormatted = String.format(sql, id);
		List<InputSpacePoint> result = new LinkedList<>();
		try {
			Statement stmt = this.connection.createStatement();
			ResultSet set = stmt.executeQuery(sqlFormatted);
			if (set.next()) {
				byte[] bytes = set.getBytes(1);
				InputSpacePointList r = new InputSpacePointList();
				r.parseBytes(bytes);
				result = r.getList();
			}
			stmt.close();
		} catch (SQLException ex) {
			Logger.getLogger(DatabaseClient.class.getName()).log(Level.SEVERE, null, ex);
		}
		return result;
	}

	/**
	 * Returns the output space points of a model trained through a specific
	 * sampler
	 *
	 * @param id
	 * @return
	 */
	public List<OutputSpacePoint> getOutputSpacePoints(Integer id) {
		String sql = "SELECT list FROM model_predictions WHERE id = %d";
		String sqlFormatted = String.format(sql, id);
		List<OutputSpacePoint> result = new LinkedList<>();
		try {
			Statement stmt = this.connection.createStatement();
			ResultSet set = stmt.executeQuery(sqlFormatted);
			if (set.next()) {
				byte[] bytes = set.getBytes(1);
				OutputSpacePointList r = new OutputSpacePointList();
				r.parseBytes(bytes);
				result = r.getList();
			}
			stmt.close();
		} catch (SQLException ex) {
			Logger.getLogger(DatabaseClient.class.getName()).log(Level.SEVERE, null, ex);
		}
		return result;
	}
	
	
	public static void main(String[] args) {
		DatabaseClient client = new DatabaseClient();
		String databaseHost=args[0], 
				databaseName=args[1], 
				databaseUsername=args[2],
				databasePassword=args[3];
		
		client.setDatabaseHost(databaseHost);
		client.setDatabaseName(databaseName);
		client.setUsername(databaseUsername);
		client.setPassword(databasePassword);
		client.openConnection();
		String samplesId=args[4];
		List<InputSpacePoint> l=client.getSampledPoints(new Integer(samplesId));
		client.closeConnection();
		
		for(InputSpacePoint p:l) {
			System.out.println(p.toStringCSVFormat());
		}
	}

}
