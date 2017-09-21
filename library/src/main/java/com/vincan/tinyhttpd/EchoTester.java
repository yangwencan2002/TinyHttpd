package com.vincan.tinyhttpd;

import com.vincan.tinyhttpd.codec.HttpResponseEncoder;
import com.vincan.tinyhttpd.codec.ResponseEncoder;
import com.vincan.tinyhttpd.request.HttpRequest;
import com.vincan.tinyhttpd.response.HttpResponse;
import com.vincan.tinyhttpd.utils.LogUtil;
import com.vincan.tinyhttpd.utils.Util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * httpd的echo测试,检测httpd是否运行
 *
 * @author vincanyang
 */
public class EchoTester {

    private static final String URL = "echo";

    private static final String RESPONSE = "echo ok";

    private static final long TIMEOUT_AWAIT = 300;//300ms

    private final String mHost;

    private final int mPort;

    private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    public EchoTester(String host, int port) {
        mHost = host;
        mPort = port;
    }

    public boolean isEchoRequest(HttpRequest request) {
        return URL.equals(request.url());
    }

    public boolean request() {
        Future<Boolean> echoFutureTask = mExecutorService.submit(new RequestEchoCallable());
        try {
            return echoFutureTask.get(TIMEOUT_AWAIT, MILLISECONDS);
        } catch (TimeoutException e) {
            LogUtil.e("Echo httpd timeout " + TIMEOUT_AWAIT, e);
        } catch (InterruptedException | ExecutionException e) {
            LogUtil.e("Error echo httpd", e);
        }
        return false;
    }

    private boolean requestEcho() throws IOException {
        HttpURLConnection urlConnection = null;
        byte[] expectedResponse = RESPONSE.getBytes();
        byte[] actualResponse = new byte[expectedResponse.length];
        try {
            urlConnection = (HttpURLConnection) new URL(Util.createUrl(mHost, mPort, URL)).openConnection();
            urlConnection.getInputStream().read(actualResponse);
            boolean isOk = Arrays.equals(expectedResponse, actualResponse);
            LogUtil.i("Echo is ok?" + isOk);
            return isOk;
        } catch (IOException e) {
            LogUtil.e("Error reading echo response", e);
            return false;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private class RequestEchoCallable implements Callable<Boolean> {

        @Override
        public Boolean call() throws Exception {
            return requestEcho();
        }
    }

    public void response(HttpResponse response) throws IOException {
        response.write(RESPONSE);
    }
}
