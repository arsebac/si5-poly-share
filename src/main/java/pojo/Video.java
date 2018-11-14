package pojo;



import java.io.Serializable;

public class Video implements Serializable {
    String url;
    String title;
    String uploadDate;

    public Video(String url, String uploadDate, String title) {
        this.url = url;
        this.uploadDate = uploadDate;
        this.title = title;
    }

}
