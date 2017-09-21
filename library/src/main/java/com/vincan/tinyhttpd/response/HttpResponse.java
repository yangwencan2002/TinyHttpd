package com.vincan.tinyhttpd.response;


import com.vincan.tinyhttpd.HttpHeaders;
import com.vincan.tinyhttpd.HttpVersion;
import com.vincan.tinyhttpd.codec.HttpResponseEncoder;
import com.vincan.tinyhttpd.codec.ResponseEncoder;
import com.vincan.tinyhttpd.utils.Util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * http响应
 *
 * @author vincanyang
 */
public final class HttpResponse {

    private ResponseEncoder mResponseEncoder = new HttpResponseEncoder();

    private HttpHeaders mHeaders = new HttpHeaders();

    private final HttpVersion mHttpVersion = HttpVersion.HTTP_1_1;

    private HttpStatus mStatus = HttpStatus.OK;

    private final SocketChannel mChannel;

    private ByteBuffer mResponseByteBuffer = ByteBuffer.allocate(Util.DEFAULT_BUFFER_SIZE);

    private boolean isHeaderWritten = false;

    public HttpResponse(SocketChannel channel) {
        mChannel = channel;
    }

    public void setStatus(HttpStatus status) {
        mStatus = status;
    }

    /**
     * 添加头部
     *
     * @param key   {@link com.vincan.tinyhttpd.HttpHeaders.Names}
     * @param value
     */
    public void addHeader(String key, String value) {
        mHeaders.put(key, value);
    }

    public void setContentType(String type) {
        addHeader(HttpHeaders.Names.CONTENT_TYPE, type);
    }

    public void setContentLength(int length) {
        addHeader(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(length));
    }

    public void write(String data) throws IOException {
        write(data.getBytes());
    }

    public void write(byte[] bytes) throws IOException {
        write(bytes, 0, bytes.length);
    }

    public void write(byte[] bytes, int offset, int length) throws IOException {
        if (!isHeaderWritten) {
            byte[] headersBytes = null;
            try {
                headersBytes = mResponseEncoder.encode(this);
            } catch (IOException e) {
                //should not happen
            }
            if (headersBytes != null) {
                mChannel.write(ByteBuffer.wrap(headersBytes));
            }
            isHeaderWritten = true;
        }
        mResponseByteBuffer.put(bytes, offset, length);
        mResponseByteBuffer.flip();
        while (mResponseByteBuffer.hasRemaining()) {//XXX ByteBuffer会缓存，可能不会全部写入channel
            mChannel.write(mResponseByteBuffer);
        }
        mResponseByteBuffer.clear();
    }

    public HttpStatus status() {
        return mStatus;
    }

    public HttpVersion protocol() {
        return mHttpVersion;
    }

    public HttpHeaders headers() {
        return mHeaders;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "httpVersion=" + mHttpVersion +
                ", status=" + mStatus +
                '}';
    }
}