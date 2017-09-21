package com.vincan.tinyhttpd;

import com.vincan.tinyhttpd.codec.HttpRequestDecoder;
import com.vincan.tinyhttpd.codec.HttpResponseEncoder;
import com.vincan.tinyhttpd.codec.RequestDecoder;
import com.vincan.tinyhttpd.codec.ResponseEncoder;
import com.vincan.tinyhttpd.request.HttpRequest;
import com.vincan.tinyhttpd.response.HttpResponse;
import com.vincan.tinyhttpd.response.ResponseException;
import com.vincan.tinyhttpd.utils.LogUtil;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

/**
 * IO处理器
 *
 * @author vincanyang
 */
public class IOHandler implements Runnable {

    private RequestDecoder mRequestDecoder = new HttpRequestDecoder();

    private ResponseEncoder mResponseEncoder = new HttpResponseEncoder();

    private TinyHttpd mHttpServer;

    private SocketChannel mChannel;

    private byte[] mReuqestBytes;

    public IOHandler(SocketChannel channel, byte[] reuqestBytes, TinyHttpd httpServer) {
        mChannel = channel;
        mReuqestBytes = reuqestBytes;
        mHttpServer = httpServer;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        HttpResponse response = new HttpResponse(mChannel);
        try {
            HttpRequest request = mRequestDecoder.decode(mReuqestBytes);//decode request
            mHttpServer.service(request, response);//handle request and response to client
        } catch (ResponseException re) {//handle biz exception
            handleResponseException(re, response);
        } catch (IOException e) {
            handleIOException(e);
        } finally {
            closeChannel();
        }
    }

    private void handleResponseException(ResponseException re, HttpResponse response) {
        LogUtil.d("ResponseException happened and handling", re);
        response.setStatus(re.getStatus());
        try {
            response.write(mResponseEncoder.encode(response));
            response.write(re.getMessage().getBytes());
        } catch (IOException e) {
            LogUtil.e("Error writing the response" + e);
        }
    }

    private void handleIOException(IOException e) {
        if (e instanceof ClosedChannelException) {
            LogUtil.d("Client close the channel" + e);
        } else {
            LogUtil.e("Error service" + e);
        }
    }

    private void closeChannel() {
        LogUtil.d("Closing the channel");
        try {
            mChannel.close();
        } catch (IOException e) {
            LogUtil.e("Error closing the channel" + e);
        }
    }
}
