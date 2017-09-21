package com.vincan.tinyhttpd.sample;

import com.vincan.tinyhttpd.interceptor.Interceptor;
import com.vincan.tinyhttpd.request.HttpRequest;
import com.vincan.tinyhttpd.response.HttpResponse;
import com.vincan.tinyhttpd.response.ResponseException;
import com.vincan.tinyhttpd.utils.LogUtil;
import com.vincan.tinyhttpd.utils.Util;

import java.io.IOException;

/**
 * 日志拦截器
 *
 * @author wencanyang
 */
public class LoggingInterceptor implements Interceptor {

    @Override
    public void intercept(Chain chain) throws ResponseException, IOException {
        long t1 = System.nanoTime();
        HttpRequest request = chain.request();
        HttpResponse response = chain.response();
        LogUtil.d(String.format("Sending request %s with headers %n%s", Util.decode(request.url()), request.headers()));
        chain.proceed(request, response);
        long t2 = System.nanoTime();
        LogUtil.d(String.format("Received response for %s in %.1fms with headers %n%s", Util.decode(request.url()), (t2 - t1) / 1e6d, response.headers()));
    }
}
