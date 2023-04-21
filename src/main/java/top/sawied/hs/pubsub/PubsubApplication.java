package top.sawied.hs.pubsub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import top.sawied.hs.pubsub.service.DefaultPubsubService;
import top.sawied.hs.pubsub.service.PubsubAdmin;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class PubsubApplication
{

    private static final Logger LOGGER = LoggerFactory.getLogger(PubsubApplication.class);

    public static void main( String[] args )
    {
        SpringApplication.run(PubsubApplication.class, args);
    }

    @Bean
    public PubsubAdmin pubsubAdmin(){
        return new PubsubAdmin();
    }

    @Bean
    public ApplicationRunner defaultPubsubService(){
        return new DefaultPubsubService();
    }
}
