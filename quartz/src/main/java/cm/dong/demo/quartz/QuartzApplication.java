package cm.dong.demo.quartz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class QuartzApplication
{

    public static void main(String[] args)
    {
        SpringApplication spring = new SpringApplication(QuartzApplication.class);
        spring.run(args);
    }

    @Bean
    public ObjectMapper jsonMapper()
    {
        return new ObjectMapper();
    }

    @Bean
    public RestTemplate restTemplate()
    {
        return new RestTemplate();
    }

}
