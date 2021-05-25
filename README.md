# SnakeWeb

### 一、项目选题

​		本项目选题为Web应用形式的贪吃蛇，其原理与验证码的原理相似，只不过验证码是点击才换图片，而本项目是运用了html的定时器，每隔150ms换一张图片，这样小蛇就动了起来。



### 二、技术选型、开发工具、运行环境

#### 1.技术选型

* 前端：
  * Bootstrap：前端开发框架
  * jQuery：JavaScript框架
  * Html：视图

* 后端：
  * Servlet：前端控制器
  * Jackson：json序列化工具
  * Junit4：test工具
  * Maven：项目构建工具

#### 2.开发工具

​		Intellij IDEA 2021.1 + Chrome 90.0

#### 3.运行环境

​		Jdk1.8 + window10
​		Tomcat9.0.431：免费的开放源代码的Web 应用服务器



### 三、需求分析

#### 1.需求分析

​	1) html页面：展示内容、并能与用户交互
​	2) 点击按钮后开始游戏，小蛇开始移动
​	3) 小蛇吃到食物后，变长并得分
​	4) 再次点击按钮则暂停，又再一次点击则继续游戏
​	5) 小蛇撞墙或吃到自己则会死亡

#### 2.实际运行过程分析

​		1) 发布项目；
​		2) 登录网址,展示页面，其中游戏画面区域会向后台发出请求，后台通过bufferedImage画好图片后生成imageBase64(data url形式的图片)并返回，前台根据imageBase64设置初始画面；
​		3) 点击“游戏开始”按钮，向后端发出游戏开始的请求，后端调用service进行数据初始化，并把snake、food的数据存入session。同时该按钮的html内容变成“暂停游戏”；
​		4) 游戏开始的请求完成后，前台设定一个循环定时器，每隔150ms秒向后台发送“小蛇走动”的请求。
​		5) 后台处理session中的snake的坐标信息，让snake动起来。在此同时，会判断小蛇蛇头与食物的坐标是否重合，即判断小蛇是否吃到食物；若吃到，则让小蛇长度加一，得分增加。处理完后转发给另一个控制器。
​		6) 该控制器负责把iamgeBase64以及分数等其他数据封装返回。前台根据返回数据设置图片、分数；
​		7) 在页面加载完毕时，就会在当前页面设定键盘按键的监听器，监听用户是否按下↑↓←→以及WSAD的按键，若游戏已经开始，则会往后台发送请求并携带数据，后台根据数据设置小蛇的移动方向；
​		8) 用户再次点击按钮，则会清除定时器，不再向后台发送请求，这样就使游戏暂停了。当用户又点击按钮，则会新建一个定时器，小蛇又会动起来。



### 四、系统设计

#### 1.页面设计

![image-20210525200134227](C:\Users\hasee\AppData\Roaming\Typora\typora-user-images\image-20210525200134227.png)

#### 2.后台设计

##### 1) entity(实体包)，类图如下图所示：

![image-20210525200142208](C:\Users\hasee\AppData\Roaming\Typora\typora-user-images\image-20210525200142208.png)

其中，ResInfo是结果封装类，便于把返回前端的所有数据封装成一个ResInfo对象返回给前台；Images是用来封装图片，先是用反射获取图片url,再生成public static ImageIcon，以便其它类访问

##### 2)service层，由于没有dao层，service层相对简单，如下图所示

![image-20210525200148224](C:\Users\hasee\AppData\Roaming\Typora\typora-user-images\image-20210525200148224.png)

两个service只有init()方法，该方法初始化snake/food后返回

##### 3)web层：有四个控制器

①　CanvasServlet——画布控制器:
路径为“/canvasServlet”，生成ResInfo实例返回给前端
②　ChangeDirection——方向控制器：
路径为“/changeDirection”，用于控制小蛇的移动方向
③　SportServlet——移动控制器：
路径为“/sportServlet”，用来处理小蛇在图片的坐标，让小蛇动起来
④　StartGame——游戏启动器：
路径为“/startGame”，用来调用service来初始化数据，并把snake、food对象存入session中



### 五、系统实现

#### 1.项目架构

​		![image-20210525200157099](C:\Users\hasee\AppData\Roaming\Typora\typora-user-images\image-20210525200157099.png)

#### 2.项目配置

​		本项目由maven构建，所用jar包需要在pom.xml文件导入依赖
​		本项目所有依赖为
​		Javax.servlet-api 4.0.1	junit 4.13.2	
​		Jackson-core 2.9.4	Jackson-databind 2.9.4	jackson-annotations 2.9.4

#### 3.关键实现代码

##### (1)Index.html

```html
<script type="text/javascript">
    let intervalNum;   //定时器标识号
    let startFlag = true;  //开始开关，true：游戏未开始，可以点击按钮开始
    //入口函数
    $(function () {       
        //向canvasServlet发送请求，返回ResInfo对象
        //设置游戏区域初始画面为 ResInfo.image_base64
        $.get("canvasServlet",null,function (info){
            $("#img_area").get(0).src = info.image_base64;
        })
        //点击按钮，调用startGame()
        $("#btn_start").click(function () { 
            startGame();
        });
        //监听键盘按键
        $(document).keydown(function(event){  
            let direction = null;
            switch (event.keyCode){
                case 38:  case 87: direction="U";break;  //38：↑   87：w
                case 40:  case 83: direction="D";break;  //40：↓   83：s
                case 37:  case 65: direction="L";break;  //37：←  65：A
                case 39:  case 68: direction="R";break;  //39：→  68：D
                default: break;
            }
            //游戏开始，才向changeDirection发送请求（请求携带direction信息）
            if(!startFlag) $.get("changeDirection",{direction:direction})
        });
    })

    function startGame(){
        if(startFlag){ //true: 游戏未开始，可以开始
            startFlag = false;
            $("#btn_start").html("暂停游戏");
            $.get("startGame",null,function (){//向startGame发送请求
                //设定定时器，每隔150ms执行一次
                intervalNum = setInterval(sport,150);
            });
        }else {     //false: 游戏已经开始，可以暂停
            startFlag = true;
            $("#btn_start").html("开始游戏");
            clearInterval(intervalNum);    //清除定时器
        }
    }

    function sport() {
        //info: string image_base64  boolean startgame  int score
        $.get("sportServlet", null, function (info) {
            $("#img_area").get(0).src = info.image_base64;//设置图片
            $("#score").html(info.score) //设置分数
            if(!info.startgame){      //判断游戏是否结束，若结束则执行下列语句
                startFlag = true;
                $("#btn_start").html("开始游戏")
                clearInterval(intervalNum);
            }
        });
    }
</script>
```

