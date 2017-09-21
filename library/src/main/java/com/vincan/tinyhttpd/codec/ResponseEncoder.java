package com.vincan.tinyhttpd.codec;

import com.vincan.tinyhttpd.response.HttpResponse;

import java.io.IOException;

/**
 * {@link HttpResponse}的编码器
 *
 * @author vincanyang
 */
public interface ResponseEncoder<T extends HttpResponse> {

    byte[] encode(T response) throws IOException;
}
