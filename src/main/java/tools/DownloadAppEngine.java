package tools;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import tools.util.QueueHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(
        name = "Download App Engine",
        urlPatterns = "/api/download/*")
public class DownloadAppEngine extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String pathInfo = req.getPathInfo();
        String[] pathParts = pathInfo.split("/");
        String videoOwner = pathParts[2];
        String videoTitle = pathParts[3];

        String email = req.getParameter("email");

        System.out.println(req.toString());
        System.out.println();
        if(email == null){
            res.sendError(400,"'email' or 'type' needed.");
            return;
        }

        Queue queue = QueueFactory.getDefaultQueue();

        Map<String,String> params = new HashMap<>();
        params.put("email", email);
        params.put("videoOwner", videoOwner);
        params.put("videoTitle", videoTitle);
        // FIXME For now, everyone is noob
        queue.add(QueueHelper.createQueueMessage("/api/queuenoob/dequeueNoob", params));
    }
}
