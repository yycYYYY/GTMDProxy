package io.github.yycYYYY.gtmd.model;

import io.github.yycYYYY.gtmd.constants.ServerType;

import java.io.Serializable;
import java.util.Objects;

/**
 * @Author yyc
 **/

public class ProxyInfo implements Serializable {

    private static final long serialVersionUID = -5766211000196160917L;
    private RequestInfo requestInfo;
    private ServerType proxyType;
    private String localAddress;
    private String remoteAddress;

    public ProxyInfo(RequestInfo requestInfo, ServerType proxyType, String localAddress, String remoteAddress) {
        this.requestInfo = requestInfo;
        this.proxyType = proxyType;
        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    public ProxyInfo() {
    }

    public RequestInfo getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

    public ServerType getProxyType() {
        return proxyType;
    }

    public void setProxyType(ServerType proxyType) {
        this.proxyType = proxyType;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProxyInfo proxyInfo = (ProxyInfo) o;
        return Objects.equals(requestInfo, proxyInfo.requestInfo) && proxyType == proxyInfo.proxyType && Objects.equals(localAddress, proxyInfo.localAddress) && Objects.equals(remoteAddress, proxyInfo.remoteAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestInfo, proxyType, localAddress, remoteAddress);
    }
}
