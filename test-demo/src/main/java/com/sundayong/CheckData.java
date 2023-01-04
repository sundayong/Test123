package com.sundayong;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.*;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

public class CheckData {

    public static ObjectMapper objectMapper = new ObjectMapper();

    public static String ci_guid;
    public static String ci_device_guid;
    public static String base_name;
    public static List<String> ciguidlist;

    public static void main(String[] args) throws ClassNotFoundException, IOException {

        //读取文件获取ciguidlist
        ciguidlist = readFile();

        //输入
        //String ci_guid = "29.167.1.1.3.10.1.4.1.1";
        List<String> list = new LinkedList<>();
        list.add("智航测点id\t动环测点id\t设备id\tkafka值\tkafka时间戳");
        for (String tmp_ci_guid : ciguidlist) {
            checkData(tmp_ci_guid, list);
        }
        for (String s : list) {
            System.out.println(s);
        }
    }

    /**
     * 读取文件
     *
     * @return
     * @throws IOException
     */
    private static List<String> readFile() throws IOException {
        List<String> resultList = new ArrayList<>();
        String path = "/Users/dayongsun/IdeaProjects/test/ciguid.txt";

        FileInputStream fis = new FileInputStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String data = null;

        while ((data = br.readLine()) != null) {
            resultList.add(data);
        }
        return resultList;
    }

    /**
     * 获取指定长度
     *
     * @param ci_guid
     * @return
     */
    public static String getBaseData(String ci_guid, int start, int length) {
        if (start < 0 || start > 10 || length < 0 || length > 10) {
            return "";
        }
        String[] arr = ci_guid.split("\\.");
        String device_id = "";
        for (int i = start; i < length; i++) {
            device_id += arr[i] + ".";
        }
        ci_device_guid = device_id.substring(0, device_id.length() - 1);
        return ci_guid.split("\\.")[0];
    }

    /**
     * 查询mysql
     *
     * @return
     */
    public static String queryMysql(String result) {
        //1.根据域号查找对应的表
        ResultSet rs = null;
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://192.168.233.26:17918/indc_south_system?useSSL=false",
                    "root",
                    "e8929c202bd417a9");

            statement = connection.createStatement();
            String sql = "select * from reflect_" + base_name + " where ci_guid like '%" + ci_guid + "%'";

            rs = statement.executeQuery(sql);

            boolean flag = false;
            //2.判断数据正确性
            while (rs.next()) {
                String ci_guid1 = rs.getString("ci_guid");
                String dh_guid = rs.getString("dh_guid");
                String ci_device_guid = rs.getString("ci_device_guid");

                result += ci_guid1 + "\t" + dh_guid + "\t" + ci_device_guid;
                //System.out.print(ci_guid1 + "   " + dh_guid + "   " + ci_device_guid);
                flag = true;
            }
            if (!flag) {
                String sql2 = "select * from reflect_" + base_name + " where ci_guid like '%" +
                        getBaseData(ci_guid, 6, 4) + "%' and ci_device_guid =" + ci_device_guid;
                rs = statement.executeQuery(sql2);

                while (rs.next()) {
                    String ci_guid1 = rs.getString("ci_guid");
                    String dh_guid = rs.getString("dh_guid");
                    String ci_device_guid = rs.getString("ci_device_guid");

                    result += ci_guid1 + "\t" + dh_guid + "\t" + ci_device_guid;
                    //System.out.print(ci_guid1 + "   " + dh_guid + "   " + ci_device_guid);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                statement.close();
                connection.close();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return "";
    }

    /**
     * 核对数据
     *
     * @param ci_guid
     * @param list
     * @throws ClassNotFoundException
     */
    private static void checkData(String ci_guid, List<String> list) throws ClassNotFoundException {

        String result = "";

        base_name = getBaseData(ci_guid, 0, 6);
        queryMysql(result);
        queryKafka(result);
        list.add(result);
    }

    /**
     * 查询kafka
     *
     * @param result
     * @return
     */
    private static void queryKafka(String result) {
        //3.查询kafka
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.233.21:31090,192.168.233.21:31091,192.168.233.21:31092" +
                ",192.168.233.21:31093,192.168.233.21:31094,192.168.233.21:31095,192.168.233.21:31096,192.168.233.21:31097" +
                ",192.168.233.21:31098 ");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "checkdata_sdy");
        properties.setProperty("enable.auto.commit", "true");
        properties.setProperty("auto.commit.interval.ms", "1000");
        //properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);
        //properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        //properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"true");

        try (KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties)) {

            String topic_name = "ep_" + base_name + "_1_2_3_1_1";
            System.out.println(topic_name);
            kafkaConsumer.subscribe(Collections.singletonList(topic_name), new ConsumerRebalanceListener() {
                @Override
                public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                    System.out.println("com.sundayong.CheckData.onPartitionsRevoked");
                    Iterator<TopicPartition> iterator = partitions.iterator();
                    while (iterator.hasNext()) {
                        System.out.println(iterator.next().partition());
                    }
                }

                @Override
                public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                    System.out.println("com.sundayong.CheckData.onPartitionsAssigned");
                    Iterator<TopicPartition> iterator = partitions.iterator();
                    while (iterator.hasNext()) {
                        System.out.println(iterator.next().partition());
                    }
                }
            });

            //手动指定offset从最开始开始查
            //Set<TopicPartition> partitions = kafkaConsumer.assignment();
            //while (partitions.size() == 0) {
            //    ConsumerRecords<String, String> poll = kafkaConsumer.poll(Duration.ofMillis(10));
            //    partitions = kafkaConsumer.assignment();
            //    //System.out.print(partitions);
            //}
            //System.out.println("partitions = " + partitions);
            //kafkaConsumer.seekToBeginning(partitions);

            boolean flag = false;
            while (true) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(10));

                if (!records.isEmpty()) {
                    //System.out.println("本次轮训到数据条数：" + records.count());
                    for (ConsumerRecord<String, String> record : records) {
                        if (record.value().contains(ci_guid)) {
                            HashMap<String, Object> hashMap = objectMapper.readValue(record.value(), HashMap.class);
                            List<Map<String, Object>> devices = (List<Map<String, Object>>) hashMap.get("devices");
                            List<Map<String, Object>> device = devices.stream()
                                    .filter(e -> e.get("device_id").toString().equals(ci_device_guid))
                                    .sorted((o1, o2) -> Long.parseLong(o1.get("timestamp").toString()) < Long.parseLong(o2.get("timestamp").toString()) ? 1 : -1)
                                    .collect(Collectors.toList());

                            Map<String, Object> data = (Map<String, Object>) device.get(0).get("data");
                            Map<String, Object> dataVal = (Map<String, Object>) data.get(ci_guid);
                            String value = (String) dataVal.get("value");
                            Integer timestamp = (Integer) dataVal.get("timestamp");
                            LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.of("+8"));
                            result += "\t" + value + "\t" + localDateTime;
                            //System.out.println(" " + value + " " + localDateTime);
                            flag = true;
                            break;
                        }
                    }
                } else {
                    System.out.print(". ");
                }
                if (flag) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //return result;
    }
}
