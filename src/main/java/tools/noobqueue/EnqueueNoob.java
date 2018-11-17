package tools.noobqueue;


// [START import]

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

// [END import]

@WebServlet(
        name = "TaskEnqueueNoob",
        description = "taskqueuenoob: ",
        urlPatterns = "/api/queuenoob/enqueue"
)
public class EnqueueNoob extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("account");
        String type = request.getParameter("type");
        System.out.println(request.toString());
        System.out.println();
        if (email == null || type == null) {
            response.sendError(400, "'email' or 'type' needed.");
            return;
        }

        Queue queue = QueueFactory.getDefaultQueue();

        Map<String, String> params = new HashMap<>();
        params.put("account", email);
        params.put("type", type);
        if (type.equals("download")) {
            params.put("videoId", request.getParameter("videoId"));
        }
        queue.add(QueueHelper.createQueueMessage("/api/queuenoob/dequeueNoob", params));
    }
}
