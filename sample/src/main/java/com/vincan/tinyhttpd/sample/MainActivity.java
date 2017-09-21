package com.vincan.tinyhttpd.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import java.io.IOException;

/**
 * 主界面
 *
 * @author vincanyang
 */
public class MainActivity extends AppCompatActivity {

    private HelloWorldServer server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            server = new HelloWorldServer();
        } catch (InterruptedException | IOException e) {
            throw new IllegalStateException("error init server", e);
        }
        server.addInterceptor(new LoggingInterceptor());
        WebView webView = (WebView) findViewById(R.id.webView);
        if (server.isWorking()) {
            webView.loadUrl(server.createUrl(Constants.URL));
        } else {
            String output = "<html><body><h1>Server start fail!</h1></body></html>";
            webView.loadData(output, "text/html", null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (server != null) {
            server.shutdown();
        }
    }
}