##### （2）CanvasUtils——画图工具类，生成Data url形式的ImageBase64串

```java
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
		//构建一个BAOS流，使用完自动关闭
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            ImageIO.write(image, "png", baos);//将bufferedImage写入流中
            byte[] bytes = baos.toByteArray();//转换成字节
            BASE64Encoder encoder = new BASE64Encoder();
            String png_base64 = encoder.encodeBuffer(bytes).trim();//转换成base64串
            png_base64 = png_base64.replaceAll("\n", "").replaceAll("\r", "");//删除 \r\n
            return "data:image/jpg;base64,"+png_base64;
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }
    
    public static String generate(){//无参方法，用于生成页面的游戏区域初始画面
        //新建BufferedImage
        BufferedImage image = new 
		BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = getGraphics(image);//获取画笔
        graphics.drawImage(Images.right.getImage(),125,150,null);//画一个向右的蛇头
        graphics.drawImage(Images.body.getImage(),100,150,null);//画身体
        graphics.drawImage(Images.body.getImage(),100,175,null);
        return bufferedImageToDataUrl(image);
    }
    
    public static String generate(Snake snake, Food food){//有参方法
        BufferedImage image = new 
		BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
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
            graphics.drawString("GAME OVER",120,200);
            graphics.drawString("SCORE:"+snake.getScore(),120,250);
        }
        return bufferedImageToDataUrl(image);
    }
}
```

##### （3）CanvasServlet（省略了其它不重要的代码）

```java
//服务器通知浏览器不要缓存 
response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
response.addHeader("Cache-Control", "post-check=0, pre-check=0");
response.setHeader("Pragma", "no-cache");
response.setHeader("expires","0");
response.setContentType("application/json;charset=utf-8");

//获取session,并获取session中的snake、food
HttpSession session = request.getSession();
Snake snake = (Snake) session.getAttribute("snake");
Food food = (Food) session.getAttribute("food");

//封装数据
String png_base64;
ResInfo info = new ResInfo();//new一个ResInfo
//session中没有小蛇，说明游戏还没开始，只是设定游戏初始画面
if(snake==null){
    png_base64 = CanvasUtils.generate();//调用CanvasUtils.generate()
}else{//小蛇存在，游戏已经开始，封装所有相关数据
    png_base64 = CanvasUtils.generate(snake, food);
    info.setStartgame(snake.getAlive());
    info.setScore(snake.getScore());
}
info.setImage_base64(png_base64);

//若小蛇死亡则删掉session中的snake、food
if(snake!=null && !snake.getAlive()) {
    session.removeAttribute("snake");
    session.removeAttribute("food");
}

//将ResInfo对象以json形式返回给浏览器
ObjectMapper mapper = new ObjectMapper();//使用的是jackson中的特有方法
mapper.writeValue(response.getOutputStream(), info);
```

##### （4）StartGame

```java
Snake snake_session = (Snake) request.getSession().getAttribute("snake");
if(snake_session!=null) return;//判断小蛇是否存活,若小蛇存活,则无需初始化

//数据初始化
SnakeService service = new SnakeServiceImpl();
Snake snake = service.init();
FoodService foodService = new FoodServiceImpl();
Food food = foodService.init();

//存储snake对象到session中
HttpSession session = request.getSession();
session.setAttribute("snake",snake);
session.setAttribute("food",food);
```

##### （5）SportServlet

```java
Snake snake = (Snake) request.getSession().getAttribute("snake");
Food food = (Food) request.getSession().getAttribute("food");
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
    snakeX[length-1]=500; snakeY[length-1]=500;//初始化小蛇新的body的位置
    //改变食物坐标
    session.removeAttribute("food");
    Food newFood = new FoodServiceImpl().init();
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
```

##### （6）ChangeDirection

```java
@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession();
    Snake snake = (Snake) session.getAttribute("snake");	
	//如果session中没有snake，说明游戏没开始，直接return
    if(snake==null) return;
	//获取请求中的direction参数
    String direction = request.getParameter("direction");
	//小蛇当前的朝向是RL的话，那它只能向Up、Down移动
	//小蛇当前的朝向是UD的话，那它只能向Right、Left移动
    switch (snake.getDirection()){
        case "R":
        case "L":
            if("U".equals(direction)||"D".equals(direction)) 
		change(session,snake,direction);  break;
        case "U":
        case "D":
            if("R".equals(direction)||"L".equals(direction)) 
		change(session,snake,direction);  break;
        default:
            System.err.println("snake direction is null!!!");break;
    }
}

//改变session中snake的direction属性
private void change(HttpSession session, Snake snake, String direction){
    session.removeAttribute("snake");
    snake.setDirection(direction);
    session.setAttribute("snake",snake);
}
```





