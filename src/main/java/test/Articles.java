package test;

import com.google.appengine.repackaged.com.google.gson.Gson;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "HelloAppEngine", value = "/articles")
public class Articles extends HttpServlet {
    List<Article> articles = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        com.google.cloud.datastore.Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

        EntityQuery query = Query.newEntityQueryBuilder().setKind("article")
                .setOrderBy(StructuredQuery.OrderBy.desc("price")).build();
        QueryResults<Entity> results = datastore.run(query);

        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();
        out.print("Articles :\n");
        while (results.hasNext()) {
            com.google.cloud.datastore.Entity entity = results.next();
            out.format("Article "+entity.getString("name")+
                    ": price "+entity.getDouble("price")+
                    ", quantity "+entity.getLong("quantity")+"\n");
        }

        //resp.getWriter().print(out.toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        com.google.cloud.datastore.Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
        KeyFactory keyFactory = datastore.newKeyFactory().setKind("article");
        IncompleteKey key = keyFactory.setKind("article").newKey();

        Gson gson = new Gson();
        Article article = gson.fromJson(request.getReader(), Article.class);

        // Record an article to the datastore
        FullEntity<IncompleteKey> curArticle = FullEntity.newBuilder(key)
                .set("name", article.name).set("price", article.price).set("quantity", article.quantity).build();
        datastore.add(curArticle);

        response.getWriter().println("Article added to list");
    }
}
