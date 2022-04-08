# seckill3
# 秒杀系统笔记


### 整体架构

- Spring解决的是整个后端系统的对象管理问题，目的是解耦，可插拔

- 利用MyBatis解决数据层

- 利用Spring MVC 解决视图层,Controller接收浏览器的请求，再传给业务层实现，如果需要与数据库交互，再传给数据层实现；得到结果后Controller将请求的内容通过Model对象封装传给ViewResolver；再利用ViewResolver将数据传给浏览器。



### 环境搭建

- 在服务器上装好MySQL，把准备好的表上传上去，本地用Nvicat连接服务器数据库
- 本地安装开发工具 IDEA JDK Maven VSCode（编译前端代码）
- IDEA导入后台代码，修改配置文件，运行
- Vscode导入前端代码，运行



### Spring + Spring MVC + Spring Boot

### spring

**IoC和AOP依赖ApplicationContext和BeanFactory实现，他们俩都是bean工厂**

- **BeanFactory**是spring的原始接口，是一个比较底层的API，针对原始结构的实现类功能比较单一，主要是spring内部人士使用，BeanFactory接口实现的容器，**特点是在每次获取对象时才会创建对象。**

- **ApplicationContext**继承了BeanFactory接口，拥有BeanFactory的全部功能，并且扩展了很多高级特性，是开发者使用的，**每次容器启动时就会创建所有的对象。**



==**spring利用Ioc（控制反转）管理对象**==，就是把对对象的控制交给Spring来做，对象可以自动装载，降低对象之间的耦合度，可插拔（想要替换某一个bean很方便），通常情况下，需要单例的用IoC来管理，因为这是可复用的，有很多实例化对象的（例如实体类）就不用IoC。

- **同时IoC可以管理bean的生命周期**（例如用`@PostConstruct`和`@PreDestory`在bean的初始化和销毁前做一些业务）。
- **也可以管理bean的作用域**，spring默认只会实例化一个对象，用`@Scope("prototype")`就变成每次都实例化一个bean

==**AOP面向切面编程**==

定义一个切面类，在该类里定义PointCut切点（也就是切入的位置）和Advice通知（在该切面完成的工作和使用时间），从而在相应的JointPoint连接点（即相应的那个方法）中生效。在代码中体现的非常简单，不需要修改任何源代码，写一个切面类就可以了。




### Spring MVC

黄蓝红四个地方的组件是最重要的

​	①所有前端的请求被==**DispatcherServlet**==（DispatcherSevlet是Spring MVC最核心的组件，负责将请求分发接收），根据 ==**HandlerMapping**== 映射到 ==**Handler**==处理器——⽣成 Handler 和 ==**HandlerInterceptor**==（处理器拦截器，是一个接口，实现该接口可以进行一些拦截处理，可以分别在调用HandlerAdapter之前、之后和视图响应数据之后三个地方进行拦截），Handler 和 HandlerInterceptor 以 ==**HandlerExecutionChain**==（处理器执⾏链，包括两部分内容： Handler 和HandlerInterceptor） 的形式⼀并返回给DispatcherServlet；

​	②DispatcherServlet 通过 **==HandlerAdpater==** （处理器适配器， Handler 执⾏业务⽅法之前，需要进⾏⼀系列的操作包括表单的数据验证、数据类型的转换、将表单数据封装到 POJO 等，这⼀些列操作都是由HandlerAdapter 完成）调⽤ Handler 的⽅法完成业务逻辑处理。 此时会返回⼀个 ==**ModelAndView**==（封装了模型数据和视图信息  ） 对象给 DispatcherServlet，**但是这个项目返回的都是json格式的数据，那么就直接由HandlerAdapter返回**（添加@ResponseBody注解）；

​	③随后DispatcherServlet 将获取的 ModelAndView 对象传给 ==**ViewResolver**==视图解析器，将逻辑视图解析成物理视图。  然后DispatcherServlet 通过 View 将模型数据填充到视图中，DispatcherServlet 将渲染之后的视图响应给客户端。  






代码：

公共部分：

