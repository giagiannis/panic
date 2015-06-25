/*
 * Scripts that create the database schema.
 * 
 * date: Thu Jun 11 15:14:04 EEST 2015
 * author: Giannis Giannakopoulos
 */
CREATE TABLE experiments (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	experiment_date DATE,
	experiment_time TIME,
	sampling_rate DOUBLE,
	input_file VARCHAR(100),
	configurations VARCHAR(1000)
);

CREATE TABLE metrics (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	experiment_id INT NOT NULL,
	model   VARCHAR(100),
	sampler VARCHAR(100),
	mean_square_error DOUBLE,
	mean_average_error DOUBLE,
	deviation DOUBLE,
	coefficient_of_determination DOUBLE,
	FOREIGN KEY(experiment_id) REFERENCES experiments(id)
);

CREATE TABLE samples (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	experiment_id INT NOT NULL,
	sampler VARCHAR(100),
	list BLOB,
	FOREIGN KEY(experiment_id) REFERENCES experiments(id)
);

CREATE TABLE model_predictions (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	experiment_id INT NOT NULL,
	sampler VARCHAR(100),
	model VARCHAR(100),
	list BLOB,
	FOREIGN KEY(experiment_id) REFERENCES experiments(id)
);
