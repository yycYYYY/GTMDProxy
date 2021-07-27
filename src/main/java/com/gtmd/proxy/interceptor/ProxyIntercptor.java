package com.gtmd.proxy.interceptor;

public interface ProxyIntercptor {

    void beforeRequest();

    void afterResponse();
}
