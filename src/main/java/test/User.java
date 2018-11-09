package test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    String email;
    List<String> availableVideos = new ArrayList<>();
    int score;

    public User(String email, int score) {
        this.email = email;
        this.score = score;
    }

    public String getEmail() {
        return email;
    }

    public int getScore() {
        return score;
    }

    public void updateScore(int points) {
        this.score += points;
    }

    public boolean addNewVideo(Video video){
        return this.availableVideos.add(video.stringFormat());
    }
}
