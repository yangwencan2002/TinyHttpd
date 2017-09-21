package com.vincan.tinyhttpd;

import com.vincan.tinyhttpd.codec.HttpResponseEncoder;
import com.vincan.tinyhttpd.codec.ResponseEncoder;
import com.vincan.tinyhttpd.interceptor.AuthInterceptor;
import com.vincan.tinyhttpd.interceptor.Interceptor;
import com.vincan.tinyhttpd.interceptor.InterceptorChainImpl;
import com.vincan.tinyhttpd.request.HttpMethod;
import com.vincan.tinyhttpd.request.HttpRequest;
import com.vincan.tinyhttpd.response.HttpResponse;
import com.vincan.tinyhttpd.response.HttpStatus;
import com.vincan.tinyhttpd.response.ResponseException;
import com.vincan.tinyhttpd.utils.LogUtil;
import com.vincan.tinyhttpd.utils.Util;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * http服务器基础类，开发者可以继承它并在{@link #doGet(HttpRequest, HttpResponse)}或{@link #doPost(HttpRequest, HttpResponse)}中对进行请求处理
 *
 * @author vincanyang
 */
public abstract class TinyHttpd {

    public static final String TAG = TinyHttpd.class.getSimpleName();

    private String mHost;

    private Thread mReactorThread;

    private DispatchHandler mDispatchRunnable;

    private EchoTester mEchoTester;

    private List<Interceptor> mInterceptors = Collections.synchronizedList(new LinkedList<Interceptor>());

    private final String mRandomUUID;

    private ResponseEncoder mResponseEncoder = new HttpResponseEncoder();

    /**
     * 构造函数，默认使用{@link Util#LOCALHOST}作为host参数
     *
     * @throws InterruptedException
     * @throws IOException
     */
    public TinyHttpd() throws InterruptedException, IOException {
        this(Util.LOCALHOST, 0);//端口将会随机
    }

    /**
     * 构造函数
     *
     * @param host
     * @throws InterruptedException
     * @throws IOException
     */
    public TinyHttpd(String host) throws InterruptedException, IOException {
        this(host, 0);//端口将会随机
    }

    /**
     * 构造函数
     *
     * @param host 服务器名
     * @param port 端口号
     * @throws InterruptedException
     * @throws IOException
     */
    public TinyHttpd(String host, int port) throws InterruptedException, IOException {
        mHost = host;
        CountDownLatch threadStartSignal = new CountDownLatch(1);
        mDispatchRunnable = new DispatchHandler(InetAddress.getByName(host), port, threadStartSignal, this);
        mReactorThread = new Thread(mDispatchRunnable);
        mReactorThread.setDaemon(true);
        mReactorThread.setName("TinyHttp thread");
        mReactorThread.start();
        threadStartSignal.await();//等待线程启动成功再接受请求，防止线程没有成功启动之前就有请求进来
        mEchoTester = new EchoTester(host, getPort());
        mRandomUUID = UUID.randomUUID().toString();
        LogUtil.d("TinyHttp is working?" + isWorking());
    }

    /**
     * 创建用于访问TinyHttpd的URL
     *
     * @param pathOfUrl 形如http://127.0.0.1:8989/helloworld中的helloworld，不包括schema和host
     * @return 用于访问TinyHttpd的URL，形如http://127.0.0.1:8989/helloworld
     */
    public String createUrl(String pathOfUrl) {
        return Util.createUrl(mHost, getPort(), pathOfUrl, mRandomUUID);
    }

    /**
     * get请求处理
     *
     * @param request
     * @param response
     * @throws ResponseException
     * @throws IOException
     */
    protected void doGet(HttpRequest request, HttpResponse response) throws ResponseException, IOException {
        response.setStatus(HttpStatus.NOT_FOUND);
        byte[] headersBytes = mResponseEncoder.encode(response);
        response.write(headersBytes);
    }

    /**
     * post请求处理
     *
     * @param request
     * @param response
     * @throws ResponseException
     * @throws IOException
     */
    protected void doPost(HttpRequest request, HttpResponse response) throws ResponseException, IOException {
        response.setStatus(HttpStatus.NOT_FOUND);
        byte[] headersBytes = mResponseEncoder.encode(response);
        response.write(headersBytes);
    }

    /**
     * 服务是否运行中
     *
     * @return
     */
    public boolean isWorking() {
        return mEchoTester.request();
    }

    /**
     * 获取端口号
     *
     * @return
     */
    public int getPort() {
        return mDispatchRunnable.getPort();
    }

    /**
     * 添加拦截器
     *
     * @param interceptor
     */
    public void addInterceptor(Interceptor interceptor) {
        mInterceptors.add(interceptor);
    }

    /**
     * 关闭服务
     */
    public void shutdown() {
        LogUtil.i(TAG, "Destroy TinyHttp");
        if (mDispatchRunnable != null) {
            mDispatchRunnable.destroy();
        }
    }

    void service(HttpRequest request, HttpResponse response) throws ResponseException, IOException {
        if (mEchoTester.isEchoRequest(request)) {
            mEchoTester.response(response);
        } else {
            serviceWithInterceptorChain(request, response);
        }
    }

    private void serviceWithInterceptorChain(HttpRequest originalRequest, HttpResponse originalResponse) throws ResponseException, IOException {
        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.add(new AuthInterceptor(mRandomUUID));
        interceptors.addAll(mInterceptors);
        interceptors.add(new LastInterceptor());//必须放最后一个才能结束InterceptorChain
        Interceptor.Chain chain = new InterceptorChainImpl(interceptors, 0, originalRequest, originalResponse);
        chain.proceed(originalRequest, originalResponse);
    }

    private final class LastInterceptor implements Interceptor {

        @Override
        public void intercept(Chain chain) throws ResponseException, IOException {
            HttpRequest request = chain.request();
            HttpResponse response = chain.response();
            HttpMethod method = request.method();
            if (method.equals(HttpMethod.GET)) {
                doGet(request, response);
            } else if (method.equals(HttpMethod.POST)) {
                doPost(request, response);
            }
        }
    }
}
