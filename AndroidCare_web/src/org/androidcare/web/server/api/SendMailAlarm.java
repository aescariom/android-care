package org.androidcare.web.server.api;

import org.androidcare.web.server.PMF;
import org.androidcare.web.shared.persistent.Alarm;

import javax.jdo.PersistenceManager;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;


public class SendMailAlarm extends HttpServlet {

    public void process(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");

        PersistenceManager pm = PMF.get().getPersistenceManager();

        long alarmId = Long.parseLong(req.getParameter("alarmId").toString());

        Alarm alarm = (Alarm) pm.getObjectById(Alarm.class, alarmId);

        SimpleDateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        Date date = new Date();
        if(req.getParameter("time") != null){
            try {
                date = format.parse(req.getParameter("time").toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        sendMail(alarm, date);

    }

    private void sendMail(Alarm alarm, Date date) {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        StringBuilder builder = new StringBuilder();
        builder.append("AndroidCare\n\n").append("La alarma ").append(alarm.getName()).append(" se ha disparado en ")
                .append(date.toString());

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("androidcare.alarms@gmail.com", "AndroidCare"));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(alarm.getEmailAddress(), alarm.getEmailAddress()));
            msg.setSubject("ANDROIDCARE: ALARMA " + alarm.getName() + " DISPARADA");
            msg.setText(builder.toString());
            Transport.send(msg);

        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        process(req, resp);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        process(req, resp);
    }
}
