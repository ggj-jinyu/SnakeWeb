package com.SnakeWeb.web;

import com.SnakeWeb.entity.Food;
import com.SnakeWeb.entity.Snake;
import com.SnakeWeb.service.FoodService;
import com.SnakeWeb.service.impl.FoodServiceImpl;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet(name = "SportServlet", value = "/sportServlet")
public class SportServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Snake snake = (Snake) session.getAttribute("snake");
        Food food = (Food) session.getAttribute("food");
        //获取相关数据
        int length = snake.getLength();
        int[] snakeX = snake.getArray_x();
        int[] snakeY = snake.getArray_y();
        //后一节身子走到前一节身子的位置
        for (int i=length-1; i>0; --i) {
            snakeX[i] = snakeX[i-1];
            snakeY[i] = snakeY[i-1];
        }
        //动头
        switch (snake.getDirection()){
            case "R": snakeX[0] += 25;break;
            case "L": snakeX[0] -= 25;break;
            case "U": snakeY[0] -= 25;break;
            case "D": snakeY[0] += 25;break;
            default:
                System.out.println("snake direction is null!!");break;
        }
        //吃到食物
        if(snakeX[0] == food.getX() && snakeY[0] == food.getY()){
            snake.setLength(++length);//长度加一
            snakeX[length-1]=500;//初始化小蛇新的body的位置
            snakeY[length-1]=500;
            //改变食物坐标
            session.removeAttribute("food");
            FoodService service = new FoodServiceImpl();
            Food newFood = service.init();
            session.setAttribute("food",newFood);
            //分数累加
            int score = snake.getScore();
            score += (length-3)/10==0 ? 1:(length-3)/10*10;
            snake.setScore(score);
        }

        //死亡判定
        //判断是否撞墙
        if(snakeX[0]<0 || snakeY[0]<0 || snakeY[0]>400 || snakeX[0]>400){
            snake.setAlive(false);//撞墙则更新snake的alive属性为false
            session.removeAttribute("snake");
            session.setAttribute("snake",snake);
        }else {
            //判断是否咬到自己
            for(int i=1; i<length; ++i){
                if(snakeX[0]==snakeX[i] && snakeY[0]==snakeY[i]){//坐标重合即为咬到
                    snake.setAlive(false);//咬到则更新snake的alive属性为false
                    session.removeAttribute("snake");
                    session.setAttribute("snake",snake);
                }
            }
        }
        //转发到canvasServlet
        request.getRequestDispatcher("/canvasServlet").forward(request,response);
    }
}
