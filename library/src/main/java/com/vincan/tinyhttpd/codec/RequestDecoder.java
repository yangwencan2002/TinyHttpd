package com.vincan.tinyhttpd.codec;

import com.vincan.tinyhttpd.request.HttpRequest;
import com.vincan.tinyhttpd.response.ResponseException;

/**
 * {@link HttpRequest}的解码器
 *
 * @author vincanyang
 */
public interface RequestDecoder<T extends HttpRequest> {

    T decode(byte[] bytes) throws ResponseException;
}
