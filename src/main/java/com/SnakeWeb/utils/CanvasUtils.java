package com.SnakeWeb.utils;

import com.SnakeWeb.entity.Food;
import com.SnakeWeb.entity.Images;
import com.SnakeWeb.entity.Snake;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class CanvasUtils {
    private static int width = 425; //图片宽度
    private static int height = 425;//图片高度

    private CanvasUtils(){}//构造方法私有

    //获取画笔，并画好图片背景
    private static Graphics2D getGraphics(BufferedImage image){
        Graphics2D graphics = image.createGraphics();//获取图片的画笔
        graphics.setColor(new Color(218,226,219));
        graphics.fillRect(0,0, width,height); //画一个矩形
        return graphics;
    }

    //BufferedImage转换成Data url形式的ImageBase64串
    private static String bufferedImageToDataUrl(BufferedImage image){
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){ //构建一个BAOS流，使用完自动关闭
            ImageIO.write(image, "png", baos);//将bufferedImage写入流中
            byte[] bytes = baos.toByteArray();//转换成字节
            BASE64Encoder encoder = new BASE64Encoder();
            String png_base64 =  encoder.encodeBuffer(bytes).trim();//转换成base64串
            //删除 \r\n
            png_base64 = png_base64.replaceAll("\n", "").replaceAll("\r", "");
            return "data:image/jpg;base64,"+png_base64;
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static String generate(){
        //新建BufferedImage
        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = getGraphics(image);//获取画笔
        graphics.drawImage(Images.right.getImage(),125,150,null);//画一个向右的蛇头
        graphics.drawImage(Images.body.getImage(),100,150,null);//画身体
        graphics.drawImage(Images.body.getImage(),100,175,null);
        return bufferedImageToDataUrl(image);
    }

    public static String generate(Snake snake, Food food){
        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = getGraphics(image);
        //获取相关数据
        int length = snake.getLength();
        String direction = snake.getDirection();
        int[] snakeX = snake.getArray_x();
        int[] snakeY = snake.getArray_y();
        //画食物
        graphics.drawImage(Images.food.getImage(),food.getX(), food.getY(), null);
        //画头
        switch (direction){
            case "R":
                graphics.drawImage(Images.right.getImage(),snakeX[0],snakeY[0],null);break;
            case "L":
                graphics.drawImage(Images.left.getImage(),snakeX[0],snakeY[0],null);break;
            case "U":
                graphics.drawImage(Images.up.getImage(),snakeX[0],snakeY[0],null);break;
            case "D":
                graphics.drawImage(Images.down.getImage(),snakeX[0],snakeY[0],null);break;
            default:break;
        }
        //画身体
        for(int i=1;i<length;++i){
            graphics.drawImage(Images.body.getImage(),snakeX[i],snakeY[i],null);
        }

        //死亡判定，死亡则画一个提示
        if(!snake.getAlive()){
            graphics.setColor(Color.BLUE);
            graphics.setFont(new Font("MV boli",Font.BOLD,30));
            graphics.drawString("GAME OVER",120,180);
            graphics.drawString("SCORE:"+snake.getScore(),120,230);
        }

        return bufferedImageToDataUrl(image);
    }
}