- ==ErrorCode==：定义了一些异常编号。
- ==BusinessException==： 有两个成员变量①编号（在ErrorCode里定义了）②描述信息。当项目遇到异常的时候，全部包装成这个异常往外抛，**这个异常继承了`RuntimeException`(可以try-catch也可以不try-catch)，因为项目是分层的，一层一层的调用，所以无论在哪一层抛异常，都会被Controller感受到，所以在controller统一处理就可以了，不用很麻烦的在每一层都处理。** 在controller如何处理（在controller写了一个ExceptionAdvice类，加上@ControllerAdvice注解，在该类里定义一个方法，参数是异常(Exception e)，该方法加@ExceptionHandler（没有捕获的异常会以参数的形式传入加了@ExceptionHandler注解的那个方法中）和@ResponseBody表示在发生异常时调用该方法，返回异常的json格式数据）
- ==ResponseModel==：用来统一视图层返回给前端的json数据，例如{status：0，{id：1，title：“”}}（status==0表示业务状态成功，不加状态码前端就无法知道你返回的数据是否正确？数据格式是什么？）。
- ==Toolbox==：里面有一个md5加密方法，将用户的明文密码存成密文。
- 创建两个资源文件，分为开发时用、测试时用，在创建一个开关文件，选择使用哪个资源配置文件

# 1. 用户注册与登录


## 1.1 ==代码实现==

**定义实体类User**，每个成员变量加注解@NotNull(针对Integer类型)或@NotBlank(针对string类型)，定义一个ObjectValidator验证类，里面定义一个validate方法，该方法传入一个Object类型的对象，用Validator的validate方法验证是否为空，定义一个map，如果有问题就把错误信息装入map。

**dao层**：

- 写一个**UserMapper接口**，定义需要用到的各种方法，
- 再在**UserMapper.xml**写SQL语句

**service层**：

- 写一个**UserService接口**，定义三个方法`register(User user)`,`login(String phone, String password)`,`findUserById(int id)`

- **UserServiceImpl**继承该接口，注入userMapper和validator，
  - 定义**register方法**，先判断传入的user是否有空值，然后再插入到数据库
  - 定义**login方法**，传入手机号和密码(密码是加密后的密码)，如果查不到返回错误信息，查得到就返回user（表示验证通过）

**controller层：** 

- 访问路径`/user`,注入userService
  - **getOTP方法**（GET请求）将传入的手机号与生成的随机验证码绑定到session里，返回`new ResponseModel()`(ResponseModel()默认是成功的)
  - rigister方法（POST请求）先验证OTP，如果没有问题，把密码加密一下，再调注册方法，返回`new ResponseModel()`代表成功
  - **login方法**（POST请求）先判断是否为空，然后调登录方法，把得到的user信息存入session中，返回`new ResponseModel()`代表成功
  - logout方法（GET请求），注销session就可以了
  - **getUser方法**（GET请求），右上角显示登录信息查登录状态的方法，通过session查询user信息



## 1.2 ==状态管理==

首先，跨域问题不适合用AOP来做，因为是否登录的某些功能是根据url进行的过滤，而不是根据某个方法进行的过滤（AOP主要是面向某个方法或者某个对象进行的过滤），适合用MVC的拦截器处理（拦截器面向url进行的过滤），因为一个拦截器可以拦截多个controller

写一个`LoginCheckInterceptor`拦截器，重写preHandle方法（还有两个方法分别表示返回ModelAndView给 DispatcherServlet的时候，和操作结束后返回ModelAndView给ViewResolver的时候）表示在操作之前过滤，检查登录状态，再写一个配置类实现WebMvcConfigurer接口，注册该拦截器并配置拦截器的拦截url（在本项目中只拦截下单这一个路径 ）



## 1.3 ==**跨域问题**==

​		跨域（CORS）是指不同域名之间相互访问。跨域，指的是浏览器不能执行其他网站的脚本，它是由浏览器的同源策略所造成的，是浏览器对于JavaScript所定义的安全限制策略。

### **什么情况会跨域**

- 同一协议， 如http或https
- 同一IP地址, 如127.0.0.1
- 同一端口, 如8080

以上三个条件中有一个条件不同就会产生跨域问题。

### 解决方法

