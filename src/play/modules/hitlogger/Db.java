package play.modules.hitlogger;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.sql2o.Sql2o;
import play.Logger;
import play.Play;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: lars
 * Date: 8/29/12
 * Time: 11:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class Db {


    private final static Sql2o sql2o;
    public final static boolean disabled;

    static{
        String url = Play.configuration.getProperty("hitlogger.url");
        String user = Play.configuration.getProperty("hitlogger.user");
        String pass = Play.configuration.getProperty("hitlogger.pass");
        String driver = Play.configuration.getProperty("hitlogger.driver");
        String hitlogDisabled = Play.configuration.getProperty("hitlogger.disabled");




        if (driver != null && !driver.isEmpty()) {
            try {
                DriverManager.registerDriver((Driver) Class.forName(driver).newInstance());
            } catch (Exception e) {
                throw new RuntimeException(String.format("could not register driver %s", driver), e);
            }
        }

        if ("true".equals(hitlogDisabled)) {
            disabled = true;
        } else {
            if (url == null) {
                Logger.error("Could not initialize hitlogger plugin. Please set hitlogger.url, hitlogger.user and hitlogger.pass in application.conf");
                disabled = true;
            } else {
                disabled = false;
            }
        }

        String poolingUrl = setupPoolingDriver(url, user, pass, "hitlogger_db");

        Logger.info("url: "  + poolingUrl);

        sql2o = new Sql2o(poolingUrl, user, pass);
    }

    public static Sql2o getSql2o() {
        return sql2o;
    }

    public static String setupPoolingDriver(String url, String user, String pass, String name) {
        final String driverNameBase = "jdbc:apache:commons:dbcp:";
        try{
            ObjectPool connectionPool = new GenericObjectPool();

            ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(url, user, pass);

            PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, connectionPool, null,null, false, true);

            Class.forName("org.apache.commons.dbcp.PoolingDriver");
            PoolingDriver driver = (PoolingDriver) DriverManager.getDriver(driverNameBase);

            driver.registerPool(name, connectionPool);

            return driverNameBase + name;

        }
        catch(Exception e){
            throw new RuntimeException(e);
        }

    }
}
