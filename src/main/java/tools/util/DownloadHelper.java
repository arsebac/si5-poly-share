package tools.util;

import pojo.Video;

import javax.servlet.http.HttpServletRequest;

public class DownloadHelper {
    public static void sendVideoByMail(HttpServletRequest request) {
        String email = request.getParameter("email");
        String videoTitle = request.getParameter("videoTitle");
        String videoOwner = request.getParameter("videoOwner");

        DatastoreHelper datastoreHelper =  (DatastoreHelper) request.getServletContext().getAttribute("datastoreHelper");
        Video video = datastoreHelper.getVideo(videoOwner, videoTitle);

        MailUtil.sendEmail(email, "Link to download the video you requested: " + video.getUrl());
    }
}
