package tools.util;

import exceptions.NoobRateExceedException;
import exceptions.UserNotFoundException;
import pojo.Video;

import javax.servlet.http.HttpServletRequest;

public class DownloadHelper {
    public static void sendVideoByMail(DatastoreHelper datastoreHelper, String email, String videoTitle, String videoOwner) {
        try {
            Video video = datastoreHelper.getVideo(videoOwner, videoTitle, email);
            MailUtil.sendEmail(email, "Link to download the video you requested: " + video.getUrl());
        } catch (NoobRateExceedException e) {
            MailUtil.sendEmail(email, "lol non noob");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

    }
}
