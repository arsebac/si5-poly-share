package pojo;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User implements Serializable {
    String email;
    String availableVideos = "";
    int score;

    public User() {
    }

    public User(String email, int score) {
        this.email = email;
        this.score = score;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvailableVideos() {
        return availableVideos;
    }

    public void setAvailableVideos(String availableVideos) {
        this.availableVideos = availableVideos;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void updateScore(int points) {
        this.score += points;
    }

    public void addNewVideo(Video video){
        String separator = ",";
        if(this.availableVideos.equals("")){
            separator = "";
        }
        this.availableVideos += separator+video.stringFormat();
    }

    public List<Video> getVideos(){
        List<Video> videos = new ArrayList<>();
        String[] result = this.availableVideos.split(",");
        for (String videoString : result) {
            String[] temp = videoString.split("&");
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM HH:mm:ss z yyyy");
            Date date = null;
            try {

                date = sdf.parse(temp[0]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            videos.add(new Video(Integer.parseInt(temp[1]), date, temp[0]));
        }
        return videos;
    }
}
