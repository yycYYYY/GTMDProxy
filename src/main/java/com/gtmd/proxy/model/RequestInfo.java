package com.gtmd.proxy.model;

import java.io.Serializable;
import java.util.Objects;

public class RequestInfo implements Serializable {

    private static final long serialVersionUID = 7312279956235006792L;

    private String host;
    private int port;
    private boolean isHttps;

    public RequestInfo() {
    }

    public RequestInfo(String host, int port, boolean isHttps) {
        this.host = host;
        this.port = port;
        this.isHttps = isHttps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestInfo that = (RequestInfo) o;
        return port == that.port && isHttps == that.isHttps && host.equals(that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, isHttps);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isHttps() {
        return isHttps;
    }

    public void setHttps(boolean https) {
        isHttps = https;
    }
}
