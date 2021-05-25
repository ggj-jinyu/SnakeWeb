package com.SnakeWeb.service.impl;

import com.SnakeWeb.entity.Snake;
import com.SnakeWeb.service.SnakeService;

import java.util.ArrayList;
import java.util.List;

public class SnakeServiceImpl implements SnakeService {
    @Override
    public Snake init() {
        //创建一条小蛇，并初始化数据
        Snake snake = new Snake();
        int[] arrayX = new int[260];
        arrayX[0]=125;arrayX[1]=100;arrayX[2]=100;
        int[] arrayY = new int[260];
        arrayY[0]=150;arrayY[1]=150;arrayY[2]=175;
        snake.setArray_x(arrayX);
        snake.setArray_y(arrayY);
        snake.setLength(3);
        snake.setAlive(true);
        snake.setDirection("R");
        snake.setScore(0);

        return snake;
    }
}
