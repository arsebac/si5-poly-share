package tools.noobqueue;


import tools.util.DatastoreHelper;
import tools.util.DownloadHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

//  [START worker]
// The Worker servlet should be mapped to the "/worker" URL.
// With @WebServlet annotation the webapp/WEB-INF/web.xml is no longer required.
@WebServlet(
        name = "DequeueNoob",
        description = "TaskQueues: worker",
        urlPatterns = "/api/queuenoob/dequeue"
)
public class DequeueNoob extends
        HttpServlet {

    private static final Logger log = Logger.getLogger(DequeueNoob.class.getName());

    @Override
    public void init() throws ServletException {
        DatastoreHelper datastoreHelper = new DatastoreHelper();
        this.getServletContext().setAttribute("datastoreHelper", datastoreHelper);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DatastoreHelper datastoreHelper = (DatastoreHelper) request.getServletContext().getAttribute("datastoreHelper");
        String email = request.getParameter("email");
        String videoTitle = request.getParameter("videoTitle");
        String videoOwner = request.getParameter("videoOwner");
        DownloadHelper.sendVideoByMail(datastoreHelper, email, videoTitle, videoOwner);
    }
}
