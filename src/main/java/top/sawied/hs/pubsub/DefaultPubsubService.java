package top.sawied.hs.pubsub;

import com.google.api.core.ApiFunction;
import com.google.api.core.ApiService;
import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DefaultPubsubService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PubsubApplication.class);

    public static String projectId ="GCP-projectId";

    public static String endpoint = "localhost:8085";

    public static String topic ="topic1";

    public static String subscriptionId ="subscription1";

    private Subscriber subscriber;

    private int pullCount = 2;

    Executor defaultExecutor=Executors.newFixedThreadPool(2);


    public void run(){
        defaultExecutor.execute(()->{
            while (true){
                try {
                    if(subscriber!=null){
                        LOGGER.info("service status is {}",subscriber.state() );
                        LOGGER.info("service try to restart after 10s is {}",subscriber.state() );
                        Uninterruptibles.sleepUninterruptibly(Duration.ofSeconds(10));
                    }
                    ProjectSubscriptionName projectSubscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId);

                    ManagedChannel channel = ManagedChannelBuilder.forTarget(DefaultPubsubService.endpoint).usePlaintext().build();
                    TransportChannelProvider channelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));

                    ApiFunction<ManagedChannelBuilder,ManagedChannelBuilder> channelConfig = new ApiFunction<ManagedChannelBuilder,ManagedChannelBuilder>() {
                        @Override
                        public ManagedChannelBuilder apply(ManagedChannelBuilder managedChannelBuilder) {
                            return ManagedChannelBuilder.forTarget(endpoint).usePlaintext().idleTimeout(1, TimeUnit.MINUTES).keepAliveWithoutCalls(false).keepAliveTime(1,TimeUnit.MINUTES);
                        }
                    };
                    InstantiatingGrpcChannelProvider instantiatingGrpcChannelProvider = InstantiatingGrpcChannelProvider.newBuilder().setChannelConfigurator(channelConfig).build();

                    subscriber = Subscriber.newBuilder(projectSubscriptionName, defaultMessageReceiver())
                            .setEndpoint(endpoint)
                            .setChannelProvider(instantiatingGrpcChannelProvider)
                            .setFlowControlSettings(FlowControlSettings.newBuilder().setMaxOutstandingElementCount(50L).build())
                            .setParallelPullCount(1)
                            .setCredentialsProvider(NoCredentialsProvider.create())
                            .build();
                    subscriber.addListener(defaultLister(), defaultExecutor);
                    subscriber.startAsync().awaitRunning();
                    LOGGER.error("service start async and  it is running.");
                    subscriber.awaitTerminated();
                    LOGGER.error("service Terminated.");
                }catch (Exception exception){
                    LOGGER.error("have something happened during running the service.");
                    subscriber.stopAsync();
                }finally {
                   if(subscriber!=null){
                       subscriber.stopAsync();
                   }
                }
            }

        });

    }

    private MessageReceiver defaultMessageReceiver(){
        return (message, consumer) -> {
            LOGGER.info("receive message Id : {} and message is {}",message.getMessageId() , message.getData().toStringUtf8());
            consumer.ack();
        };
    }

    private ApiService.Listener defaultLister(){
        return new ApiService.Listener() {
            @Override
            public void failed(ApiService.State from, Throwable failure) {
                LOGGER.error("service failed {}" ,failure.getMessage());
            }

            @Override
            public void running() {
                LOGGER.info("service is running.");
            }

            @Override
            public void starting() {
                LOGGER.info("service is starting...");
            }

            @Override
            public void stopping(ApiService.State from) {
                LOGGER.info("service is stopping...");
            }

            @Override
            public void terminated(ApiService.State from) {
                LOGGER.info("service is terminated.");
            }
        };
    }


}
