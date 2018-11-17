import com.google.appengine.repackaged.org.joda.time.LocalDate;
import exceptions.UserNotFoundException;
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

        try {
            new DatastoreHelper().addVideo(mail, Long.getLong(size), url, title);
            resp.getWriter().print("The video " + title+" has been added");
        } catch (UserNotFoundException e) {
            resp.sendError(403,"The user " + mail + "cannot be found");
        }


    }

}
