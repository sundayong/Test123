//package com.sundayong.telnet;
//
//import com.sundayong.ping.Ping;
//import jdk.internal.org.objectweb.asm.Handle;
//import org.apache.kafka.common.utils.CollectionUtils;
//import org.bson.json.JsonObject;
//
//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.net.Socket;
//import java.sql.SQLOutput;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CountDownLatch;
//import java.util.function.Consumer;
//import java.util.stream.Collectors;
//
//
//public class TelnetUtil {
//    static CountDownLatch countDownLatch = new CountDownLatch(12);
//
//    public static synchronized boolean telnet(String hostname, int port, int timeout) {
//        Socket socket = new Socket();
//        boolean isConnected = false;
//        try {
//            socket.connect(new InetSocketAddress(hostname, port), timeout); // 建立连接
//            isConnected = socket.isConnected(); // 通过现有方法查看连通状态
////            System.out.println(isConnected);    // true为连通
//        } catch (IOException e) {
//            System.out.println("false");        // 当连不通时，直接抛异常，异常捕获即可
//        } finally {
//            try {
//                socket.close();   // 关闭连接
//            } catch (IOException e) {
//                System.out.println("false");
//            }
//        }
//        return isConnected;
//    }
//
//    public static void main(String[] args) throws IOException, InterruptedException {
//
//        int port = 22;
//        int timeout = 200;
//        BufferedReader br = new BufferedReader(new FileReader("/Users/dayongsun/IdeaProjects/test/host1"));
//        List<String> hosts = new ArrayList<>();
//        String host = "";
//        while ((host = br.readLine()) != null) {
//            hosts.add(host);
//        }
//        Map<String, Object> map1 = new ConcurrentHashMap<>();
//        Map<String, Object> map2 = new ConcurrentHashMap<>();
//        hosts.parallelStream().forEach((s) -> {
//            //boolean isConnected = telnet(s, port, timeout);
//            Ping ping = new Ping();
//            boolean isConnected = ping.ping(s, 5, 3000);
//            if (isConnected) {
//                map1.put(s, "通");
//            } else {
//                map2.put(s, "不通");
//            }
//            countDownLatch.countDown();
//        });
//        countDownLatch.await();
//        System.out.println(map1);
//        System.out.println(map2);
//
//        map2.keySet().stream().forEach(s-> System.out.println(s));
//        //System.out.println("telnet " + hostname + " " + port + "\n==>isConnected: " + isConnected);
//    }
//}