1. **《规避法》**利用Nginx做代理，规避跨域问题，所有请求都访问Nginx，由Nginx分配给相应的地址。以`/static`开头的请求都交给前端服务器，以`/other`开头的请求交给后端服务器，实现代理即可。弊端是前端和后端的url要有严格的规范，要不然Nginx不好做判断，缺乏了一些灵活性





2. **《欺骗法》**前端使用JSONP方式实现跨域调用，利用<script>标签不受同源策略的限制 ①使用<script>标签让浏览器认为需要获取js，然后就会访问服务器 ②返回需要的数据并转为json，拼成一个show({...})的字符串返回给浏览器 ③浏览器会认为这是一个函数，通过提前写好的方法调用这个函数，得到需要的数据。弊端：这个请求只能是get 不能说post，而且很麻烦，适用范围太小




3. 《真正后端支持的方式》在方法上添加注解`@CrossOrigin`，并设定允许进行跨域请求的地址，即可



# 2. 商品列表与详情


**==把商品详情和库存存成两张表==，如果不拆，在一张表里，下单减库存的时候，一定要锁定这个商品的整行，在高并发的场景下，影响其他人的读，拆开之后，锁的只是库存表的一行，不影响商品详情的读**

## 2.1 ==代码实现==

**定义实例类**：

- Item商品，除了id name等属性，还加入了ItemStock和Promotion两个对象属性，并且也加了@NotNull和@NotBlank
- ItemStock商品库存，加了@Min 限制商品库存最小值为0
- Promotion 活动

**dao层**：

- ItemMapper 增删改查等方法，其中有一个查询活动商品，ItemMapper.xml写SQL语句，查询活动商品就是查询当前时间在商品活动范围内的商品
- ItemStockMapper 定义了根据ID查库存数据，ItemStockMapper.xml写SQL语句
- PromotionMapper 定义了根据商品查活动，PromotionMapper.xml写SQL语句

**service层**

- ItemService, 
  - `List<Item> findItemsOnPromotion()`查询出所有正在活动的商品,具体实现，通过遍历正在活动的商品，每次遍历的时候，分别再查一下该商品的库存和活动，一起存入List<Item>；
  - `Item findItemById(int id)`,根据id查商品，然后再查库存和活动塞进去返回
  - `decreaseStock()`减库存 `increaseSales`加库存

**controller层**(首先使用`@CrossOrigin`实现跨域)

- `getItemList()`返回商品数据集合
- `getItemDetail` 通过id返回商品详情



## 2.3 ==慢查询分析==

慢查询日志，顾名思义，就是查询慢的日志，是指mysql记录所有执行超过long_query_time参数设定的时间阈值的SQL语句的日志。该日志能为SQL语句的优化带来很好的帮助。默认情况下，慢查询日志是关闭的，要使用慢查询日志功能，首先要开启慢查询日志功能。

**慢查询日志，顾名思义，就是查询慢的日志，是指mysql记录所有执行超过long_query_time参数设定的时间阈值的SQL语句的日志。该日志能为SQL语句的优化带来很好的帮助。默认情况下，慢查询日志是关闭的，要使用慢查询日志功能，首先要开启慢查询日志功能。**


# 3. 用户下单与秒杀


## 超买与少卖

下单的时刻先判定该商品有没有活动，有活动就走秒杀价（秒杀操作），没有活动就走日常价




解决少卖问题：用延时队列解决--在下单这一刻，把处理加到延时队列里，过半个小时再消费一下，如果已经付款就移出队列，如果没有付款就把付款消息作废并移出队列。延时队列有以下两种实现



## 3.1 ==代码实现==

**dao层：**

- 自动生成的增删改查就够了
- **在`SerialNumberMapper.xml`中的selectByPrimaryKey加了一个`for update`**，for update是一种行级锁，又叫排它锁。这里是查询最大的订单号，因为每次查最大订单后之后就要进行修改，所以为了防止出现并发的问题，加了一个排它锁，
  - **具体来说，如果不加这个X锁，比如线程一在改订单数据，线程二来读，这时候读到的就是MVCC里的历史版本数据，就会出现问题，这里加一个X锁，读和写就没法同时进行了，即强制不让读历史数据**

**service层：**

