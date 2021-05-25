package com.SnakeWeb.entity;

public class Snake {
    private int length; //长度
    private boolean alive;  //存活与否
    private int[] array_x; //x坐标
    private int[] array_y; //y坐标
    private String direction; //行走方向
    private int score;  //分数

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean getAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isAlive() {
        return alive;
    }

    public int[] getArray_x() {
        return array_x;
    }

    public void setArray_x(int[] array_x) {
        this.array_x = array_x;
    }

    public int[] getArray_y() {
        return array_y;
    }

    public void setArray_y(int[] array_y) {
        this.array_y = array_y;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
