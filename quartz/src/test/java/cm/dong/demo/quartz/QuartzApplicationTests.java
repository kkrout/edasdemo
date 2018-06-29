package cm.dong.demo.quartz;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QuartzApplicationTests
{
    @Autowired
    private RestTemplate testRestTemplate;

    @Autowired
    private ObjectMapper jsonMapper;

    @Test
    public void contextLoads() throws JsonParseException, JsonMappingException, IOException
    {
        // http://172.16.80.175:8080//ECW-Mall-WXMP/mp/goods/800-000046.ihtml?token=6b0e90c9c2508c6901b643c8c2e1ecbb0ada4346ec30ac278b8d1c62636438b77013dbef8621ea54289cf3df6ce70deb
        // ResponseEntity<ArrayList> result =
        // testRestTemplate.getForEntity("http://localhost:8081/quartz/job/list/",
        // ArrayList.class);
        // HttpHeaders headers = new HttpHeaders();
        // headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        // HttpEntity<String> entity = new HttpEntity<String>("", headers);
        // ResponseEntity<String> exchange = testRestTemplate.exchange(
        // "http://172.16.80.175:8080//ECW-Mall-WXMP/mp/goods/800-000046.ihtml?token=6b0e90c9c2508c6901b643c8c2e1ecbb0ada4346ec30ac278b8d1c62636438b77013dbef8621ea54289cf3df6ce70deb",
        // HttpMethod.GET, entity, String.class);
        // String body = exchange.getBody();
        // Map readValue = jsonMapper.readValue(body, Map.class);
        // System.out.println(readValue.get("goods"));
        // ResponseEntity<String> result = testRestTemplate.getForEntity("https://www.baidu.com/",
        // String.class);
        // String body = result.getBody();
        // System.out.println(body);

        // Map<String, String> multiValueMap = new HashMap<>();
        // multiValueMap.put("jobClassName", "cm.dong.demo.quartz.job");// 传值，但要在url上配置相应的参数
        // multiValueMap.put("jobGroupName", "BaseJob");// 传值，但要在url上配置相应的参数
        // multiValueMap.put("cronExpression", "* * * * * ?");// 传值，但要在url上配置相应的参数
        // String res = testRestTemplate.getForObject("/quartz/job/addjob", String.class,
        // multiValueMap);
        // System.out.println(res);
        // try
        // {
        // Thread.sleep(30000);
        // }
        // catch (InterruptedException e)
        // {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }

}
