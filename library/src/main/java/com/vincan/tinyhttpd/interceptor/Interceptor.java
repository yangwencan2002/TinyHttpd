package com.vincan.tinyhttpd.interceptor;

import com.vincan.tinyhttpd.request.HttpRequest;
import com.vincan.tinyhttpd.response.HttpResponse;
import com.vincan.tinyhttpd.response.ResponseException;

import java.io.IOException;

/**
 * 拦截器
 *
 * @author wencanyang
 */
public interface Interceptor {
    void intercept(Chain chain) throws ResponseException, IOException;

    /**
     * 拦截器链
     */
    interface Chain {
        /**
         * 获取request
         */
        HttpRequest request();

        /**
         * 获取response
         */
        HttpResponse response();

        /**
         * 链式处理请求
         */
        void proceed(HttpRequest request, HttpResponse response) throws ResponseException, IOException;
    }
}