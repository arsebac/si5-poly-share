package routes;

import tools.util.CloudStorageHelper;
import tools.util.DatastoreHelper;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@MultipartConfig
@WebServlet(name = "CleanEngine", value = "/clean")
public class CleanEngine extends HttpServlet {

    private final String BUCKET_NAME = "polyshare-cgjm.appspot.com";

    @Override
    public void init() {
        CloudStorageHelper storageHelper = new CloudStorageHelper();
        this.getServletContext().setAttribute("storageHelper", storageHelper);
        DatastoreHelper datastoreHelper = new DatastoreHelper();
        this.getServletContext().setAttribute("datastoreHelper", datastoreHelper);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        DatastoreHelper datastoreHelper = (DatastoreHelper) request.getServletContext().getAttribute("datastoreHelper");
        datastoreHelper.deleteAll();
        CloudStorageHelper storageHelper = (CloudStorageHelper) request.getServletContext().getAttribute("storageHelper");
        storageHelper.deleteAll(BUCKET_NAME);
    }
}
