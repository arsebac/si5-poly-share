import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.repackaged.org.joda.time.LocalDate;
import com.google.cloud.datastore.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;


@WebServlet(name = "Special Requests", value = "/special")
public class SpecialRequestAppEngine extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String mail = req.getParameter("mail");
        String size = req.getParameter("size");
        String name = req.getParameter("title");
        DatastoreService datastore;
        datastore = DatastoreServiceFactory.getDatastoreService();
        int point = Integer.parseInt(size) / 10;
        final Query q =
                new Query("user").setFilter(new Query.FilterPredicate("email", Query.FilterOperator.NOT_EQUAL, mail));

        PreparedQuery pq = datastore.prepare(q);
        com.google.appengine.api.datastore.Entity entity = pq.asSingleEntity(); // Retrieve up to five posts
        String availableVideos = entity.getProperty("availableVideos").toString() + ",";
        entity.setProperty("score", ((int)entity.getProperty("score")) + point);
        entity.setProperty("availableVideos", availableVideos + LocalDate.now().toString() + "-" + new Random().nextInt());
        try {
            datastore.put(entity); // store the entity
        } catch (DatastoreFailureException e) {
            throw new ServletException("Datastore error", e);
        }
        }
}
