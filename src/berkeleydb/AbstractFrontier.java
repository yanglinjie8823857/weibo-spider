package berkeleydb;

import java.io.File;
import java.io.FileNotFoundException;

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

public abstract class AbstractFrontier {    
    private Environment env;    
    private static String CLASS_CATALOG = "java_class_catalog";   
    protected StoredClassCatalog javaCatalog;    
    protected Database catalogdatabase;    
    protected static Database database = null ;   
    protected String homeDirectory = null;  
         
    public AbstractFrontier(String homeDirectory) throws DatabaseException,  
                              FileNotFoundException {       
          this.homeDirectory = homeDirectory;     
          System.out.println("open environment: " + homeDirectory);      
            //设置环境参数，打开env        
          EnvironmentConfig envConfig = new EnvironmentConfig();        
          envConfig.setTransactional(true);        
          envConfig.setAllowCreate(true);        
          env = new Environment(new File(homeDirectory), envConfig);        
          //设置数据库参数        
          DatabaseConfig dbConfig = new DatabaseConfig();        
          dbConfig.setTransactional(true);       
          dbConfig.setAllowCreate(true);        
          //打开数据库       
          catalogdatabase = env.openDatabase(null, CLASS_CATALOG, dbConfig);       
          javaCatalog = new StoredClassCatalog(catalogdatabase);       
           //设置参数        
          DatabaseConfig dbConfigTe = new DatabaseConfig();       
          dbConfigTe.setTransactional(true);        
          dbConfigTe.setAllowCreate(true);        
          //打开数据库        
          database = env.openDatabase(null, "URL", dbConfig);    
      }       
      
      public void close() throws DatabaseException {        
          database.close();       
          javaCatalog.close();        
          env.close();    
       }        
      
      protected abstract void put(Object key, Object value);        
      protected abstract Object get(Object key);        
      protected abstract Object delete(Object key);
 }