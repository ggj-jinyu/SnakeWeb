package com.SnakeWeb.web;

import com.SnakeWeb.entity.Food;
import com.SnakeWeb.entity.ResInfo;
import com.SnakeWeb.entity.Snake;
import com.SnakeWeb.utils.CanvasUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@WebServlet(name = "CanvasServlet", value = "/canvasServlet")
public class CanvasServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //服务器通知浏览器不要缓存
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("expires","0");
        response.setContentType("application/json;charset=utf-8");

        HttpSession session = request.getSession();
        Snake snake = (Snake) session.getAttribute("snake");
        Food food = (Food) session.getAttribute("food");

        String png_base64;
        ResInfo info = new ResInfo();

        if(snake==null){
            png_base64 = CanvasUtils.generate();
        }else{
            png_base64 = CanvasUtils.generate(snake, food);
            info.setStartgame(snake.getAlive());
            info.setScore(snake.getScore());
        }
        info.setImage_base64(png_base64);

        if(snake!=null && !snake.getAlive()) {
            session.removeAttribute("snake");
            session.removeAttribute("food");
        }
        //将内存中的图片输出到浏览器
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), info);

    }
}
