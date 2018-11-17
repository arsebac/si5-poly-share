package routes;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.search.DateUtil;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.cloud.datastore.*;
import pojo.Video;
import tools.util.CloudStorageHelper;
import tools.util.DatastoreHelper;
import tools.util.MailUtil;
import tools.util.QueueHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        email=  email.replaceAll(" ","+");
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
        if(entity == null){
            res.sendError(400,"User "+ email + " not found");
            return;
        }
        long score = (long) entity.getProperty("score");
        Map<String,String> params = new HashMap<>();
        params.put("email", email);
        params.put("videoOwner", videoOwner);
        params.put("videoTitle", videoTitle);
        List<EmbeddedEntity> videos = ((  List<EmbeddedEntity>) entity.getProperty("availableVideos"));

        long MINUTE = 600*1000; // in milli-seconds.
        // TODO need to check in the specialised queue ?
        // Autrement dit, si un Noob fait une deuxième demande en moins d'une minute, il recevra un email contenant le texte "lol non noob".
        List<Long> timers =  videos.stream().map(vid->new Date().getTime() - DateUtil.deserializeDate(String.valueOf(vid.getProperty("uploadDate"))).getTime()).collect(Collectors.toList());
        System.out.println(timers);
        boolean before1Min = false;
        if(score < 100){
            if(before1Min){
                MailUtil.sendEmail(email,"lol non noob");
            }else{
                Queue queue = QueueFactory.getQueue("queue-noob");
                queue.add(QueueHelper.createQueueMessage("/api/queuenoob/dequeue", params));
            }

        }else if(score < 200){
            res.getWriter().write("No queue for u !");
        }else{
            res.getWriter().write("No queue for u !");
        }


        // FIXME For now, everyone is noob
    }
}
