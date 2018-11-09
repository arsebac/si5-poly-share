package tools;

import com.google.appengine.api.mail.BounceNotification;
import com.google.appengine.api.mail.BounceNotificationParser;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public class BounceMailHandlerServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(BounceMailHandlerServlet.class.getName());

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            BounceNotification bounce = BounceNotificationParser.parse(req);
            log.warning("Bounced email notification.");
            log.warning(bounce.toString());
            // The following data is available in a BounceNotification object
            // bounce.getOriginal().getFrom()
            // bounce.getOriginal().getTo()
            // bounce.getOriginal().getSubject()
            // bounce.getOriginal().getText()
            // bounce.getNotification().getFrom()
            // bounce.getNotification().getTo()
            // bounce.getNotification().getSubject()
            // bounce.getNotification().getText()
            // ...
        } catch (MessagingException e) {
            log.warning(e.getMessage());
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
