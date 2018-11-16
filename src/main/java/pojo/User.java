package pojo;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class User implements Serializable {
    public long score;
    private String email;
    private List<Video> availableVideos = new LinkedList<>();
    public User() {
    }

    public User(String email, int score) {
        this.email = email;
        this.score = score;
    }

    public void updateScore(int points) {
        this.score += points;
    }

    public void addNewVideo(Video video){
        availableVideos.add(video);
    }

    public long getScore() {
        return score;
    }

    public String getEmail() {
        return email;
    }
}
