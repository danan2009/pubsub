package top.sawied.hs.pubsub;

import com.google.common.util.concurrent.Uninterruptibles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.time.Duration;
import java.util.Date;

public class ServiceWorker implements Runnable{

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceWorker.class);

    private Boolean isRunning = true;

    @Override
    public void run() {
        LOGGER.info("DeamonService starting");
        int i = 0;
        while(isRunning && i <5 ){
            Uninterruptibles.sleepUninterruptibly(Duration.ofSeconds(2));
            LOGGER.info("run: {}" , DateFormat.getDateInstance().format(new Date()));
            i++;
        }
    }

    public synchronized void stop(){
        this.isRunning = false;
    }


}
