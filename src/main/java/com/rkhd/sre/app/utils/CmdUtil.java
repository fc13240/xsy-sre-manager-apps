package com.rkhd.sre.app.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;


public class CmdUtil {
    private static Logger logger = LoggerFactory.getLogger(CmdUtil.class);

    public static String telnet(String ip, String port) {
        BufferedReader input = null;
        try {
            Process process = Runtime.getRuntime().exec("telnet " + ip + " " + port);
            //Process process = Runtime.getRuntime().exec("ping " + ip);
            new BufferedOutputStream(process.getOutputStream());
            input = new BufferedReader(new InputStreamReader(process.getErrorStream(), "GBK"));
            StringBuffer cmdout = new StringBuffer();
            String line;
            while ((line = input.readLine()) != null) {
                cmdout.append(line).append(System.getProperty("line.separator"));
            }
            System.out.println(ip + ":" + port + " result: " + cmdout.toString());

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    public static boolean connect(String ip, int port) {
        Socket socket = new Socket();
        try {
            socket.setReceiveBufferSize(8192);
            socket.setSoTimeout(1000);//ms
        } catch (SocketException e) {
            logger.error(e.getMessage(), e);
        }
        InetAddress netAddress = null;
        try {
            netAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            logger.error(e.getMessage());
        }
        SocketAddress socketAddress = new InetSocketAddress(netAddress, port);
        try {
            socket.connect(socketAddress, 2000);
        } catch (IOException e) {
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                //do nothing
            }
        }
        return true;
    }

    public static void main(String[] args) {
        //CmdUtil.telnet("192.168.0.55", "11211");
//        CmdUtil.telnet("192.168.0.78", "6379");
//        System.out.println(CmdUtil.connect("192.168.0.78", 6379));
        System.out.println(CmdUtil.connect("127.0.0.1", 2181));
//        Naming.rebind("rmi://127.0.0.1:6600/PersonService", personService);
    }
}
