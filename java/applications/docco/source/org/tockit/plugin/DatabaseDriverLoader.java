/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.plugin;

import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;



public class DatabaseDriverLoader extends LoaderBase {

	private static final Logger logger = Logger.getLogger(DatabaseDriverLoader.class.getName());
	private static final String dbDriverDescriptorFileName = "driver.txt";
	
	private static List errors = new ArrayList();

	private static class DriverWrapper implements Driver {
		private final Driver finalDriver;
		private DriverWrapper(Driver finalDriver) {
			super();
			this.finalDriver = finalDriver;
		}
		public Connection connect(String url, Properties info)
			throws SQLException {
			return finalDriver.connect(url, info);
		}
		public boolean acceptsURL(String url) throws SQLException {
			return finalDriver.acceptsURL(url);
		}
		public DriverPropertyInfo[] getPropertyInfo(
			String url,
			Properties info)
			throws SQLException {
			return finalDriver.getPropertyInfo(url, info);
		}
		public int getMajorVersion() {
			return finalDriver.getMajorVersion();
		}
		public int getMinorVersion() {
			return finalDriver.getMinorVersion();
		}
		public boolean jdbcCompliant() {
			return finalDriver.jdbcCompliant();
		}
	}
	
	public static class Error {
		private File file;
		private Exception e;
		private Error (File file, Exception e) {
			this.file = file;
			this.e = e;
		}
		public Exception getException() {
			return e;
		}
		public File getDriverLocation() {
			return file;
		}
	}
	
	/**
	 * Instances of this class shouldn't be created - access it via static method loadPlugins.
	 */
	private DatabaseDriverLoader () {
	}
		
	public static DatabaseDriverLoader.Error[] loadDrivers (File driversDir) {
		logger.setLevel(Level.FINER);

		File[] driverDirs = findSubDirectories(driversDir);
		if (driverDirs == null) {
			return new Error[0];
		}
		logger.fine("STARTING to load drivers. Found " + driverDirs.length + " db drivers");

		List foundDrivers = new ArrayList();
		for (int i = 0; i < driverDirs.length; i++) {
			File curDriverDir = driverDirs[i];
			try {
				logger.fine("Loading class loader for " + curDriverDir);
				Class[] foundDriverClasses = findClassesInDir(curDriverDir, dbDriverDescriptorFileName, Driver.class, logger);
				for (int j = 0; j < foundDriverClasses.length; j++) {
					Class cur = foundDriverClasses[j];
					Driver driver = (Driver) cur.newInstance();
					DriverManager.registerDriver(new DriverWrapper(driver));
					logger.finer("Instantiated driver: " + driver.getClass().getName());
				}
				logger.fine("Finished loading drivers in " + curDriverDir);					
			} catch (Exception e) {
				errors.add( new DatabaseDriverLoader.Error(curDriverDir, e));
			}
		}
		
		logger.fine("FINISHED loading drivers with " + errors.size() + " error(s)");
		DatabaseDriverLoader.Error[] errorsRes = (DatabaseDriverLoader.Error[]) errors.toArray(new DatabaseDriverLoader.Error[errors.size()]);
		return errorsRes;
	}
	
}
