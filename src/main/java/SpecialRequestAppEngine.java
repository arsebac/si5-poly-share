import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.repackaged.org.joda.time.LocalDate;
import com.google.cloud.datastore.*;
import tools.util.DatastoreHelper;

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
        String title = req.getParameter("title");
        String url = LocalDate.now().toString() + "-" + new Random().nextInt();
        new DatastoreHelper().addVideo(mail, size, url, title);


    }

}
