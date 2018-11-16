package routes;

import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.search.DateUtil;
import com.google.cloud.storage.BlobInfo;
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
import java.time.Instant;
import java.util.Date;
import java.util.List;

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
        DatastoreHelper datastoreHelper = (DatastoreHelper) request.getServletContext().getAttribute("datastoreHelper");
        try {
            BlobInfo blobInfo = storageHelper.getVideoUrl(request, BUCKET_NAME);
            String email = request.getParameter("email");
            String title = request.getParameter("title");
            String url = BUCKET_NAME + "/api/download/" + email + "/" + title + "?email=" + email;
            MailUtil.sendEmail(request.getParameter("email"), "Merci d'avoir utilisé Poly truc, Voici le lien de téléchargement partageable :" + url);
            datastoreHelper.addVideo(email, blobInfo.getSize(), blobInfo, title);
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
