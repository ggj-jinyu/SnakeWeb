package com.SnakeWeb.entity;

public class ResInfo {
    private String image_base64;
    private boolean startgame;
    private int score;

    public String getImage_base64() {
        return image_base64;
    }

    public void setImage_base64(String image_base64) {
        this.image_base64 = image_base64;
    }

    public boolean isStartgame() {
        return startgame;
    }

    public void setStartgame(boolean startgame) {
        this.startgame = startgame;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
