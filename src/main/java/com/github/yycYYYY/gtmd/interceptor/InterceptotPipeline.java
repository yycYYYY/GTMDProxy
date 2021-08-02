package com.github.yycYYYY.gtmd.interceptor;

import com.github.yycYYYY.gtmd.model.ProxyInfo;
import com.github.yycYYYY.gtmd.model.RequestInfo;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author yuyongchao
 **/
public class InterceptotPipeline implements Iterable<ProxyInterceptor> {
    private List<ProxyInterceptor> interceptors;
    private ProxyInterceptor defaultInterceptor;
    private RequestInfo requestInfo;
    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private ProxyInfo proxyInfo;

    private int posBeforeHead = 0;
    private int posBeforeContent = 0;
    private int posAfterHead = 0;
    private int posAfterContent = 0;

    public void addFirst(ProxyInterceptor interceptor){this.interceptors.add(0, interceptor);}
    public void addLast(ProxyInterceptor interceptor){this.interceptors.add(this.interceptors.size() - 1, interceptor);}
    public ProxyInterceptor get(int index){ return this.interceptors.get(index);}

    public ProxyInterceptor getDefaultInterceptor() {
        return defaultInterceptor;
    }

    public RequestInfo getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public void setHttpRequest(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    public void setHttpResponse(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public ProxyInfo getProxyInfo() {
        return proxyInfo;
    }

    public void setProxyInfo(ProxyInfo proxyInfo) {
        this.proxyInfo = proxyInfo;
    }

    public InterceptotPipeline(ProxyInterceptor defaultIntercept) {
        this.interceptors = new LinkedList<>();
        this.defaultInterceptor = defaultIntercept;
        this.interceptors.add(defaultIntercept);
    }

    public void beforeRequest(Channel clientChannel, HttpRequest httpRequest) throws Exception {
        this.httpRequest = httpRequest;
        if (this.posBeforeHead < interceptors.size()) {
            ProxyInterceptor intercept = interceptors.get(this.posBeforeHead++);
            intercept.beforeRequest(clientChannel, this.httpRequest, this);
        }
        this.posBeforeHead = 0;
    }

    public void afterResponse(Channel clientChannel, Channel proxyChannel, HttpResponse httpResponse)
            throws Exception {
        this.httpResponse = httpResponse;
        if (this.posAfterHead < interceptors.size()) {
            ProxyInterceptor intercept = interceptors.get(this.posAfterHead++);
            intercept.afterResponse(clientChannel, proxyChannel, this.httpResponse, this);
        }
        this.posAfterHead = 0;
    }

    public int posBeforeHead() {
        return this.posBeforeHead;
    }

    public int posBeforeContent() {
        return this.posBeforeContent;
    }

    public int posAfterHead() {
        return this.posAfterHead;
    }

    public int posAfterContent() {
        return this.posAfterContent;
    }

    public void posBeforeHead(int pos) {
        this.posBeforeHead = pos;
    }

    public void posBeforeContent(int pos) {
        this.posBeforeContent = pos;
    }

    public void posAfterHead(int pos) {
        this.posAfterHead = pos;
    }

    public void posAfterContent(int pos) {
        this.posAfterContent = pos;
    }

    public void resetBeforeHead() {
        posBeforeHead(0);
    }

    public void resetBeforeContent() {
        posBeforeContent(0);
    }

    public void resetAfterHead() {
        posAfterHead(0);
    }

    public void resetAfterContent() {
        posAfterContent(0);
    }

    @Override
    public Iterator<ProxyInterceptor> iterator() {
        return interceptors.iterator();
    }
}
