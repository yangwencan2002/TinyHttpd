package com.vincan.tinyhttpd.interceptor;

import com.vincan.tinyhttpd.request.HttpRequest;
import com.vincan.tinyhttpd.response.HttpResponse;
import com.vincan.tinyhttpd.response.ResponseException;

import java.io.IOException;
import java.util.List;

/**
 * 拦截器链实现
 *
 * @author vincanyang
 */
public class InterceptorChainImpl implements Interceptor.Chain {

    private List<Interceptor> mInterceptors;

    private int mIndex;

    private HttpRequest mRequest;

    private HttpResponse mResponse;

    public InterceptorChainImpl(List<Interceptor> interceptors, int index, HttpRequest request, HttpResponse response) {
        mInterceptors = interceptors;
        mIndex = index;
        mRequest = request;
        mResponse = response;
    }

    @Override
    public HttpRequest request() {
        return mRequest;
    }

    @Override
    public HttpResponse response() {
        return mResponse;
    }

    @Override
    public void proceed(HttpRequest request, HttpResponse response) throws ResponseException, IOException {
        if (mIndex >= mInterceptors.size()) {
            throw new AssertionError();
        }
        InterceptorChainImpl next = new InterceptorChainImpl(
                mInterceptors, mIndex + 1, request, response);
        Interceptor interceptor = mInterceptors.get(mIndex);
        interceptor.intercept(next);
    }
}
