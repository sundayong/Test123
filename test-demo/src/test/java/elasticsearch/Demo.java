package elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sundayong.App;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.LinkedQueueAtomicNode;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.lucene.util.SetOnce;
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
import org.springframework.aop.aspectj.annotation.LazySingletonAspectInstanceFactoryDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.*;

//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = App.class)
public class Demo {


    //@Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    //public Demo(){}


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

            if (ltr.next().equals("1234")) {
                ltr.remove();
            }
        }

        System.out.println(list);
        System.out.println(123);
        System.out.println(123);

    }

    @Test
    public void name_001() {

        ListNode listNode1 = getListNode(4);
        print(listNode1);

        System.out.println();
        ListNode node = ReverseList(listNode1);
        print(node);
        System.out.println();
        ListNode node1 = ReverseList(node);
        print(node1);
        System.out.println();
        ListNode node2 = ReverseList(node1);
        print(node2);
    }

    private ListNode getListNode(int size) {
        if (size == 0) {
            return null;
        }
        ListNode listNode = new ListNode(1);
        ListNode head = listNode;
        for (int i = 2; i <= size; i++) {
            listNode.next = new ListNode(i);
            listNode = listNode.next;
        }
        return head;
    }

    public ListNode ReverseList(ListNode head) {

        ListNode pre = null;
        ListNode next;
        ListNode cur = head;

        while (cur != null) {
            next = cur.next;
            cur.next = pre;

            pre = cur;
            cur = next;
        }
        return pre;
    }

    void printReverse(ListNode head) {
        ListNode tmp = head;
        Stack<ListNode> stack = new Stack<>();
        while (tmp != null) {
            stack.add(tmp);
            tmp = tmp.next;
        }
        while (!stack.empty()) {
            System.out.println(stack.pop().val);
        }
    }

    public void print(ListNode head) {
        System.out.println("----------------------");
        ListNode node = head;
        for (; node != null; node = node.next) {
            System.out.print(node.val + " ");
        }
        System.out.println();
    }

    @Test
    public void name__002() {
        ListNode listNode = getListNode(2);
        print(listNode);
        ListNode node = reverseBetween(listNode, 1, 2);
        print(node);
    }

    /**
     * @param head ListNode类
     * @param m    int整型
     * @param n    int整型
     * @return ListNode类
     */
    public ListNode reverseBetween(ListNode head, int m, int n) {
        if (m == n) {
            return head;
        }
        ListNode tmp = head;
        ListNode head1 = null;
        ListNode tail = null;
        ListNode pre = null;
        ListNode cur;
        ListNode next;
        int index = 1;
        while (tmp != null) {
            if (index == m - 1) {
                head1 = tmp;
                tmp = tmp.next;
            } else if (index == m) {
                tail = tmp;
                cur = tmp;
                next = tmp.next;
                cur.next = pre;

                pre = cur;
                tmp = next;

            } else if (index > m && index < n) {
                cur = tmp;
                next = tmp.next;
                cur.next = pre;

                pre = cur;
                tmp = next;

            } else if (index == n) {
                cur = tmp;
                next = tmp.next;
                cur.next = pre;

                tail.next = next;
                if (head1 != null) {
                    head1.next = cur;
                } else {
                    head = cur;
                }
            } else {
                tmp = tmp.next;
            }
            index++;
        }
        return head;
    }

    /**
     * 反转连续k个链表
     */
    @Test
    public void name_003() {
        ListNode listNode = getListNode(0);
        print(listNode);
        ListNode node = reverseKGroup(listNode, 1);
        print(node);
    }

    /**
     * @param head ListNode类
     * @param k    int整型
     * @return ListNode类
     */
    public ListNode reverseKGroup(ListNode head, int k) {
        // write code here
        ListNode pre = null;
        ListNode cur = null;
        ListNode next = null;
        ListNode tmp = head;
        int index = 0;
        ListNode head1 = null;
        ListNode tail = null;
        boolean flag = false;
        while (tmp != null) {

            cur = tmp;
            next = tmp.next;

            if (index == 0) {
                head1 = tmp;
                flag = false;
            } else if (index == k - 1) {
                tail = tmp;
                reverseKGroup1(head1, tail);
                //链接外部
                if (pre != null) {
                    pre.next = tail;
                } else {
                    head = tail;
                }
                pre = head1;
                flag = true;
            }
            tmp = next;
            index = ++index % k;

        }
        if (!flag && pre != null) {
            pre.next = head1;
        }
        return head;
    }

    class ListNode {
        int val;
        ListNode next = null;

        ListNode(int val) {
            this.val = val;
        }
    }

    public void reverseKGroup1(ListNode start, ListNode end) {
        ListNode s = start;
        ListNode e = end;
        ListNode pre = null;
        ListNode cur = null;
        ListNode next = null;
        while (s != e) {

            cur = s;
            next = s.next;
            cur.next = pre;

            pre = cur;
            s = next;
        }
        e.next = pre;
        System.out.println();
    }

    @Test
    public void name_004() {
        ListNode node1;
        ListNode node11;
        //准备数据
        {
            node1 = new ListNode(1);
            ListNode node2 = new ListNode(2);
            ListNode node3 = new ListNode(3);
            node1.next = node2;
            node2.next = node3;

            node11 = new ListNode(3);
            ListNode node22 = new ListNode(3);
            ListNode node33 = new ListNode(3);
            node11.next = node22;
            node22.next = node33;
        }
        print(node1);
        print(node11);
        ListNode merge = Merge(node1, node11);
        print(merge);
    }

    public ListNode Merge(ListNode list1, ListNode list2) {


        ListNode i = list1;
        ListNode j = list2;
        ListNode cur = new ListNode(-999);
        ListNode head = cur;

        while (i != null || j != null) {

            if (i == null) {
                cur.next = j;
                break;
            }
            if (j == null) {
                cur.next = i;
                break;
            }

            ListNode iNext = i.next;
            ListNode jNext = j.next;
            if (i.val <= j.val) {
                cur.next = i;
                cur = cur.next;
                i = iNext;
            } else {
                cur.next = j;
                cur = cur.next;
                j = jNext;
            }
        }
        return head.next;
    }

    @Test
    public void name_005() {
        ListNode listNode1 = new ListNode(1);
        ListNode listNode2 = new ListNode(2);
        listNode1.next = listNode2;

        ListNode listNode3 = new ListNode(1);
        ListNode listNode4 = new ListNode(4);
        ListNode listNode5 = new ListNode(5);
        listNode3.next = listNode4;
        listNode4.next = listNode5;

        ListNode listNode6 = new ListNode(6);

        ArrayList<ListNode> list = new ArrayList<>();
        list.add(listNode1);
        list.add(listNode3);
        list.add(listNode6);
        print(mergeKLists(list));
    }

    public ListNode mergeKLists(ArrayList<ListNode> lists) {

        if (lists == null || lists.size() == 0) {
            return null;
        }
        ListNode cur = null;
        for (ListNode listNode : lists) {
            cur = Merge(cur, listNode);
        }
        return cur;
    }

    public boolean hasCycle1(ListNode head) {

        Set<Integer> set = new HashSet<>();
        for (ListNode tmp = head; tmp != null; tmp = tmp.next) {
            if (set.contains(tmp.hashCode())) {
                return true;
            }
            set.add(tmp.hashCode());
        }
        return false;
    }

    public ListNode hasCycle(ListNode pHead) {

        Set<Integer> set = new HashSet<>();
        for (ListNode tmp = pHead; tmp != null; tmp = tmp.next) {
            if (set.contains(tmp.hashCode())) {
                return tmp;
            }
            set.add(tmp.hashCode());
        }
        return null;
    }

    @Test
    public void name_006() {
        ListNode listNode3 = new ListNode(3);
        ListNode listNode4 = new ListNode(2);
        ListNode listNode5 = new ListNode(0);
        ListNode listNode6 = new ListNode(-4);
        listNode3.next = listNode4;
        listNode4.next = listNode5;
        listNode5.next = listNode6;
        listNode6.next = listNode4;

        boolean b = hasCycle1(listNode3);
        System.out.println(b);
    }

    @Test
    public void name_007() {
        byte a = 127;
        byte b = 126;
        b += a;

        //String s;
        //System.out.println(s);


        //byte c =2;
        //byte d =3;
        //d = c+d;
        System.out.println(a);
    }

    /**
     * 代码中的类名、方法名、参数名已经指定，请勿修改，直接返回方法规定的值即可
     *
     * @param pHead ListNode类
     * @param k     int整型
     * @return ListNode类
     */
    public ListNode FindKthToTail(ListNode pHead, int k) {
        // write code here
        ListNode node = pHead;
        int index = 0;
        Stack<ListNode> stack = new Stack<>();
        while (node != null) {
            stack.add(node);
            index++;
            node = node.next;
        }
        if (index < k || k == 0) {
            return null;
        }
        while (k > 1) {
            stack.pop();
            k--;
        }
        return stack.pop();
    }

    @Test
    public void name_008() {
        ListNode listNode = getListNode(2);
        ListNode listNode1 = FindKthToTail(listNode, 2);
        print(listNode1);
    }

    /**
     * @param head ListNode类
     * @param n    int整型
     * @return ListNode类
     */
    public ListNode removeNthFromEnd(ListNode head, int n) {
        // write code here
        ListNode cur = head;
        ListNode pre = null;
        ListNode next = null;
        int index = 0;
        Stack<ListNode> stack = new Stack<>();
        while (cur != null) {
            stack.add(cur);

            index++;
            cur = cur.next;
        }
        if (index < n || n == 0) {
            return null;
        }
        ListNode pop = null;
        while (n > 0) {
            pop = stack.pop();
            n--;
        }
        System.out.println(pop.val);
        cur = head;
        while (cur != null) {

            next = cur.next;
            if (cur == pop) {
                if (pre == null) {
                    head = cur.next;
                } else {
                    pre.next = cur.next;
                }
            }

            pre = cur;
            cur = next;

        }
        return head;
    }

    @Test
    public void name_009() {
        ListNode listNode = getListNode(2);
        ListNode listNode1 = removeNthFromEnd(listNode, 2);
        print(listNode1);
    }

    public ListNode FindFirstCommonNode(ListNode pHead1, ListNode pHead2) {

        Set<ListNode> set1 = new HashSet<>();
        ListNode p1 = pHead1;
        while (p1 != null) {
            set1.add(p1);
            p1 = p1.next;
        }
        ListNode p2 = pHead2;
        while (p2 != null) {
            if (set1.contains(p2)) {
                return p2;
            }
            p2 = p2.next;
        }
        return null;
    }

    @Test
    public void name_0010() {
        ListNode listNode = getListNode(3);// 1 2 3
        ListNode listNode6 = new ListNode(6);
        ListNode listNode7 = new ListNode(7);
        listNode.next.next.next = listNode6;
        listNode6.next = listNode7;

        ListNode listNode4 = new ListNode(4);
        ListNode listNode5 = new ListNode(5);
        listNode4.next = listNode5;
        listNode5.next = listNode6;

        ListNode listNode1 = FindFirstCommonNode(listNode, listNode4);
        print(listNode1);

    }

    /**
     * @param head1 ListNode类
     * @param head2 ListNode类
     * @return ListNode类
     */
    public ListNode addInList(ListNode head1, ListNode head2) {
        // write code here
        //  9 3 7
        //    6 3
        //1 0 0 0
        System.out.println("开始:" + System.currentTimeMillis());

        if (head1 == null) {
            return head2;
        }
        if (head2 == null) {
            return head1;
        }

        ListNode h1 = head1;
        ListNode h2 = head2;

        List<Integer> str1 = new ArrayList<>();
        List<Integer> str2 = new ArrayList<>();
        int ii = 0;
        while (h1 != null) {
            str1.add(h1.val);
            h1 = h1.next;
        }

        System.out.println("11遍历完第一个:" + System.currentTimeMillis());
        ii = 0;
        while (h2 != null) {
            str2.add(h2.val);
            h2 = h2.next;
        }
        System.out.println("11遍历完第二个:" + System.currentTimeMillis());
        //Stack<Integer> stack = new Stack<>();
        int p = 0;
        int index = 1;
        int[] res = new int[Math.max(str1.size(), str2.size()) + 1];
        int q = 0;
        System.out.println("1:" + System.currentTimeMillis());
        for (int i = str1.size() - 1, j = str2.size() - 1; i >= 0 || j >= 0; i--, j--, index *= 10, q++) {

            if (i < 0) {
                int i1 = str2.get(j) + p;
                //stack.add(i1 % 10);
                res[q] = (i1 % 10);
                p = i1 / 10;
                continue;
            }
            if (j < 0) {
                int i1 = str1.get(i) + p;
                //stack.add(i1 % 10);
                res[q] = (i1 % 10);
                p = i1 / 10;
                continue;
            }
            int i1 = str2.get(j) + str1.get(i) + p;
            //stack.add(i1 % 10);
            res[q] = i1 % 10;
            p = i1 / 10;
        }
        if (p != 0) {
            //stack.add(p);
            res[q] = p;
        }
        System.out.println("2:" + System.currentTimeMillis());
        ListNode cur = new ListNode(-999);
        ListNode head = cur;
        for (int i = res.length - 1; i >= 0; i--) {
            cur.next = new ListNode(res[i]);
            cur = cur.next;
        }
        System.out.println("3:" + System.currentTimeMillis());
        return head.next.val == 0 ? head.next.next : head.next;
    }


    @Test
    public void name_0011() {

        int length = 100000;
        ListNode listNode9 = new ListNode(9);
        ListNode h1 = listNode9;
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            listNode9.next = new ListNode(random.nextInt(10));
            listNode9 = listNode9.next;
        }

        ListNode listNode6 = new ListNode(9);
        ListNode h2 = listNode6;
        for (int i = 0; i < length; i++) {
            listNode6.next = new ListNode(random.nextInt(10));
            listNode6 = listNode6.next;
        }
        //
        //ListNode listNode9 = new ListNode(9);
        //ListNode listNode3 = new ListNode(3);
        //ListNode listNode7 = new ListNode(7);
        //listNode9.next = listNode3;
        //listNode3.next = listNode7;
        //
        //ListNode listNode6 = new ListNode(6);
        //ListNode listNode1 = new ListNode(3);
        //listNode6.next = listNode1;
        //
        //print(listNode9);
        //print(listNode6);
        ListNode listNode = addInList(h1, h2);

        //print(listNode);
    }

    @Test
    public void name_0012() {
        System.out.println(Long.MAX_VALUE);
    }

    /**
     * @param head ListNode类 the head node
     * @return ListNode类
     */
    public ListNode sortInList(ListNode head) {
        // write code here
        List<Integer> list = new ArrayList<>();
        ListNode h1 = head;
        while (h1 != null) {
            list.add(h1.val);
            h1 = h1.next;
        }
        Collections.sort(list);
        ListNode cur = new ListNode(-99);
        ListNode head1 = cur;

        for (Integer integer : list) {
            cur.next = new ListNode(integer);
            cur = cur.next;
        }
        return head1.next;
    }

    @Test
    public void name_0013() {
        ListNode listNode1 = new ListNode(5);
        ListNode listNode2 = new ListNode(4);
        ListNode listNode3 = new ListNode(3);
        ListNode listNode4 = new ListNode(1);
        ListNode listNode5 = new ListNode(2);

        listNode1.next = listNode2;
        listNode2.next = listNode3;
        listNode3.next = listNode4;
        listNode4.next = listNode5;

        print(listNode1);

        ListNode listNode = sortInList(listNode1);
        print(listNode);
    }

    /**
     * @param head ListNode类 the head
     * @return bool布尔型
     */
    public boolean isPail(ListNode head) {
        // write code here
        if (head == null) {
            return false;
        }
        StringBuffer sb = new StringBuffer();
        StringBuffer sb1 = new StringBuffer();
        Stack<Integer> stack = new Stack<>();
        ListNode h1 = head;
        while (h1 != null) {
            sb.append(h1.val);
            stack.add(h1.val);
            h1 = h1.next;
        }
        while (!stack.isEmpty()) {
            sb1.append(stack.pop());
        }


        return sb.toString().equals(sb1
                .toString());
    }

    @Test
    public void name_0014() {


        //ListNode listNode1 = new ListNode(1);
        //ListNode listNode2 = new ListNode(2);
        //ListNode listNode3 = new ListNode(2);
        //ListNode listNode4 = new ListNode(1);
        //
        //listNode1.next = listNode2;
        //listNode2.next = listNode3;
        //listNode3.next = listNode4;

        int length = 100000;
        ListNode listNode9 = new ListNode(9);
        ListNode h1 = listNode9;
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            listNode9.next = new ListNode(random.nextInt(10));
            listNode9 = listNode9.next;
        }

        System.out.println(isPail(h1));
    }

    /**
     * 代码中的类名、方法名、参数名已经指定，请勿修改，直接返回方法规定的值即可
     *
     * @param head ListNode类
     * @return ListNode类
     */
    public ListNode oddEvenList(ListNode head) {
        // write code here
        if (head == null) {
            return null;
        }
        ListNode h1 = head;
        List<ListNode> list1 = new ArrayList<>();
        List<ListNode> list2 = new ArrayList<>();
        boolean flag = true;
        while (h1 != null) {
            if (flag) {
                list1.add(h1);
                flag = false;
            } else {
                list2.add(h1);
                flag = true;
            }
            h1 = h1.next;
        }
        ListNode cur = new ListNode(-99);
        ListNode head1 = cur;

        for (ListNode listNode : list1) {
            cur.next = listNode;
            cur = cur.next;
        }
        for (ListNode listNode : list2) {
            cur.next = listNode;
            cur = cur.next;
        }
        return head1.next;
    }

    @Test
    public void name_0015() {
        ListNode listNode = getListNode(6);
        print(listNode);
        ListNode listNode1 = oddEvenList(listNode);
        print(listNode1);
    }

    @Test
    public void name_0016() {
        Queue queue = new ArrayDeque();
        queue.add(1);
        System.out.println(queue.peek());
        System.out.println(queue);
        System.out.println(queue.poll());
        System.out.println(queue);
    }

    /**
     * top3 largest
     */
    @Test
    public void name_0017() {
        int arr[] = {10, 4, 3, 50, 50, 23, 90};
        //90, 50, 23
        int second;
        int third;
        int first = second = third = Integer.MIN_VALUE;
        for (int i : arr) {
            if (i > first) {
                third = second;
                second = first;
                first = i;
            } else if (i > second && i != first) {
                third = second;
                second = i;
            } else if (i > third && i != second) {
                third = i;
            }
        }
        System.out.println(first + "," + second + "," + third);
    }

    // Function returns the second
    // largest elements
    int print2largest(int arr[], int n) {
        // code here
        if (n < 2) {
            return -1;
        }
        int first, second;
        first = second = Integer.MIN_VALUE;
        for (int i : arr) {
            if (i > first) {

                second = first;
                first = i;
            } else if (i > second && i != first) {
                second = i;
            }
        }
        if (second == Integer.MIN_VALUE) {
            return -1;
        }
        return second;
    }

    @Test
    public void name_0018() {
        int Arr[] = {12, 35, 1, 10, 34, 1};
        System.out.println(print2largest(Arr, 6));
    }

    void pushZerosToEnd(int[] arr, int n) {
        // code here
        int[] a = new int[n];
        for (int i = 0, j = 0; i < arr.length; i++) {
            if (arr[i] != 0) {
                a[j++] = arr[i];
            }
        }
        for (int i = 0, j = 0; i < a.length; i++) {
            arr[j++] = a[i];
        }
    }

    void pushZerosToEnd1(int[] arr, int n) {
        // code here
        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != 0) {
                arr[count++] = arr[i];
            }
        }
        while (count < n) {
            arr[count++] = 0;
        }
    }

    @Test
    public void name_0019() {
        //N = 5
        //Arr[] = {3, 5, 0, 0, 4}
        int Arr[] = {3, 5, 0, 0, 4};
        pushZerosToEnd(Arr, 5);
        System.out.println(Arrays.toString(Arr));
    }

    ArrayList<Integer> assign(int a[], int n) {
        // Complete the function
        ArrayList<Integer> result = new ArrayList(n);
        int[] a1 = new int[n];
        if (n == 0) {
            return result;
        }
        if (n == 1) {
            result.add(a[0]);
            return result;
        }
        Arrays.sort(a);
        int index = 1;
        for (int i = n - 1; i >= n / 2 && index < n; i--) {
            a1[index] = a[i];
            index += 2;
        }
        index = 0;
        for (int i = n / 2 - 1; i >= 0 && index < n; i--) {
            a1[index] = a[i];
            index += 2;
        }
        for (int i : a1) {
            result.add(i);
        }
        return result;
    }

    @Test
    public void name_0020() {
        //int A[] = {1, 2, 2, 1};
        //int A[] = {1, 3, 2};
        //int A[] = {1, 2, 3, 4, 5};
        //int n = 16;
        //int A[] = {18, 18, 9, 3, 16, 18, 11, 1, 13, 5, 6, 7, 16, 7, 16, 17};
        int n = 5;
        int A[] = {3, 18, 12, 4, 18};
        System.out.println(assign(A, n));

    }

    @Test
    public void name_0021() throws URISyntaxException {
        //String url = "https://www.supercloudsms.com/zh/message/33644628617.html";
        String url = "http://172.16.50.223:9099/transformation/getPositionByStandardId?standardId=2.1.2.3.1.1&userId=&queryType=1";
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(url);
        HashMap<Object, Object> map = new HashMap<>();
        map = restTemplate.getForObject(uri, map.getClass());
        System.out.println();
    }

    @Test
    public void name_0022() {
        List<Integer> list = new ArrayList<>();
        list.add(0, 1);
        list.add(1, 2);
        list.add(0, 3);
        System.out.println(list);
    }

    // temp: input array
    // n: size of array
    //Function to rearrange  the array elements alternately.
    public static void rearrange(long arr[], int n) {

        // Your code here
        if (n == 0 || n == 1) {
            return;
        }
        int p, q;
        p = 0;
        q = arr.length - 1;
        for (int i = 0; i < arr.length; i++) {
            if (i % 2 == 0) {
                arr[i] = arr[q--];
            } else {
                arr[i] = arr[p++];
            }
        }

    }

    @Test
    public void name_0023() {
        int n = 6;
        long arr[] = {1, 2, 3, 4, 5, 6};
        rearrange(arr, n);
        System.out.println(Arrays.toString(arr));
    }
}


