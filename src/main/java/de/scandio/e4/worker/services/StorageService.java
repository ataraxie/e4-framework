package de.scandio.e4.worker.services;

import de.scandio.e4.dto.Measurement;
import de.scandio.e4.worker.interfaces.Action;
import de.scandio.e4.worker.interfaces.VirtualUser;
import de.scandio.e4.worker.util.WorkerUtils;
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

		String sql = "CREATE TABLE E4(" +
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
	}

	public void recordMeasurement(Measurement measurement) throws Exception {

		Statement stmt = this.connection.createStatement();
		String sqlTemplate = "INSERT INTO E4 (timestamp,time_taken,virtual_user,action,testpackage,thread_id,node_id) " +
				"VALUES(%d,%d,'%s','%s','%s','%s','%s')";
		String sql = String.format(sqlTemplate,
				new Date().getTime(),
				measurement.getTimeTaken(),
				measurement.getVirtualUser(),
				measurement.getAction(),
				measurement.getTestPackage(),
				measurement.getThreadId(),
				measurement.getNodeId()
		);

		log.warn("[RECORD]{}|{}|{}|{}|{}", measurement.getTimeTaken(), measurement.getThreadId(),
				measurement.getVirtualUser(), measurement.getAction(), measurement.getNodeId());

		stmt.executeUpdate(sql);
		stmt.close();
	}

	public int getWorkerIndex() {
		return workerIndex;
	}

	public void setWorkerIndex(int workerIndex) {
		this.workerIndex = workerIndex;
	}
}
