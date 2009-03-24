package org.genedb.web.mvc.model;

import org.genedb.web.mvc.controller.download.ResultEntry;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.EnvironmentLockedException;


public class ResultsCacheFactory {

    private Environment env;

    private StoredClassCatalog javaCatalog;
    private Database resultsCacheDb;

    private StoredMap<String, ResultEntry> resultsCacheMap;

    public StoredMap<String, ResultEntry> getResultsCacheMap() {
        openDb();
        return resultsCacheMap;
    }

    private String rootDirectory;

    private boolean readOnly;

    private final static Logger logger = Logger.getLogger(ResultsCacheFactory.class);

    private final String CLASS_CATALOG = "java_class_catalog";

    private final String RESULTS_CACHE_STORE = "dtos";

    // This field must only be accessed while holding a lock on this object
    private boolean databaseIsOpen = false;

    {
        logger.debug("Instantiating BerkeleyMapFactory");
    }

    private synchronized void openDb() {
        if (databaseIsOpen) {
            return;
        }

        Runtime.getRuntime().addShutdownHook( new Thread() {
            @Override
            public void run() {
                closeDb();
            }
        });

        logger.debug("Opening environment in: " + rootDirectory);
        logger.debug("Read-only status: " + readOnly);

        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setTransactional(true);
        envConfig.setAllowCreate(!readOnly);
        envConfig.setReadOnly(readOnly);

        try {
            env = new Environment(new File(rootDirectory), envConfig);
        } catch (EnvironmentLockedException e) {
            throw new RuntimeException("Unable to open Berkeley databases", e);
        } catch (DatabaseException e) {
            throw new RuntimeException("Unable to open Berkeley databases", e);
        }

        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setTransactional(true);
        dbConfig.setAllowCreate(!readOnly);
        dbConfig.setReadOnly(readOnly);

        try {
            Database catalogDb = env.openDatabase(null, CLASS_CATALOG, dbConfig);
            javaCatalog = new StoredClassCatalog(catalogDb);

            resultsCacheDb = env.openDatabase(null, RESULTS_CACHE_STORE, dbConfig);
            databaseIsOpen = true;
        }
        catch (DatabaseException exp) {
            throw new RuntimeException("Unable to open Berkeley databases", exp);
        }

        EntryBinding<String> stringBinding =
            new SerialBinding<String>(javaCatalog, String.class);
        EntryBinding<ResultEntry> resultEntryBinding =
            new SerialBinding<ResultEntry>(javaCatalog, ResultEntry.class);

        resultsCacheMap =
            new StoredMap<String, ResultEntry>(resultsCacheDb, stringBinding, resultEntryBinding, true);

    }


    public synchronized void closeDb() {
        try {
            resultsCacheDb.close();
        } catch (DatabaseException exp) {
            throw new RuntimeException("Unable to close DTO Berkeley DB", exp);
        }

       try {
           javaCatalog.close();
       } catch (DatabaseException exp) {
           throw new RuntimeException("Unable to close stored class catalog", exp);
       }

       try {
           env.close();
       } catch (DatabaseException exp) {
           throw new RuntimeException("Unable to close environment", exp);
       }

        databaseIsOpen = false;
    }


    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }


    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}
