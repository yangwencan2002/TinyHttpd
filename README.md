## Tiny Httpd Server for Android

## Why TinyHttpd?
TinyHttpd是一个运行于Android上轻量且高效的Http服务器组件，可用于代理服务器等使用场景（如音视频边下边播的代理服务器）。

## 特性
- 高效，网络使用NIO编程模型，更少的线程资源支持更多的并发请求；
- 安全，对http请求进行身份鉴权，无端口开放风险（端口开放是一个很容易忽略的安全隐患，详细请搜索“WormHole虫洞漏洞”）；
- 轻量，代码简洁，OOP设计，容易修改和扩展；
- 灵活，包含拦截器功能，可对任意http请求和响应进行拦截，对它们进行动态检查或修改。


## Sample
1. 定义HttpServer类：
```java
/**
 * 继承TinyHttpd并重写{@link TinyHttpd#doGet(HttpRequest, HttpResponse)}即可创建HttpServer
 * @author vincanyang
 */
public class HelloWorldServer extends TinyHttpd {//继承TinyHttpd
    public static final String RESPONSE = "<html><body><h1>Hello world,server!</h1></body></html>";
    public HelloWorldServer() throws InterruptedException, IOException {
        super();
    }
    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws ResponseException, IOException {//重写doGet()或doPost()
        if (!Constants.URL.equals(request.url())) {
            super.doGet(request, response);
            return;
        }
        response.setContentType("text/html");
        response.write(RESPONSE);
    }
}
```

2. 启动HttpServer并访问：

```java
public class MainActivity extends AppCompatActivity {
    private HelloWorldServer server;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            server = new HelloWorldServer();
        } catch (InterruptedException | IOException e) {
            throw new IllegalStateException("error init server", e);
        }
        server.addInterceptor(new LoggingInterceptor());//添加自定义拦截器
        WebView webView = (WebView) findViewById(R.id.webView);
        if (server.isWorking()) {//服务是否运行中
            webView.loadUrl(server.createUrl(Constants.URL));
        } else {
            String output = "<html><body><h1>Server start fail!</h1></body></html>";
            webView.loadData(output, "text/html", null);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (server != null) {
            server.shutdown();
        }
    }
}
```
更多参考 `sample` 工程，运行成功后显示如下图：
![image](https://github.com/yangwencan2002/TinyHttpd/blob/master/sample.jpg)

## TODO
1. 支持HTTPS(SSL)；
2. 支持Cookie；
3. 文件上传；
4. 支持HTTP2.0；
5. ...

## License

    Copyright 2017 Vincan Yang

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
