package routes;

import tools.util.CloudStorageHelper;

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
        
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        CloudStorageHelper storageHelper = (CloudStorageHelper) request.getServletContext().getAttribute("storageHelper");
        try {
            String videoUrl = storageHelper.getVideoUrl(request, BUCKET_NAME);
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
    
    public static String getInfo() {
        return "Version: " + System.getProperty("java.version")
                + " OS: " + System.getProperty("os.name")
                + " User: " + System.getProperty("user.name");
    }
    
}
