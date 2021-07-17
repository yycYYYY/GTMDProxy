package com.gtmd.proxy.cli;

import picocli.CommandLine;
import picocli.CommandLine.*;

/**
 * @Author yuyongchao
 **/
@Command(name = "ProxyCli")
public class ProxyCli implements Runnable{
    
    @Option(names = {"-t", "--type"}, required = true, description = "The server type.")
    String type;

    @Override
    public void run() {
        System.out.println("hello" + type);
    }

    public static void main(String[] args) {
        CommandLine.run(new ProxyCli(), System.err, args);
    }
}
