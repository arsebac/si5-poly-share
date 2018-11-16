package routes;

import pojo.UploadResult;
import tools.util.CloudStorageHelper;
import tools.util.DatastoreHelper;
import tools.util.MailUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@MultipartConfig
@WebServlet(name = "UploadEngine", value = "/api/upload")
public class UploadEngine extends HttpServlet {
    
    private final String BUCKET_NAME = "polyshare-cgjm.appspot.com";
    
    @Override
    public void init() throws ServletException {
        CloudStorageHelper storageHelper = new CloudStorageHelper();
        this.getServletContext().setAttribute("storageHelper", storageHelper);
        DatastoreHelper datastoreHelper = new DatastoreHelper();
        this.getServletContext().setAttribute("datastoreHelper", datastoreHelper);
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        CloudStorageHelper storageHelper = (CloudStorageHelper) request.getServletContext().getAttribute("storageHelper");
        try {
            UploadResult uploadResult = storageHelper.getVideoUrl(request, BUCKET_NAME);
            String email = request.getParameter("email");
            String title = request.getParameter("title");
            String url = "http://polyshare-cgjm.appspot.com/api/download/"+email+"/" + title + "?email=" + email;
            MailUtil.sendEmail(request.getParameter("email"), "MErci d'avoir utilisé Poly truc, Voici le lien de téléchargement partageable :" + url);

            DatastoreHelper datastoreHelper =  (DatastoreHelper) request.getServletContext().getAttribute("datastoreHelper");
            datastoreHelper.addVideo(email,uploadResult.getSize()+"",uploadResult.getDownloadLink(), title);
            response.setContentType("text/plain");
            response.setStatus(201);
            response.getWriter().println("succeeded");

        } catch (ServletException e) {
            response.setContentType("text/plain");
            response.setStatus(500);
            response.getWriter().println("error");
            response.getWriter().println(e);
        }
    }
}
