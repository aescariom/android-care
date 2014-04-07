package org.androidcare.web.server.module.dashboard;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.androidcare.web.client.module.dashboard.rpc.AlarmService;
import org.androidcare.web.server.PMF;
import org.androidcare.web.shared.persistent.Alarm;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class AlarmServiceImpl extends RemoteServiceServlet implements AlarmService {

    private static final Logger log = Logger.getLogger(AlarmServiceImpl.class.getName());

    @Override
    public List<Alarm> getActiveAlarms() {
        List<Alarm> alarms = new ArrayList();
        PersistenceManager pm = PMF.get().getPersistenceManager();

        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        Query query = pm.newQuery(Alarm.class);
        query.setFilter("owner == alarmOwner");
        query.declareParameters("String alarmOwner");

        try {
            List<?> rs = (List<?>) query.execute(user.getUserId());
            if(rs != null){
                for (Object alarm : rs) {
                    alarms.add(new Alarm((Alarm) alarm));
                }
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }finally {
            query.closeAll();
        }
        return alarms;
    }

    @Override
    public void saveAlarm(Alarm alarm) {
        PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            UserService userService = UserServiceFactory.getUserService();
            User user = userService.getCurrentUser();
            alarm.setOwner(new String(user.getUserId()));

            pm.makePersistent(alarm);
        } catch(Exception ex){
            log.log(Level.SEVERE, "Alarm could not be saved", ex);
        } finally {
            pm.close();
        }
    }

    @Override
    public boolean deleteAlarm(Alarm alarm) {
        PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
        	pm.deletePersistent(alarm);
            return true;
        } catch(Exception ex){
            log.log(Level.SEVERE, "Alarm could not be saved", ex);
            return false;
        } finally {
            pm.close();
        }
    }

}
