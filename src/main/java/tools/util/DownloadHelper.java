package tools.util;

import exceptions.NoobRateExceedException;
import exceptions.UserNotFoundException;
import pojo.Video;

import javax.servlet.http.HttpServletRequest;

public class DownloadHelper {
    public static void sendVideoByMail(HttpServletRequest request) {
        String email = request.getParameter("email");
        String videoTitle = request.getParameter("videoTitle");
        String videoOwner = request.getParameter("videoOwner");

        DatastoreHelper datastoreHelper = (DatastoreHelper) request.getServletContext().getAttribute("datastoreHelper");
        Video video = null;
        try {
            video = datastoreHelper.getVideo(videoOwner, videoTitle, email);
            MailUtil.sendEmail(email, "Link to download the video you requested: " + video.getUrl());
        } catch (NoobRateExceedException e) {
            MailUtil.sendEmail(email, "lol non noob");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

    }
}
