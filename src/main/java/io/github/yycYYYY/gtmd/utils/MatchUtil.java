package io.github.yycYYYY.gtmd.utils;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.AsciiString;

import java.util.Objects;

/**
 * @Author yyc
 **/
public class MatchUtil {

    public static boolean matchHost(HttpRequest httpRequest, String expectHost){
        String host = httpRequest.headers().get(HttpHeaderNames.HOST);
        return Objects.equals(host, expectHost);
    }

    public static boolean matchPort(HttpRequest httpRequest, String expectHost){
        return true;
    }
    public static boolean matchUri(HttpRequest httpRequest, String regex) {
        String host = httpRequest.headers().get(HttpHeaderNames.HOST);
        if (host != null && regex != null) {
            String url;
            if (httpRequest.uri().indexOf("/") == 0) {
                if (httpRequest.uri().length() > 1) {
                    url = host + httpRequest.uri();
                } else {
                    url = host;
                }
            } else {
                url = httpRequest.uri();
            }
            return url.matches(regex);
        }
        return false;
    }

    public static boolean matchHeader(HttpHeaders httpHeaders, AsciiString name, String regex) {
        String s = httpHeaders.get(name);
        return s != null && s.matches(regex);
    }

}
