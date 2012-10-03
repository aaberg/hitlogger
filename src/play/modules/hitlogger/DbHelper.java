package play.modules.hitlogger;

import org.sql2o.Sql2o;
import play.jobs.Job;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: lars
 * Date: 9/24/12
 * Time: 10:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class DbHelper {

    public static void logRequestAsync(String action, String url, Sql2o sql2o) {
        if (Db.disabled) return;

        new LogRequestJob(action, url, sql2o).now();
    }

    public static class LogRequestJob extends Job {

        private final String action;
        private final String url;
        private final Sql2o sql2o;

        public LogRequestJob(String action, String url, Sql2o sql2o) {
            this.action = action;
            this.url = url;
            this.sql2o = sql2o;
        }

        @Override
        public void doJob() throws Exception {
            final String sql = "insert into hitlog(url,method,logdate) values (:url,:method,:logdate)";

            sql2o.createQuery(sql, String.format("hitlogger.logRequest %s", url)).addParameter("url", url).addParameter("method", action)
                    .addParameter("logdate", new Timestamp(new Date().getTime())).executeUpdate().getKey();
        }
    }

}
