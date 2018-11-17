package routes;

import com.google.cloud.datastore.*;
import exceptions.UserNotFoundException;
import tools.util.DatastoreHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "Users servlet", value = "/users")
public class Users extends HttpServlet {
    @Override
    public void init() {
        DatastoreHelper datastoreHelper = new DatastoreHelper();
        this.getServletContext().setAttribute("datastoreHelper", datastoreHelper);
    }

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
            out.format("<tr><td>" + entity.getString("email") +
                    "</td><td>" + entity.getLong("score") + "</tr>");
        }
        out.print("</table></body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String score1 = request.getParameter("score");
        int score = Integer.parseInt(score1 == null ? "0" : score1);
        String email = request.getParameter("email");

        DatastoreHelper datastoreHelper = (DatastoreHelper) request.getServletContext().getAttribute("datastoreHelper");
        try {
            com.google.appengine.api.datastore.Entity userFound = datastoreHelper.getUser(email);
            datastoreHelper.addPointsToUser(email, score);
            response.getWriter().println("Add point to user " + email + "\n<br/><a href='/'>Retour à la page d'accueuil</a>");
        } catch (UserNotFoundException e) {
            datastoreHelper.addUser(email, score);
            response.getWriter().println("User added to database\n<br/><a href='/'>Retour à la page d'accueuil</a>");

        }
    }
}
