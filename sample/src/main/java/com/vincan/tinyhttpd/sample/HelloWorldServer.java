package com.vincan.tinyhttpd.sample;

import com.vincan.tinyhttpd.TinyHttpd;
import com.vincan.tinyhttpd.request.HttpRequest;
import com.vincan.tinyhttpd.response.HttpResponse;
import com.vincan.tinyhttpd.response.ResponseException;

import java.io.IOException;

/**
 * 继承TinyHttpd并重写{@link TinyHttpd#doGet(HttpRequest, HttpResponse)}即可创建HttpServer
 *
 * @author vincanyang
 */
public class HelloWorldServer extends TinyHttpd {

    public static final String RESPONSE = "<html><body><h1>Hello world,server!</h1></body></html>";

    public HelloWorldServer() throws InterruptedException, IOException {
        super();
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws ResponseException, IOException {
        if (!Constants.URL.equals(request.url())) {
            super.doGet(request, response);
            return;
        }
        response.setContentType("text/html");
        response.write(RESPONSE);
    }
}
