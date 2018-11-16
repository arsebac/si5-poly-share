package routes;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import pojo.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@WebServlet(name = "HelloAppEngine", value = "/users")
public class Users extends HttpServlet {
    List<User> users = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        com.google.cloud.datastore.Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

        EntityQuery query = Query.newEntityQueryBuilder().setKind("user")
                .setOrderBy(StructuredQuery.OrderBy.desc("score")).setLimit(10).build();
        QueryResults<Entity> results = datastore.run(query);

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        resp.setContentType("text/html");

        out.print("<html><h1>LeaderBoard</h1><body><table><th><td>Utilisateur</td><td>Score</td></th>\n");
        while (results.hasNext()) {
            com.google.cloud.datastore.Entity entity = results.next();
            out.format("<tr><td>"+entity.getString("email")+
                    "</td><td>"+entity.getLong("score")+"</tr>");
        }
        out.print("</table></body></html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        com.google.cloud.datastore.Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
        KeyFactory keyFactory = datastore.newKeyFactory().setKind("user");
        IncompleteKey key = keyFactory.setKind("user").newKey();

        String score1 = request.getParameter("score");
        int score = Integer.parseInt(score1);

        User user = new User(request.getParameter("email"), score);

        // Record an user to the datastore
        FullEntity<IncompleteKey> curUser = FullEntity.newBuilder(key)
                .set("email", user.getEmail()).set("availableVideos", new LinkedList<>()).set("score", user.getScore()).build();
        datastore.add(curUser);

        response.getWriter().println("User added to database");
    }
}