- `ItemServiceImpl`的扣减库存方法，返回boolean ，如果rows>0 说明扣减库存成功，如果rows<0,说明扣除失败（因为超过最大库存了,在dao层加了一个扣减库存量必须小于库存总量），**这样的话就不用先查询库存量 再进行比较 再进行扣减 提高效率**
- `OrderService`定义`createOrder(int userId, int itemId, int amount, Integer promotionId)`创建订单，先校验参数是否合法（这里也校验库存），然后先扣库存（先把库存锁住再生成订单），再生成订单，然后更新销量（这个放最后无所谓，因为销量不影响下单，只影响观察），最后返回这个订单。

**controller层**： 

- `OrderController`从session中得到user，然后传入相应参数调用service



## 3.2 ==数据库事务和锁==

看mysql的总结


# 4. 项目部署与压测

**压测的逻辑：在尽可能跑满服务器指标的情况下，尽可能提高并发量。**




- 把项目打包成jar包，上传到服务器上，安装jdk1.8

- 用后台的方式启动jar，并且需要指定一下jvm（默认的jvm内存空间比较小，修改大一点），以脚本的方式启动jar![image-20211125212510131](img/image-20211125212510131.png)

  ​	nohup 控制台如果要产生信息，把他记录在一个文件里，不要打印在控制台，后面&结尾，表示后台启动，这个窗口关了应用也不会挂掉，`-Xms1024m -Xmx1024m -XX:NewSize=512m -XX:MaxNewSize=512m`,最小内存和最大内存都是1G，防止内存大小伸缩影响性能，新生代512m，最大新生代也是512m

  `pid=`ps aux | grep seckill | grep -v grep | awk '{print $2}' `
  kill -9 $pid`这两句话是查一下seckill的端口，查到之后kill掉

- 然后安装Nginx，并配置web服务和反向代理

- 本地安装jmeter并配置，先测详情页面，压测的时候不断增大线程数，在不出错的情况下找到最大值，此时主要瓶颈是带宽，总共测试300个线程，10秒内起来，一共跑10次，吞吐量224吞吐量只有300


- 再测下单操作，下单需要登录，在jmeter中，我们先传入参数，再把登录后的cookie传入就可以了，总共测试300个线程，10秒内起来，一共跑10次，吞吐量224





## 后期优化方向

1. Nginx主从热备
2. Tomcat做集群
3. 两级缓存
4. MySQL读写分离
5. 用MQ做异步，提高性能




## 1. ==redis安装与使用==

- 导入redis的包，并配置等，此外，写一个redis配置类覆盖原有的配置类`RedisConfiguration`,改掉序列化的方式，在序列化前加上类名，反序列化的时候就可以直接转换为我们需要的类，而不是object
- redis支持简单的事务


## 2. ==代码实现==

要用redis代替session做状态管理，我们用到session的地方有**UserControlle**，**OrderController**，**LoginCheckInterceptor**

1. **UserControlle**

   - 注册原来把验证码存入session中，现在把这个验证码存入redis中，并且设置5分钟的有效时间。![image-20211130200152624](img/image-20211130200152624.png)

     从redis中获取验证码![image-20211130200249439](img/image-20211130200249439.png)

   - 登录，原先把user存入session中，记录了登录状态，下次可以直接看能不能取到loginUser判断是否登录。现在存到redis里，随机生成一个字符串记为token作为key，value是user，有效时间一天![image-20211130200719507](img/image-20211130200719507.png)

   - 登出的时候传入token，在redis里删除这个token就可以了

2. **OrderController**，在创建订单的时候需要记录userId，原先是从session里取user

   - 现在在方法里传入token，通过token在redis里取user就可以了![image-20211130201130347](img/image-20211130201130347.png)

3. **LoginCheckInterceptor**拦截器，在指定的请求前面做拦截，判断是否登录，原先是看能不能从session里取到user

   - 现在用redis，首先从request中获取token，然后再去redis中查user![image-20211130202132038](img/image-20211130202132038.png)



# 缓存商品与用户

**用redis做缓存，提升查询时的性能；不仅用到了redis，还用到了guava（瓜哇），用这两个做了个两级缓存。为什么不用MySQL或MyBatis的二级缓存，因为这俩离数据源太近，离用户比较远，我们要尽可能让缓存靠前。 我们用guava做一级缓存，guava是和tomat在一起，是本地缓存，最快，但不能占太大空间，只有特别热的数据才放到一级缓存，其余的放在二级缓存（redis）。**

