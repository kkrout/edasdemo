package cm.dong.demo.quartz.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import cm.dong.demo.quartz.SpringUtils;

public class HelloJob implements BaseJob
{

    // private static Logger _log = LoggerFactory.getLogger(HelloJob.class);

    public HelloJob()
    {

    }

    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        RestTemplate restTemplate = SpringUtils.getBean(RestTemplate.class);

        ResponseEntity<String> result = restTemplate.getForEntity("https://www.baidu.com/", String.class);
        String body = result.getBody();
        System.out.println(body);
    }
}
