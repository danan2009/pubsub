package top.sawied.hs.pubsub.service;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.*;

/**
 *
 */
public class DeamonService extends AbstractExecutionThreadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeamonService.class);

    @Override
    protected void run() throws Exception {
        LOGGER.info("DeamonService starting");
        int i = 0;
        while(i <5 ){
            Uninterruptibles.sleepUninterruptibly(Duration.ofSeconds(2));
            LOGGER.info("run: {}" , DateFormat.getDateInstance().format(new Date()));
            i++;
        }
    }

    class DeamernServiceLister extends Listener {
        @Override
        public void starting() {
            LOGGER.info("demon service starting....");
        }

        @Override
        public void terminated(State from) {
            LOGGER.warn("demon service terminated....");
        }

        @Override
        public void failed(State from, Throwable failure) {
            LOGGER.error("demon service failed ....");
        }

        @Override
        public void stopping(State from) {
            LOGGER.error("demon service stopping ....");
        }
    }

    public static void deamonServiceStarter() {

        ExecutorService  fxiedThreadPool = Executors.newFixedThreadPool(2);
        while (true) {
            ServiceManager serviceManager = null;
            try {

                DeamonService deamonService = new DeamonService();
                deamonService.addListener(deamonService.new DeamernServiceLister(), fxiedThreadPool);
                ArrayList<DeamonService> guavaServices = Lists.newArrayList(deamonService);
                serviceManager = new ServiceManager(guavaServices);
                serviceManager.startAsync().awaitHealthy();
                LOGGER.info("ServiceManager have started and healthy");
                serviceManager.awaitStopped();
            } catch (Exception e) {
                LOGGER.error("some error happened during running.");
            } finally {
                if (serviceManager != null && !serviceManager.isHealthy()) {
                    try {
                        serviceManager.stopAsync().awaitStopped(10, TimeUnit.SECONDS);
                        LOGGER.warn("serviceManager is going to stop.");
                    } catch (TimeoutException e) {
                        LOGGER.error("service manager stop timeout.");
                        throw new RuntimeException(e);
                    }
                }
                if(fxiedThreadPool !=null && fxiedThreadPool.isTerminated()){
                    fxiedThreadPool.shutdown();
                    try {
                        fxiedThreadPool.awaitTermination(10,TimeUnit.SECONDS);
                        LOGGER.error("fxiedThreadPool stop timeout.");
                    } catch (InterruptedException e) {
                        LOGGER.error("fxiedThreadPool stop timeout.");
                        throw new RuntimeException(e);
                    }
                    LOGGER.error("fxiedThreadPool stopped.");
                }
            }
        }
    }


}
