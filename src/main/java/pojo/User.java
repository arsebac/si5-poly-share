package pojo;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class User implements Serializable {
    String email;
    List<Video> availableVideos = new LinkedList<>();
    long score;

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

}