## ==1. 代码实现==

改缓存对于前端来说没有任何变化，该怎么请求还是怎么请求，只是在查找的时候，把命中的数据放在缓存里，期待下次对数据的访问，所以都是在service层

1. **ItemServiceImpl**,注入redis（远程缓存，也就是二级缓存）和cache（本地缓存，也就是一级缓存）

   初始化瓜哇缓存，初始化容量10，最大100，过期时间1分钟

   - 原先有一个查商品详情的方法`findItemById`是从MySQL中查，现在我们加一个`findItemInCache`,从缓存里查，（）先去瓜哇查，再去redis 查，再去mysql查，在更多的时候可以替代`findItemById`


   - 用这个格式，key是`"item:" + id`，item是商品详情，在瓜哇和redis里通过key找item。①第一次找的时候，瓜哇和redis都没有，去，mysql里找到，存入瓜哇和redis；②第二次找的时候，直接在瓜哇里就能找到，返回该数据；③瓜哇有效时间是1分钟，1分钟后瓜哇失效，此时在redis中可以找到，然后再存入瓜哇

   

2. **UserServiceImpl**，因为在登录之后，很多地方都需要请求用户的信息(此 项目是在orderController里用)，而且因为用户信息更改的频率很低，所以可以把用户也存入缓存里

   - 原先如果要查用户信息，是有一个`findUserById`,现在写了一个`findUserFromCache`来替换这个方法；这个方法只引入了redis，没有引入瓜哇，也就是只用了远程缓存，不用本地缓存，因为本地缓存只存入最热的数据，在线用户数据量太大，而且不一定所有用户都是活跃状态，所以只用redis
   - 逻辑和商品缓存一样，先看redis里有没有，没有的话去mysql查，查到之后存入redis（30分钟有效期）

3. controller不调用`findItemById`和`findUserById`，调用`findItemInCache`和`findUserFromCache`



# 异步化扣减库存

消息队列的作用：**异步、解耦、削峰。** 

## RocketMQ

RocketMQ主要由 Producer、Broker、Consumer 三部分组成，其中Producer 负责生产消息，Consumer 负责消费消息，Broker (消息队列服务器)负责存储消息。Broker 在实际部署过程中对应一台服务器，每个 Broker 可以存储多个Topic的消息，每个Topic的消息也可以分片存储于不同的 Broker。Message Queue 用于存储消息的物理地址，每个Topic中的消息地址存储于多个 Message Queue 中。ConsumerGroup 由多个Consumer 实例构成。





## ==代码实现==

两阶段提交



先在服务器上安装rocketmq，本地代码导包即可

### 增加销量

**增加销量和下单不用保持事务性，因为销量不影响购买**

**service层：**

- 原先是在`OrderServiceImpl`中生成订单后增加销量，增加销量也是锁住Item的一行，会影响并发性能，现在
  1. 向Server发送第一阶段的消息，主题是`seckill`标签是`increase_sales`，消息内容包括商品ID和数量，成功失败都记录日志，超时时间设置60s
  2. `IncreaseSalesConsumer`中定义消费主题是`seckill`标签是`increase_sales`的消费者，消费者从消息队列中取出消息并消费（调用`itemService.increaseSales`方法）



### **扣减库存**

这个必须是事务性操作。这里的事务性是指在缓存里扣库存和数据库扣库存必须是事务性的。首先需要利用`cacheItemStock`预热缓存（因为下单扣减库存先在缓存里减，需要活动开始前先提前把库存都导入到缓存中,具体操作是把所有商品查出来，针对每个商品把库存存入redis中）。

事务性的异步扣减库存关键在于订单流水，在数据库里有一个item_stock_log流水表，id是随机字符串（因为订单之间无关），item_id,amount,status(表示状态，0表示不知道，1代表成功，2代表失败)

**service层：**

