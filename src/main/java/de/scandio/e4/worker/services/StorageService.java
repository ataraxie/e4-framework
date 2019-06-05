package de.scandio.e4.worker.services;

import de.scandio.e4.worker.model.E4Error;
import de.scandio.e4.worker.model.E4Measurement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.Date;


@Service
public class StorageService {

	private static final Logger log = LoggerFactory.getLogger(StorageService.class);

	private ApplicationStatusService applicationStatusService;

	private int workerIndex;
	private Connection connection;

	public StorageService(ApplicationStatusService applicationStatusService) throws Exception {
		this.applicationStatusService = applicationStatusService;
		initDatabase("e4-" + new Date().getTime() + ".sqlite");
	}

	private void initDatabase(String databaseFileName) throws Exception {
		String url = "jdbc:sqlite:" + this.applicationStatusService.getOutputDir() + "/" + databaseFileName;
		this.connection = DriverManager.getConnection(url);
		DatabaseMetaData meta = connection.getMetaData();
		log.info("New DB created with driver {}", meta.getDriverName());

		Statement stmt = connection.createStatement();

		String sql = "CREATE TABLE E4Measurement(" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"timestamp DATETIME default current_timestamp," +
				"time_taken INTEGER NOT NULL," +
				"virtual_user TEXT NOT NULL," +
				"action TEXT NOT NULL," +
				"testpackage TEXT NOT NULL, " +
				"thread_id TEXT," +
				"node_id TEXT)";

		stmt.executeUpdate(sql);
		stmt.close();

		stmt = connection.createStatement();

		sql = "CREATE TABLE E4Error(" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"timestamp DATETIME default current_timestamp," +
				"key TEXT NOT NULL, " +
				"type TEXT NOT NULL)";

		stmt.executeUpdate(sql);
		stmt.close();

	}

	public void recordMeasurement(E4Measurement e4measurement) throws Exception {

		Statement stmt = this.connection.createStatement();
		String sqlTemplate = "INSERT INTO E4Measurement (timestamp,time_taken,virtual_user,action,testpackage,thread_id,node_id) " +
				"VALUES(%d,%d,'%s','%s','%s','%s','%s')";
		String sql = String.format(sqlTemplate,
				new Date().getTime(),
				e4measurement.getTimeTaken(),
				e4measurement.getVirtualUser(),
				e4measurement.getAction(),
				e4measurement.getTestPackage(),
				e4measurement.getThreadId(),
				e4measurement.getNodeId()
		);

		log.info("[REC_MEASURE]{}", e4measurement);

		stmt.executeUpdate(sql);
		stmt.close();
	}

	public int getWorkerIndex() {
		return workerIndex;
	}

	public void setWorkerIndex(int workerIndex) {
		this.workerIndex = workerIndex;
	}

	public void recordError(E4Error e4error) throws Exception {
		Statement stmt = this.connection.createStatement();
		String sqlTemplate = "INSERT INTO E4Error (timestamp,key,type) " +
				"VALUES(%d,'%s','%s')";

		String sql = String.format(sqlTemplate,
				new Date().getTime(),
				e4error.getKey(),
				e4error.getType());

		log.info("[REC_ERROR]{}", e4error);
		log.info(sql);

		stmt.executeUpdate(sql);
		stmt.close();
	}

}
