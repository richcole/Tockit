/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.plugin;

import java.io.File;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseDriverLoader {

	private static final Logger logger = Logger.getLogger(DatabaseDriverLoader.class.getName());
	private static final String dbDriverDescriptorFileName = "driver.txt";
	
	private static List errors = new ArrayList();
	
	/**
	 * Introduced this as it seems that in my case java.sql.DriverManager
	 * doesn't return loaded drivers. (Javadoc states that each Driver implementation
	 * should register itself with DriverManager.)
	 */
	public static class Result {
		private Error[] errors;
		private Driver[] drivers;
		public Result(Driver[] drivers, Error[] errors) {
			this.drivers = drivers;
			this.errors = errors;
		}
		public Driver[] getDrivers() {
			return drivers;
		}
		public Error[] getErrors() {
			return errors;
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
	
	public static DatabaseDriverLoader.Result loadDrivers (File[] driversBaseFiles) {
		logger.setLevel(Level.FINER);

		File[] driverDirs = LoaderUtil.listBaseDirs(driversBaseFiles);
		if (driverDirs == null) {
			return new Result(new Driver[0], new Error[0]);
		}
		logger.fine("STARTING to load drivers. Found " + driverDirs.length + " db drivers");

		List foundDrivers = new ArrayList();
		for (int i = 0; i < driverDirs.length; i++) {
			File curDriverDir = driverDirs[i];
			try {
				logger.fine("Loading class loader for " + curDriverDir);
				Class[] foundDriverClasses = LoaderUtil.findClassesInDir(curDriverDir, dbDriverDescriptorFileName, Plugin.class, logger);
				System.out.println("found num of classes: " + foundDriverClasses.length);
				for (int j = 0; j < foundDriverClasses.length; j++) {
					Class cur = foundDriverClasses[j];
					Driver driver = (Driver) cur.newInstance();
					foundDrivers.add(driver);
					logger.finer("Instantiated driver: " + driver.getClass().getName());
				}
				logger.fine("Finished loading drivers in " + curDriverDir);
			}
			catch (ClassCastException e) {
				String errMsg = "Expected implementation of java.sql.Driver " + 
								"interface in " + e.getMessage();
				errors.add( new DatabaseDriverLoader.Error(
							curDriverDir, 
							new Exception(errMsg, e)));
			} catch (Exception e) {
				errors.add( new DatabaseDriverLoader.Error(curDriverDir, e));
			}
		}
		
		logger.fine("FINISHED loading drivers with " + errors.size() + " error(s)");
		DatabaseDriverLoader.Error[] errorsRes = (DatabaseDriverLoader.Error[]) errors.toArray(new DatabaseDriverLoader.Error[errors.size()]);
		Driver[] driversRes = (Driver[]) foundDrivers.toArray(new Driver[foundDrivers.size()]);
		return new Result(driversRes, errorsRes);
	}
	
}
