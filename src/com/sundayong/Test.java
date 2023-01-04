package com.sundayong;


import jdk.nashorn.internal.objects.annotations.Getter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import sun.net.www.http.HttpClient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Test {
    public static void main(String[] args) throws Exception {
        //String url = "http://www.99nn85.com/news/151716.html";
        int num = 151664;
        for (int i = num; i < num + 30; i++) {


            String url = "http://www.99nn85.com/news/" + i + ".html";
            Set<String> selectRule = new HashSet<>();
            selectRule.add("img"); // 博客正文

            CrawlMeta crawlMeta = new CrawlMeta();
            crawlMeta.setUrl(url); // 设置爬取的网址
            crawlMeta.setSelectorRules(selectRule); // 设置抓去的内容


            SimpleCrawlJob job = new SimpleCrawlJob();
            job.setCrawlMeta(crawlMeta);

            job.doFetchPage();

            CrawlResult result = job.getCrawlResult();
            System.out.println(result);
            download(result);
        }
    }

    private static void download(CrawlResult result) throws IOException {
        String path = "/Users/dayongsun/img/123";// + File.separator + Calendar.getInstance().getTime();
        for (Map.Entry<String, List<String>> entry : result.getResult().entrySet()) {
            String key = entry.getKey();
            if ("img".equals(key)) {
                List<String> value = entry.getValue();
                for (int i = 0; i < value.size(); i++) {
                    String s = value.get(i);
                    System.out.println(s);

                    String fileName = s.substring(s.lastIndexOf("/") + 1, s.lastIndexOf("?") == -1 ? s.length() : s.lastIndexOf("?"));
                    fileName = Calendar.getInstance().getTimeInMillis() + "_" + fileName;
                    HttpURLConnection connection = (HttpURLConnection) new URL(s).openConnection();
                    // 设置通用的请求属性
                    connection.setRequestProperty("accept", "*/*");
                    connection.setRequestProperty("connection", "Keep-Alive");
                    connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                    // 建立实际的连接
                    connection.connect();
                    InputStream is = connection.getInputStream();

                    byte[] bs = new byte[1024];

                    File file = new File(path);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    FileOutputStream fos = new FileOutputStream(path + File.separator + fileName);


                    int length = 0;
                    while ((length = is.read(bs)) != -1) {
                        fos.write(bs, 0, length);
                    }

                    fos.close();
                    is.close();
                }
            }
        }
    }

}

class SimpleCrawlJob {

    /**
     * 配置项信息
     */
    private CrawlMeta crawlMeta;


    /**
     * 存储爬取的结果
     */
    private CrawlResult crawlResult;


    /**
     * 执行抓取网页
     */
    public void doFetchPage() throws Exception {

        URL url = new URL(crawlMeta.getUrl());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        BufferedReader in = null;

        StringBuilder result = new StringBuilder();

        try {
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();


            Map<String, List<String>> map = connection.getHeaderFields();


            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } finally {        // 使用finally块来关闭输入流
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }


        doParse(result.toString());
    }

    private void doParse(String html) {
        Document doc = Jsoup.parse(html);

        Map<String, List<String>> map = new HashMap<>(crawlMeta.getSelectorRules().size());
        for (String rule : crawlMeta.getSelectorRules()) {
            List<String> list = new ArrayList<>();
            for (Element element : doc.select(rule)) {
                list.add(element.attr("src"));
            }

            map.put(rule, list);
        }


        this.crawlResult = new CrawlResult();
        this.crawlResult.setHtmlDoc(doc);
        this.crawlResult.setUrl(crawlMeta.getUrl());
        this.crawlResult.setResult(map);
    }

    public void setCrawlMeta(CrawlMeta crawlMeta) {
        this.crawlMeta = crawlMeta;
    }

    public CrawlResult getCrawlResult() {
        return crawlResult;
    }
}

class CrawlResult {

    /**
     * 爬取的网址
     */
    private String url;


    /**
     * 爬取的网址对应的 DOC 结构
     */
    private Document htmlDoc;


    /**
     * 选择的结果，key为选择规则，value为根据规则匹配的结果
     */
    private Map<String, List<String>> result;

    @Override
    public String toString() {
        return "result=" + result;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Document getHtmlDoc() {
        return htmlDoc;
    }

    public void setHtmlDoc(Document htmlDoc) {
        this.htmlDoc = htmlDoc;
    }

    public Map<String, List<String>> getResult() {
        return result;
    }

    public void setResult(Map<String, List<String>> result) {
        this.result = result;
    }
}

class CrawlMeta {

    /**
     * 待爬去的网址
     */

    private String url;

    /**
     * 获取指定内容的规则, 因为一个网页中，你可能获取多个不同的内容， 所以放在集合中
     */

    private Set<String> selectorRules;


    // 这么做的目的就是为了防止NPE, 也就是说支持不指定选择规则
    public Set<String> getSelectorRules() {
        return selectorRules != null ? selectorRules : new HashSet<>();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSelectorRules(Set<String> selectRule) {
        this.selectorRules = selectRule;
    }
}