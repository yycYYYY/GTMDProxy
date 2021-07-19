package com.gtmd.proxy.cli;

import com.gtmd.proxy.server.ProxyServer;
import picocli.CommandLine;
import picocli.CommandLine.*;

/**
 * @Author yuyongchao
 **/
@Command(name = "ProxyCli")
public class ProxyCli implements Runnable{

    ProxyServer server = new ProxyServer();
    
    @Option(names = {"-t", "--type"}, required = false, description = "The server type.")
    String type;

    @Override
    public void run() {
        System.out.println("$$GTMD$$ Proxy server start at port: 8088");
        server.init(8088);
    }

    public static void main(String[] args) {
        CommandLine.run(new ProxyCli(), System.err, args);
    }
}
