package com.SnakeWeb.entity;

import javax.swing.*;
import java.net.URL;

/**
 * 获取项目里的图片对象
 */
public class Images {
    //封装所有对象
    private static URL bodyURL = Images.class.getResource("/imgs/body.png");
    public static ImageIcon body = new ImageIcon(bodyURL);
    private static URL upURL = Images.class.getResource("/imgs/up.png");
    public static ImageIcon up = new ImageIcon(upURL);
    private static URL downURL = Images.class.getResource("/imgs/down.png");
    public static ImageIcon down = new ImageIcon(downURL);
    private static URL rightURL = Images.class.getResource("/imgs/right.png");
    public static ImageIcon right = new ImageIcon(rightURL);
    private static URL leftURL = Images.class.getResource("/imgs/left.png");
    public static ImageIcon left = new ImageIcon(leftURL);
    private static URL foodURL = Images.class.getResource("/imgs/food.png");
    public static ImageIcon food = new ImageIcon(foodURL);
}