- 先在`ItemServiceImpl`写一个在缓存里扣减库存的方法`decreaseStockInCache`,如果减完库存后值>=0，说明扣减成功，反之失败。
- `LocalTransactionListenerImpl`类中定义执行本地事务的方法，先判断来的消息的tag是不是`decrease_stock`，如果是，就执行`createOrder`（创建订单，这里给创建订单的方法加了个更新流水），
- `LocalTransactionListenerImpl`类中定义回查的方法，先判断来的消息的tag是不是`decrease_stock`，如果是，就执行`checkStockStatus`（通过传入的流水ID去查流水的status）

- 原先是在`OrderServiceImpl`中创建订单时扣减库存，现在createOrder方法： ①调用`decreaseStockInCache`先扣减缓存里的库存，然后通过返回值判断是否成功（如果失败就报库存不足）②生成订单 ③更新流水状态为1
- `DecreaseStockConsumer`中定义消费者，在数据库中扣减库存
- 定义`createOrderAsync`方法,先判断该商品是否具有售罄标识（如果已经售罄直接返回），如果没有售罄，①生成订单流水，②发消息，消息体里存了商品ID 下单数量 订单流水ID（回查的时候需要用到），再定义一个本地事务需要的参数 ③投送消息

controller层

- 原本在creat中调用orderService.createOrder创建订单并扣减库存，现在调用orderService.createOrderAsync




# 削峰限流与防刷

## 削峰限流

**削峰限流解决的是下单这个借口流量大的问题（比如一次进来100万的流量，服务器肯定崩溃了）**

## 代码

controller层

- **验证码**：`getCaptcha`得到验证码，验证token，然后把验证码存入redis（user，验证码文本），1分钟有效期
- **请求令牌**：`generateToken`先验证验证码是否正确，然后调用`promotionService.generateToken`（这个方法检测了售罄标识、校验用户、校验商品活动等，然后判断秒杀大闸到没到限制，就是减去redis的设定值，我设定的是库存的5倍，如果大闸到0，就不颁发令牌，否则就返回一个有效期为10分钟的令牌）生成令牌，判断令牌是否为空，如果不为空，就到创建订单
- **限流器**：`create`创建订单里面，首先限制单机流量（申请访问，1秒内允许就可以，否则就是系统繁忙），然后再验证上一步得到的令牌是否正确，
- **队列缓冲**：然后利用线程池（线程池提前做了配置）执行，就起到了队列缓冲的作用，利用多线程下单



### 限流器

限流器有两种算法

**令牌桶算法（业务有可能出现爆发的情况，比如一开始没有请求，突然爆发来了100个，业务组件就要同时处理100个）：**限流器初始化，同时初始化令牌桶，假设令牌桶容量100，一开始初始化给桶里面放10个令牌，然后令牌生成器每秒添加10个令牌，桶满则弃掉多余令牌。当客户端访问时，会被限流器拦截，然后看有没有令牌，有的话桶里令牌-1并允许访问业务组件，没有则直接返回

**漏桶算法（业务组件处理速度恒定，很稳健，永远只处理10个请求）：**客户端访问时（视为一滴水），被限流器拦截，尝试将这滴水加给漏铜，如果此时桶已经满了，直接返回，如果桶未满，则传入漏铜假设漏铜容量100，水满则溢，每秒漏出10滴水，漏给业务组件

如果要求服务器能够很稳健的处理请求，用漏桶算法，如果希望能偶尔处理大规模突发请求，但是不能长期，用令牌桶算法，在我们的系统里，在秒杀那一刻会涌入大量请求，所以我用令牌桶算法。



## 防刷

削峰是削的正常的用户，防刷是防止黄牛

前两种比较老 第三种比较好，但是每种方法都有破解的方法，往往结合多种方法配合大数据和AI一起解决


# 最后压测

购买按需付费的服务器，原先的服务器因为带宽受限，现在买一个带宽上限是300Mbit/s的服务器再次压测。

商品详情 2000个线程 20s跑完 跑20轮  吞吐量大概是5500




后续优化方向：该做的都差不多了，剩下就是可以把功能补充完整，例如 

①付款环节 

②解决少卖的问题（用延时队列，超时取消订单）、

③整个系统都是单节点部署，可以把tomcat mysql redis nginx mq都做成集群的方式 

再有就是一些细节，例如

④线程池的数量多少比较合适（需要不断测试去调整参数）

