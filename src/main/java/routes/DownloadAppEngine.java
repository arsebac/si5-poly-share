package routes;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.cloud.datastore.*;
import tools.util.CloudStorageHelper;
import tools.util.DatastoreHelper;
import tools.util.QueueHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        final Query q = new Query("user").setFilter(new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, email));

        PreparedQuery pq = datastore.prepare(q);
        com.google.appengine.api.datastore.Entity entity = pq.asSingleEntity(); // Retrieve up to five posts


        String pathInfo = req.getPathInfo();
        String[] pathParts = pathInfo.split("/");
        String videoOwner = pathParts[1];
        String videoTitle = pathParts[2];


        System.out.println(req.toString());
        System.out.println();
        if(email == null){
            res.sendError(400,"'email' or 'type' needed.");
            return;
        }
        long score = (long) entity.getProperty("score");
        Map<String,String> params = new HashMap<>();
        params.put("email", email);
        params.put("videoOwner", videoOwner);
        params.put("videoTitle", videoTitle);
        if(score < 100){
            Queue queue = QueueFactory.getDefaultQueue();
            queue.add(QueueHelper.createQueueMessage("/api/queuenoob/dequeueNoob", params));
        }else if(score < 200){
            res.getWriter().write("No queue for u !");
        }else{
            res.getWriter().write("No queue for u !");
        }


        // FIXME For now, everyone is noob
    }
}
