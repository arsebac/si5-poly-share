package tools;

import tools.util.MailUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "Mail App Engine", value = "/sendmail")
public class MailAppEngine extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String dest = req.getParameter("mail");
        if(!MailUtil.sendEmail(dest, "Bonjour,\n\n ceci est un des premiers emails envoyé par PolyShare")){
            resp.getWriter().print("An error");
        }else{
            resp.sendRedirect("/");
        }
    }
}
