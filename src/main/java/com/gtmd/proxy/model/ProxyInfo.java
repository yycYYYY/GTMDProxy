package com.gtmd.proxy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author yuyongchao
 **/
@Getter
@Setter
@AllArgsConstructor
public class ProxyInfo implements Serializable {

    private static final long serialVersionUID = -5766211000196160917L;
    private RequestInfo requestInfo;
    private String proxyType;

}
