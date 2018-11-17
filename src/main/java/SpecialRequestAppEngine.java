import exceptions.NoobRateExceedException;
import exceptions.UserNotFoundException;
import tools.util.DatastoreHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Random;


@WebServlet(name = "Special Requests", value = "/special")
public class SpecialRequestAppEngine extends HttpServlet {

    @Override
    public void init() throws ServletException {
        DatastoreHelper datastoreHelper = new DatastoreHelper();
        this.getServletContext().setAttribute("datastoreHelper", datastoreHelper);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DatastoreHelper datastoreHelper = (DatastoreHelper) req.getServletContext().getAttribute("datastoreHelper");

        String mail = req.getParameter("mail");
        String size = req.getParameter("size");
        String title = req.getParameter("title");
        String url = new Date() + "-" + new Random().nextInt();

        try {
            datastoreHelper.addVideo(mail, Long.parseLong(size), url, title, false);
            resp.getWriter().print("The video " + title+" has been added");
        } catch (UserNotFoundException e) {
            resp.sendError(403,"The user " + mail + "cannot be found");
        } catch (NoobRateExceedException e) {
            resp.sendError(406,"The user need to send email");
        }


    }

}
