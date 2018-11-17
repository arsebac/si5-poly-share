package routes;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import exceptions.NoobRateExceedException;
import exceptions.UserNotFoundException;
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
        String email = request.getParameter("email");
        try {
            BlobInfo blobInfo = storageHelper.getVideoUrl(request, BUCKET_NAME);
            String title = request.getParameter("title");
            String url = BUCKET_NAME + "/api/download/" + email + "/" + title + "?email=" + email;
            System.out.println("email: " + email);
            DatastoreHelper.addVideo(email, blobInfo.getSize(), blobInfo.getMediaLink(), title);

            StringBuilder responseMail = new StringBuilder();
            responseMail.append("Merci d'avoir utilisé Poly Share.\n\nTitre:\t").append(title).append("\n").append("Lien: \t").append(url);
            MailUtil.sendEmail(request.getParameter("email"), responseMail.toString());
            setupDelete(datastoreHelper, email, blobInfo);
            response.setContentType("text/plain");
            response.setStatus(201);
            response.getWriter().println("File uploaded");

        } catch (ServletException e) {
            response.sendError(500,e.toString());
        } catch (UserNotFoundException | NullPointerException e) {
            response.sendError(401,"The user " + email + " is not registered.");
            MailUtil.sendEmail(request.getParameter("email"), "Lol non noob");
        } catch (NoobRateExceedException e) {
            response.sendError(403,"Rate exceed for user " + email);
            response.getWriter().println("Rate exceed");
            response.getWriter().println(e);
            MailUtil.sendEmail(request.getParameter("email"), "Lol non noob");
        }

    }

    private void setupDelete(DatastoreHelper datastoreHelper, String email, BlobInfo blobInfo) throws ServletException {
        Entity user = null;
        try {
            user = DatastoreHelper.getUser(email);
        } catch (UserNotFoundException ignored) {
            ignored.printStackTrace();
        }
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
        private static DatastoreService datastore = null;

        static {
            datastore = DatastoreServiceFactory.getDatastoreService();
        }

        private BlobInfo blobInfo;
        private Entity user;

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
