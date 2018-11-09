package test;



import java.io.Serializable;
import java.util.Date;

public class Video implements Serializable {
    int videoId;
    Date uploadDate;

    public Video(int videoId, Date uploadDate) {
        this.videoId = videoId;
        this.uploadDate = uploadDate;
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
        return uploadDate.toString()+"-"+videoId;
    }
}
