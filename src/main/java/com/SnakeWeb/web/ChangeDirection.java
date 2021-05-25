package com.SnakeWeb.web;

import com.SnakeWeb.entity.Snake;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "ChangeDirection", value = "/changeDirection")
public class ChangeDirection extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Snake snake = (Snake) session.getAttribute("snake");
        if(snake==null) return;
        String direction = request.getParameter("direction");
        switch (snake.getDirection()){
            case "R":
            case "L":
                if("U".equals(direction)||"D".equals(direction)) change(session,snake,direction);break;
            case "U":
            case "D":
                if("R".equals(direction)||"L".equals(direction)) change(session,snake,direction);break;
            default:
                System.err.println("snake direction is null!!!");break;
        }
    }

    private void change(HttpSession session, Snake snake, String direction){
        session.removeAttribute("snake");
        snake.setDirection(direction);
        session.setAttribute("snake",snake);
    }
}
