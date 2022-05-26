package io.github.yycYYYY.gtmd.cli;

import io.github.yycYYYY.gtmd.server.ProxyServer;
import picocli.CommandLine;
import picocli.CommandLine.*;

/**
 * @Author yyc
 **/
@Command(name = "ProxyCli")
public class ProxyCli implements Runnable{

    ProxyServer server = new ProxyServer();
    
    @Option(names = {"-t", "--type"}, required = false, description = "The server type.")
    String type;

    @Option(names = {"-pa", "--proxyAddress"}, required = false
            , description = "proxyAddress, such as www.baidu.com:www.google.com")
    String proxyAddress;

    @Override
    public void run() {
        System.out.println("$$GTMD$$ Proxy server start at port: 8088");
        server.start(8088);
    }

    public static void main(String[] args) {
        CommandLine.run(new ProxyCli(), System.err, args);
    }
}
