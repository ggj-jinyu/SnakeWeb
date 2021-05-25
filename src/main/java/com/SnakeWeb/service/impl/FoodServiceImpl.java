package com.SnakeWeb.service.impl;

import com.SnakeWeb.entity.Food;
import com.SnakeWeb.service.FoodService;

import java.util.Random;

public class FoodServiceImpl implements FoodService {
    @Override
    public Food init() {
        Random r = new Random();
        //[0,400]-->[0,16]*25
        int foodX = r.nextInt(17)*25; //[0,17)*25
        //[0,400]-->[3,16]*25
        int foodY = r.nextInt(17)*25; //[0,17)*25
        Food food = new Food();
        food.setX(foodX);
        food.setY(foodY);

        return food;
    }
}
