package com.vincan.tinyhttpd;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

/**
 * {@link TinyHttpd}
 *
 * @author vincanyang
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class HelloWorldServerTest {

    @Test
    public void testHelloWorld() throws Exception {
        HelloWorldServer server = null;
        try {
            server = new HelloWorldServer();
            boolean isWorking = server.isWorking();
            Assert.assertTrue(isWorking);
            boolean isOk = TestHelper.hello(server.createUrl(Constants.URL));
            Assert.assertTrue(isOk);
        } catch (InterruptedException | IOException e) {
            throw new IllegalStateException("error init server", e);
        } finally {
            server.shutdown();
        }
    }
}
