package com.SnakeWeb.web;

import com.SnakeWeb.entity.Food;
import com.SnakeWeb.entity.Snake;
import com.SnakeWeb.service.FoodService;
import com.SnakeWeb.service.SnakeService;
import com.SnakeWeb.service.impl.FoodServiceImpl;
import com.SnakeWeb.service.impl.SnakeServiceImpl;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;



@WebServlet(name = "StartGame", value = "/startGame")
public class StartGame extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //判断小蛇是否存活,若小蛇存活,则无需初始化
        Snake snake_session = (Snake) request.getSession().getAttribute("snake");
        if(snake_session!=null) return;
        //初始化
        SnakeService service = new SnakeServiceImpl();
        Snake snake = service.init();
        FoodService foodService = new FoodServiceImpl();
        Food food = foodService.init();
        //存储snake对象到session中
        HttpSession session = request.getSession();
        session.setAttribute("snake",snake);
        session.setAttribute("food",food);
    }
}
