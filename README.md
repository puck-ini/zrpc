# 简易RPC框架



## zrpc-spring-boot-starter 模块

zrpc-spring-boot-starter 模块为 rpc 的主要实现，结构如下：

```
├─annotation 注解相关
├─cluster 负载均衡
├─config 配置相关、注册服务、注入服务
├─constants 常量
├─exception 异常
├─model 请求数据以及返回数据的格式定义
├─proxy 客户端发送请求代理
├─register 服务发现实现
├─remote 客户端和服务端实现
│  ├─client 客户端实现
│  ├─codec 解码编码器实现
│  ├─handler 请求处理器以及响应处理器
│  └─server 服务端实现
├─serializer 序列化方式
│  ├─fastjson
│  ├─hessian
│  ├─kryo
│  └─protostuff
└─util 工具类
```



## zrpc-spring-boot-test 模块

zrpc-spring-boot-test 为测试模块，demo-consumer 和 demo-provider 启动 nacos 后可直接运行测试，也可以在application配置文件中修改配置更换注册中心。

例如：

```yaml
zrpc:
  register-address: 127.0.0.1:2181 # 注册中心地址
  register-protocol: zookeeper # 注册中心，有 nacos 和 zookeeper 两种
  proxy: jdk # 代理方式，有 jdk 和 cglib 两种实现
```



启动后访问 http://localhost:9090/v1/get 即可看到结果。



## 使用方式

定义接口：

```java
public interface DemoService {

    String getMsg();

}
```



接口实现，在实现类加入 @ZService 注解：

```java
@ZService
public class DemoServiceImpl implements DemoService {

    @Override
    public String getMsg() {
        return "getMsg: " + System.currentTimeMillis();
    }

}
```



调用接口，使用 @ZReference 调用接口：

```java
    @ZReference
    private DemoService demoService;
```

