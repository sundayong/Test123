import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestCase {
    public static void main(String[] args) {
        String strip = StringUtils.strip("[]123213[]8789dashk^^*hdsa{}[]", "[]");
        System.out.println(strip);

        ExecutorService threadPoolExecutor = Executors.newSingleThreadExecutor();

        CompletableFuture.supplyAsync(() -> null, threadPoolExecutor);

        threadPoolExecutor.submit(()->{});
    }

    @Test
    public void dummy() {
        String line = "64 bytes from 127.0.0.1: icmp_seq=0 ttl=64 time=0.039 ms";
        Pattern pattern = Pattern.compile("(\\s+)(ttl=\\d+)",    Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);
        System.out.println(matcher.find());
    }

}
