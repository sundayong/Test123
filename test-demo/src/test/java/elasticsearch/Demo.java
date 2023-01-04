package elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sundayong.App;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.client.core.GetSourceRequest;
import org.elasticsearch.client.core.GetSourceResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.xcontent.XContentType;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = App.class)
public class Demo {


    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    public void esTest() {
        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(matchAllQueryBuilder)
                .build();

        String indexName = "ads_indc_current_2022.08";
        IndexCoordinates index = IndexCoordinates.of(indexName);
        SearchHits<HashMap> result = elasticsearchRestTemplate.search(query, HashMap.class, index);
        System.out.println(result);
    }

    @Test
    public void test1() throws IOException { //192.168.233.25:6902
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("192.168.233.25", 6902, "http")));
        GetRequest getRequest = new GetRequest("test", "user", "1");
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        boolean exist = client.exists(getRequest, RequestOptions.DEFAULT);
        if (exist) {
            System.out.println("文档存在");
        } else {
            System.out.println("文档不存在");
        }
        client.close();
    }

    RestHighLevelClient client;

    @Before
    public void before() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("elastic", "pGyaAoJ1E0Wr366kBjgw"));  //es账号密码（默认用户名为elastic）
        client = new RestHighLevelClient(
                RestClient.builder(
                                new HttpHost("192.168.233.25", 6902, "http"))
                        .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                                httpClientBuilder.disableAuthCaching();
                                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                            }
                        }));
    }

    @Test
    public void test12() throws IOException, InterruptedException {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("elastic", "pGyaAoJ1E0Wr366kBjgw"));  //es账号密码（默认用户名为elastic）
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                                new HttpHost("192.168.233.25", 6902, "http"))
                        .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                                httpClientBuilder.disableAuthCaching();
                                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                            }
                        }));

        GetRequest getRequest = new GetRequest("ads_indc_current_2022.08", "line_loss_current_2_202208");
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");

        GetSourceRequest getSourceRequest = new GetSourceRequest("ads_indc_current_2022.08", "line_loss_current_2_202208");


        System.out.println("===========start");
        GetResponse documentFields = client.get(getRequest, RequestOptions.DEFAULT);
        for (DocumentField documentField : documentFields) {
            System.out.println(documentField.getName());
        }
        System.out.println("===========end");
        GetSourceResponse source = client.getSource(getSourceRequest, RequestOptions.DEFAULT);

        Map<String, Object> source1 = source.getSource();
        System.out.println("source1 = " + source1);

        client.close();
    }

    /**
     * 创建索引
     */
    @Test
    public void name() throws IOException {
        CreateIndexRequest demoTest123 = new CreateIndexRequest("demo_test_123");
        CreateIndexResponse createIndexResponse = client.indices().create(demoTest123, RequestOptions.DEFAULT);
        boolean acknowledged = createIndexResponse.isAcknowledged();
        System.out.println(acknowledged);

    }

    /**
     * 删除索引
     */
    @Test
    public void delete() {

    }

    /**
     * 查入数据
     */
    @Test
    public void name1() throws IOException {
        Random random = new Random();
        BulkRequest bulkRequest = new BulkRequest();
        var date = LocalDate.of(2022, 11, 1);
        for (int i = 0; i < 20; i++) {
            var map = new HashMap<String, Object>();

            map.put("value1", 100 + i * random.nextInt(10));
            map.put("value2", 200 + i * random.nextInt(20));
            map.put("date", date.plusDays(i));
            bulkRequest.add(new IndexRequest().index("demo_test_123").source(map, XContentType.JSON));
        }
        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk.getTook());
        System.out.println(bulk.getItems());


        //IndexResponse index = client.index(request, RequestOptions.DEFAULT);
        //System.out.println(index.getResult());
    }

    class Node {
        int value;
        Node next;

        public Node(int value) {
            this.value = value;
        }

        //@Override
        //public boolean equals(Object o) {
        //    if (this == o) return true;
        //    if (!(o instanceof Node node)) return false;
        //    return value == node.value;
        //}
        //
        //@Override
        //public int hashCode() {
        //    return Objects.hash(value);
        //}
        //
        //@Override
        //public String toString() {
        //    return "Node{" +
        //            "value=" + value +
        //            '}';
        //}
    }

    @Test
    public void test23() {

        Node node1 = new Node(1);
        Node node2 = new Node(1);

        Map<Node, Object> map = new HashMap<>();
        map.put(node1, "我是node");

        System.out.println(node1);
        System.out.println(node2);
        System.out.println(map.containsKey(node1));
        System.out.println(map.containsKey(node2));
    }

    @Test
    public void test1234() {
        String str = "123";
        test55(str);
        System.out.println(str);

    }

    public void test55(String text) {
        text = "1234";
    }

    @Test
    public void test11() {
        Node node = new Node(1);
        test55(node);
        System.out.println(node.value);
    }

    @Test
    public void test122() {
        Node node = new Node(1);
        node.next = new Node(2);
        node.next.next = new Node(3);
        test555(node);
        System.out.println(node);
        System.out.println(node.value);
    }

    public void test55(Node text) {
        text.value = 2;
    }


    public void test555(Node text) {
        System.out.println(text);
        text = text.next;

    }

    @Test
    public void test121() {

        class T<E> {
            E[] data;

            public T() {
                data = (E[]) new Object[10];
            }

            E get(int index) {
                return data[index];
            }
        }
    }

    @Test
    public void 托尔斯泰222() {
        String str = "全国,INSTANCE-5650,INSTANCE-7680,";
        String str1 = "全国,INSTANCE-5650,INSTANCE-7680,请选择";
        String[] split = str.split(",");
        String[] split1 = str1.replace("请选择", "").split(",");
        System.out.println(Arrays.toString(split));
        System.out.println(Arrays.toString(split1));
        System.out.println(split[split.length - 1]);
        System.out.println(split1[split.length - 1]);
    }

    @Test
    public void qwe() {
        Map<String, String> map = new HashMap<>();
        Random random = new Random();
        for (int i = 1; i < 10000000; i++) {
            map.put(i + random.nextInt(i) + "", i + random.nextInt(i) + "");
        }
        System.out.println(System.currentTimeMillis());
        System.out.println(map.containsKey("13214113"));
        System.out.println(System.currentTimeMillis());
        System.out.println(map.containsValue("12334224"));
        System.out.println(System.currentTimeMillis());
    }

    @Test
    public void qqq() {
        var list = new ArrayList<>();
        list.add("123");
        list.add("1234");
        list.add("1235");
        for (var ltr = list.listIterator(); ltr.hasNext(); ) {
            //System.out.println(ltr.next());

            if(ltr.next().equals("1234")){
                ltr.remove();
            }
        }

        System.out.println(list);


    }
}
