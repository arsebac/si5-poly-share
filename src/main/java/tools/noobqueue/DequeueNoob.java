package tools.noobqueue;


import tools.util.MailUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

//  [START worker]
// The Worker servlet should be mapped to the "/worker" URL.
// With @WebServlet annotation the webapp/WEB-INF/web.xml is no longer required.
@WebServlet(
        name = "DequeueNoob",
        description = "TaskQueues: worker",
        urlPatterns = "/api/queuenoob/dequeueNoob"
)
public class DequeueNoob extends
        HttpServlet {

    private static final Logger log = Logger.getLogger(DequeueNoob.class.getName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String videoId = request.getParameter("videoId");

        MailUtil.sendEmail(email, "Link to download you video: http://www.yolo.com/" + videoId);

        // Do something with key.
        // [START_EXCLUDE]
        // [END_EXCLUDE]
    }
}
