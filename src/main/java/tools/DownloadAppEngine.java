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
        value = "/download",
        urlPatterns = "/api/queuenoob/enqueue")
public class DownloadAppEngine extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String email = req.getParameter("email");
        String videoId = req.getParameter("videoId");

        System.out.println(req.toString());
        System.out.println();
        if(email == null || videoId == null){
            res.sendError(400,"'email' or 'type' needed.");
            return;
        }

        Queue queue = QueueFactory.getDefaultQueue();

        Map<String,String> params = new HashMap<>();
        params.put("email", email);
        params.put("videoId", videoId);
        // FIXME For now, everyone is noob
        queue.add(QueueHelper.createQueueMessage("/api/queuenoob/dequeueNoob", params));
    }
}
