package routes;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.search.DateUtil;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
            System.out.println("email: " + email);
            setupDelete(datastoreHelper, email, blobInfo);
            datastoreHelper.addVideo(email, blobInfo.getSize(), blobInfo.getMediaLink(), title);
            MailUtil.sendEmail(request.getParameter("email"), "Merci d'avoir utilisé Poly truc, Voici le lien de téléchargement partageable :" + url);
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
    
    private void setupDelete(DatastoreHelper datastoreHelper, String email, BlobInfo blobInfo) throws ServletException {
        Entity user = datastoreHelper.getUser(email);
        if (user != null) {
            long score = (long) user.getProperty("score");
            int deleteTimeout = 300000;
            if (score > 100 && score <= 200) {
                deleteTimeout = 600000;
            } else if (score > 200) {
                deleteTimeout = 1800000;
            }
            Queue queue = QueueFactory.getDefaultQueue();
            queue.add(TaskOptions.Builder.withPayload(new BlobDeleter(blobInfo, user)).countdownMillis(deleteTimeout));
        }
        
    }
    
    public static class BlobDeleter implements DeferredTask {
        private BlobInfo blobInfo;
        private Entity user;
        private static DatastoreService datastore = null;
        
        static {
            datastore = DatastoreServiceFactory.getDatastoreService();
        }
        
        public BlobDeleter(BlobInfo blobInfo, Entity user) {
            this.blobInfo = blobInfo;
            this.user = user;
        }
        
        @Override
        public void run() {
            System.out.println("running with " + user.getProperty("email"));
            List<EmbeddedEntity> availableVideos = (List<EmbeddedEntity>) user.getProperty("availableVideos");
            if (availableVideos != null) {
                availableVideos.stream().filter(vid -> vid.getProperty("url").equals(blobInfo.getMediaLink()))
                        .findFirst().ifPresent(availableVideos::remove);
            }
            user.setProperty("availableVideos", availableVideos);
            datastore.put(user);
            Storage storage = StorageOptions.getDefaultInstance().getService();
            storage.delete(this.blobInfo.getBlobId());
        }
    }
}
