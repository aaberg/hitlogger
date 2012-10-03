package play.modules.hitlogger;

import org.sql2o.Sql2o;
import play.Logger;
import play.PlayPlugin;
import play.mvc.Http;

import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: lars
 * Date: 9/24/12
 * Time: 9:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class HitLoggerPlugin extends PlayPlugin {

    private final static String HITLOGGER_ALREADY_LOGGED = "hitlogger.requestLogged";

    private final Sql2o sql2o;
    public HitLoggerPlugin() {
        this.sql2o = Db.getSql2o();

    }

    @Override
    public void beforeActionInvocation(Method actionMethod) {
        String action = actionMethod.toGenericString();
        Http.Request request = Http.Request.current();

        if (request.args.containsKey(HITLOGGER_ALREADY_LOGGED)) {
            return; // Already logged this request
        }

        DbHelper.logRequestAsync(action, request.url, this.sql2o);
        request.args.put(HITLOGGER_ALREADY_LOGGED, true);
    }
}
