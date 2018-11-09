package test;



import java.io.Serializable;
import java.util.Date;

public class Video implements Serializable {
    int videoId;
    String title;
    Date uploadDate;

    public Video(int videoId, Date uploadDate, String title) {
        this.videoId = videoId;
        this.uploadDate = uploadDate;
        this.title = title;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String stringFormat(){
        return uploadDate.toString()+"&"+videoId+"&"+title;
    }
}
