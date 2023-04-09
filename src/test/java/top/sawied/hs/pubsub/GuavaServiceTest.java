package top.sawied.hs.pubsub;


import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for Guava service test.
 */
public class GuavaServiceTest
{
   private static final Logger LOGGER = LoggerFactory.getLogger(GuavaServiceTest.class);

    @Test
    public void shouldBeingRunning()
    {
        LOGGER.info("heard 1");
        try {
            DeamonService.deamonServiceStarter();
            LOGGER.info("heard 2");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
