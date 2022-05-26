<div align="center">
  <h1>GTMD Proxy</h1>
  <p>

[![author](https://img.shields.io/badge/author-yycYYYY-brightgreen)](https://github.com/yycYYYY)
[![license](https://img.shields.io/github/license/yycYYYY/gtmdproxy.svg)](https://opensource.org/licenses/MIT)

  </p>
</div>

## 一、工具介绍
GTMD Proxy是一个使用Netty编写的HTTP代理工具，支持HTTP、HTTPS、Socket、MQTT等协议的代理，支持HTTP、HTTPS请求响应报文的捕获分析、篡改、转发等功能。

## 二、使用方法
### 1、添加依赖
```xml
<dependency>
  <groupId>com.github.yycYYYY</groupId>
  <artifactId>gtmdproxy</artifactId>
  <version>0.0.1</version>
</dependency>
```
启动服务
```java
new ProxyServer().start(8889);
```

### 2、CLI调用
> java -jar ProxyCli.java --type proxy --proxyAddress www.baidu.com:www.google.com

## 三、主要功能
### 1、普通代理服务器

```java
public class ProxyTestServer {
    public static void main(String[] args) {
        new ProxyServer().interceptorInitializer(new InterceptorInitializer(){
            @Override
            public void init(InterceptotPipeline pipeline) {
                //域名转发拦截器，实现见下方
                pipeline.addLast(new ForwardInterceptor());
                //请求拦截器，实现见下方
                pipeline.addLast(new MyRequestInterceptor());
                //响应拦截器，实现见下方
                pipeline.addLast(new MyResponseInterceptor());
            }
        })
        .start(8889);
    }
}
```
### 2、http报文捕获、断言、篡改
#### 1)请求的匹配和篡改
```java
public class MyRequestInterceptor extends DefaultRequestInterceptor {
    @Override
    public boolean match(HttpRequest httpRequest, InterceptotPipeline pipeline) {
        boolean res = false;
//        res = 判断逻辑
        return res;
    }

    @Override
    public void handleRequest(FullHttpRequest httpRequest, InterceptotPipeline pipeline) {

        //针对httpRequest请求的篡改
        httpRequest.setUri(httpRequest.uri()+"/test=1");
        httpRequest.headers().add("test","test");
    }
}
```

#### 2)响应的匹配和篡改
```java
public class MyResponseInterceptor extends DefaultResponseInterceptor {
    @Override
    public boolean match(HttpRequest httpRequest, HttpResponse httpResponse, InterceptotPipeline pipeline) {
        //如果目标服务器响应502了
        return httpResponse.status() == HttpResponseStatus.BAD_GATEWAY;
    }

    @Override
    public void handleResponse(HttpRequest httpRequest, FullHttpResponse httpResponse, InterceptotPipeline pipeline) {
        //篡改为成功的响应
        httpResponse.setStatus(HttpResponseStatus.OK);
        httpResponse.content().writeBytes("success".getBytes());
    }
}
```

### 3、域名转发

```java
public class ForwardInterceptor extends DefaultForwardInterceptor {

    @Override
    public void beforeRequest(Channel clientChannel, HttpRequest httpRequest, InterceptotPipeline pipeline) throws Exception {
        if (MatchUtil.matchHost(httpRequest, "www.123.com")) {
            pipeline.getRequestInfo().setHost("www.456.com");
            pipeline.getRequestInfo().setPort(80);
            pipeline.getRequestInfo().setHttps(false);
        }
        pipeline.beforeRequest(clientChannel, httpRequest);
    }

}
```
### 4、MQTT信息捕获分析（待实现）

## 四、通信流程


