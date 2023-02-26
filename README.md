# 描述
SpringBoot2+SSE+redis发布订阅服务端推送消息

# 应用场景
后台新增数据提醒，审批数据提醒

# 技术思路
![技术思路](https://raw.githubusercontent.com/huashijun/huashijun.github.io/master/sse-starter.jpg)

# boot版本
2.0.x/2.1.x/2.2.x/2.3.x/2.4.x/2.5.x/2.6.x

# 依赖
```
#最新
<dependency>
    <groupId>io.github.huashijun</groupId>
    <artifactId>sse-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

# 配置
```
#redis单机配置
spring:
  redis:
    database: 6  #Redis索引0~15，默认为0
    host: 127.0.0.1
    port: 6379
    password:  #密码（默认为空）
    lettuce: # 这里标明使用lettuce配置
      shutdown-timeout: 1000
      pool:
        max-active: 8   #连接池最大连接数（使用负值表示没有限制）
        max-wait: -1  #连接池最大阻塞等待时间（使用负值表示没有限制）
        max-idle: 5     #连接池中的最大空闲连接
        min-idle: 0     #连接池中的最小空闲连接
    timeout: 10000    #连接超时时间（毫秒）
```
```
#redis集群配置
spring:
  redis:
    database: 6
    password: 123456
    lettuce:
      shutdown-timeout: 1000
      pool:
        max-active: 8
        max-wait: -1
        max-idle: 5
        min-idle: 0
    cluster:
      nodes:
        - 127.0.0.1:8080
        - 127.0.0.1:8081
        - 127.0.0.1:8082
      max-redirects: 3
```
```
#redis哨兵配置/主从配置
spring:
  redis:
    database: 6
    password: 123456
    lettuce:
      shutdown-timeout: 1000
      pool:
        max-active: 8
        max-wait: -1
        max-idle: 5
        min-idle: 0
    sentinel:
      master:
      nodes:
```

# 使用
```
#后端使用
@Controller
@RequestMapping(path = "sse")
public class SseRest {
    @Autowired
    private SseUtil sseUtil;
    @Autowired
    private RedisUtils redisUtils;

    @ResponseBody
    @GetMapping(path = "register", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter register(String userId) {
        return sseUtil.register(userId,3600_000L);
    }

    @ResponseBody
    @GetMapping(path = "remove")
    public void remove(String userId) {
        sseUtil.remove(userId);
    }

    @ResponseBody
    @GetMapping(path = "send")
    public void send(String msg) {
        redisUtils.convertAndSend("huashijun_sse",msg);
    }
}
```
```
#前端使用
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>sse demo</title>
    <script>
        if(typeof(EventSource)!=="undefined") {
            const userId = Math.floor(Math.random() * (99 - 1 + 1)) + 1;
            var url = 'http://localhost:8080/sse/register?userId=' + userId;
            var es = new EventSource(url);
            es.addEventListener("info", e => {
                console.log("连接成功", e)
            });
            es.addEventListener('disconnected', e => {
                console.log("断开连接",e.data);
                es.close()
            });
            es.addEventListener("message", (e) => {
                console.log("消息", e)
            });
            es.addEventListener("error", e => {
                if (e.error) {
                    console.log("错误,关闭连接", e.error);
                    es.close()
                }
            });
            es.addEventListener("open", e => {
                console.log("打开连接")
            })
        }
        else {
            document.getElementById("result").innerHTML="抱歉，你的浏览器不支持 server-sent 事件...";
        }
    </script>
</head>
<body>
<div id="result"></div>
</body>
</html>

```

# 反馈
如果该组件有问题请留言反馈，受到反馈后会及时修复问题

# 点赞
如果使用该组件觉得还不错，请留下您的足迹，点个赞
