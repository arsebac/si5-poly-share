package test;

import com.google.appengine.repackaged.com.google.gson.Gson;
import com.google.cloud.datastore.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "HelloAppEngine", value = "/users")
public class Users extends HttpServlet {
    List<User> users = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        com.google.cloud.datastore.Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

        EntityQuery query = Query.newEntityQueryBuilder().setKind("user")
                .setOrderBy(StructuredQuery.OrderBy.desc("score")).build();
        QueryResults<Entity> results = datastore.run(query);

        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();
        out.print("Users :\n");
        while (results.hasNext()) {
            com.google.cloud.datastore.Entity entity = results.next();
            out.format("User "+entity.getString("email")+
                    ": videos "+entity.getString("availableVideos")+
                    ", score "+entity.getLong("score")+"\n");
        }

        //resp.getWriter().print(out.toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        com.google.cloud.datastore.Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
        KeyFactory keyFactory = datastore.newKeyFactory().setKind("user");
        IncompleteKey key = keyFactory.setKind("user").newKey();

        Gson gson = new Gson();
        User user = gson.fromJson(request.getReader(), User.class);


        // Record an user to the datastore
        FullEntity<IncompleteKey> curUser = FullEntity.newBuilder(key)
                .set("email", user.email).set("availableVideos", "").set("score", user.score).build();
        datastore.add(curUser);

        response.getWriter().println("User added to database");
    }
}
