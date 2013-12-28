package org.androidcare.web.server.api;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.androidcare.web.server.PMF;
import org.androidcare.web.shared.persistent.Alarm;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RetrieveAlarms extends HttpServlet {

    private static final Logger log = Logger.getLogger(RetrieveReminders.class.getName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    private void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONArray jsonArray = null;

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        PersistenceManager pm = PMF.get().getPersistenceManager();

        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        List<JSONObject> list = null;

        if(user != null){
            Query query = pm.newQuery(Alarm.class);
            query.setOrdering("id asc");
            if(request.getParameter("reminderId") != null){
                List<Alarm> alarms = new ArrayList<Alarm>();
                int reminderId = Integer.parseInt(request.getParameter("alarmId").toString());
                Alarm alarm = (Alarm)pm.getObjectById(Alarm.class, reminderId);
                if(alarm != null){
                    if(alarm.getOwner().compareToIgnoreCase(user.getUserId()) == 0){
                        alarms.add(alarm);
                    }
                }
                list = getAlarmsList(alarms);
            }else{
                query.setFilter("owner == alarmOwner");
                query.declareParameters("String alarmOwner");
                list = getAlarmsList((List<Alarm>) query.execute(user.getUserId()));
            }
        }else{
            response.getWriter().write("{\"status\": -2}");
        }
        jsonArray = new JSONArray(list);
        response.getWriter().println(jsonArray.toString());
    }

    private List<JSONObject> getAlarmsList(List<Alarm> alarms) {
        ArrayList<JSONObject> list = new ArrayList<JSONObject>();
        for(Alarm alarm : alarms){
            list.add(new JSONObject(new Alarm(alarm)));
        }
        return list;
    }
}
