package test;

import com.google.appengine.repackaged.com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "HelloAppEngine", value = "/articles/")
public class Articles extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Article> articles = new ArrayList<>();
        Article e = new Article("dd",3.3,2);
        articles.add(e);
        Article f = new Article("dd",3.3,2);
        articles.add(f);
        Gson gson = new Gson();
        resp.getWriter().print(gson.toJson(articles));
    }
}
