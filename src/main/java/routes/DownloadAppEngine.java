package routes;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import tools.util.DatastoreHelper;
import tools.util.DownloadHelper;
import tools.util.QueueHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@WebServlet(
        name = "Download App Engine",
        urlPatterns = "/api/download/*")
public class DownloadAppEngine extends HttpServlet {

    @Override
    public void init() throws ServletException {
        DatastoreHelper datastoreHelper = new DatastoreHelper();
        this.getServletContext().setAttribute("datastoreHelper", datastoreHelper);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        String email = req.getParameter("email");
        email = email.replaceAll(" ", "+");
        final Query q = new Query("user").setFilter(new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, email));

        PreparedQuery pq = datastore.prepare(q);
        com.google.appengine.api.datastore.Entity entity = pq.asSingleEntity(); // Retrieve up to five posts


        String pathInfo = req.getPathInfo();
        String[] pathParts = pathInfo.split("/");
        String videoOwner = pathParts[1];
        String videoTitle = pathParts[2];


        System.out.println(req.toString());
        System.out.println();
        if (email == null) {
            res.sendError(400, "'email' or 'type' needed.");
            return;
        }
        if (entity == null) {
            res.sendError(400, "User " + email + " not found");
            return;
        }
        long score = (long) entity.getProperty("score");

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("videoOwner", videoOwner);
        params.put("videoTitle", videoTitle);
        if (score < 100) {
            Queue queue = QueueFactory.getDefaultQueue();
            queue.add(QueueHelper.createQueueMessage("/api/queuenoob/dequeue", params));
        } else {
            Gson gson = new GsonBuilder().create();
            Queue queue = QueueFactory.getQueue("queue-casu-leet");
            queue.add(TaskOptions.Builder
                            .withMethod(TaskOptions.Method.PULL)
                            .payload(gson.toJson(params)));
            DatastoreHelper datastoreHelper = (DatastoreHelper) req.getServletContext().getAttribute("datastoreHelper");
            processDownloadCasuLeet(gson, datastoreHelper);
        }
        res.getWriter().println("Request received");
    }

    public void processDownloadCasuLeet(Gson gson, DatastoreHelper datastoreHelper) {
        Queue queue = QueueFactory.getQueue("queue-casu-leet");
        List<TaskHandle> tasks = queue.leaseTasks(60, TimeUnit.SECONDS, 1);
        for (TaskHandle task : tasks) {
            Type typeToken = new TypeToken<Map<String, String>>() { }.getType();
            Map<String, String> params = gson.fromJson(new String(task.getPayload()), typeToken);
            String email = params.get("email");
            String videoTitle = params.get("videoTitle");
            String videoOwner = params.get("videoOwner");
            DownloadHelper.sendVideoByMail(datastoreHelper, email, videoTitle, videoOwner);
            // [START delete_task]
            queue.deleteTask(task);
            // [END delete_task]
        }
    }
}
