package tools.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class MailUtil {

    public static boolean sendEmail(String destination, String message){
        Properties p = new Properties();
        p.put("mail.debug",true);
        Session session = Session.getDefaultInstance(p);
        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setText(message);
            /*
                todo: change to noreply@polyshare-cgjm.appspotmail.com
                The email used in order to send email should be registered (Cloud console->parameters)
             */
            String fromEmail = "polyshare-cgjm@appspot.gserviceaccount.com";

            mimeMessage.setFrom(new InternetAddress(fromEmail, "PolyShare"));
            mimeMessage.addRecipients(Message.RecipientType.TO, destination);
            mimeMessage.setSubject("Email test");
            Transport.send(mimeMessage);
            return true;
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;

    }
}
