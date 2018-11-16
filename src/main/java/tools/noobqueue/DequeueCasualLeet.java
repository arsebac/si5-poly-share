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
        name = "DequeueCasualLeet",
        description = "TaskQueues: worker",
        urlPatterns = "/api/queuecasualleet/dequeue"
)
public class DequeueCasualLeet extends
        HttpServlet {

    private static final Logger log = Logger.getLogger(DequeueCasualLeet.class.getName());

    @Override
    public void init() throws ServletException {
        DatastoreHelper datastoreHelper = new DatastoreHelper();
        this.getServletContext().setAttribute("datastoreHelper", datastoreHelper);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DownloadHelper.sendVideoByMail(request);
    }

}
