package com.gtmd.proxy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class RequestInfo implements Serializable {

    private static final long serialVersionUID = 7312279956235006792L;

    private String host;
    private int port;
    private boolean isHttps;

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
}
