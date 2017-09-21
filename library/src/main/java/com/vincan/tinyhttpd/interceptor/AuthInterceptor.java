package com.vincan.tinyhttpd.interceptor;

import android.text.TextUtils;

import com.vincan.tinyhttpd.HttpConstants;
import com.vincan.tinyhttpd.request.HttpRequest;
import com.vincan.tinyhttpd.response.HttpStatus;
import com.vincan.tinyhttpd.response.ResponseException;
import com.vincan.tinyhttpd.utils.Util;

import java.io.IOException;

/**
 * 身份认证拦截器
 *
 * @author wencanyang
 */
public class AuthInterceptor implements Interceptor {

    private String mSecret;

    public AuthInterceptor(String secret) {
        mSecret = secret;
    }

    @Override
    public void intercept(Chain chain) throws ResponseException, IOException {
        HttpRequest request = chain.request();
        String clientSign = request.getParam(HttpConstants.PARAM_SIGN);
        if (TextUtils.isEmpty(clientSign)) {
            throw new ResponseException(HttpStatus.BAD_REQUEST, HttpConstants.PARAM_SIGN + " cann't be empty");
        }
        String clientTimestamp = request.getParam(HttpConstants.PARAM_TIMESTAMP);
        if (TextUtils.isEmpty(clientTimestamp)) {
            throw new ResponseException(HttpStatus.BAD_REQUEST, HttpConstants.PARAM_TIMESTAMP + " cann't be empty");
        }
        String serverSign = Util.getHmacSha1(request.url() + clientTimestamp, mSecret);
        if (!serverSign.equals(clientSign)) {
            throw new ResponseException(HttpStatus.UNAUTHORIZED, HttpConstants.PARAM_SIGN + " is not correct");
        }
        chain.proceed(request, chain.response());
    }
}
