# 简易RPC框架

zrpc-spring-boot-test 为测试模块，demo-consumer 和 demo-provider 启动 nacos 后可直接运行测试



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

