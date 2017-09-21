package com.vincan.tinyhttpd;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * @author vincanyang
 */
public final class TestHelper {

    public static boolean hello(String url) throws IOException {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        byte[] expectedResponse = HelloWorldServer.RESPONSE.getBytes();
        byte[] actualResponse = new byte[expectedResponse.length];
        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            inputStream = urlConnection.getInputStream();
            inputStream.read(actualResponse);
            return Arrays.equals(expectedResponse, actualResponse);
        } catch (IOException e) {
            return false;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}
