package top.sawied.hs.pubsub;


import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.*;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.DefaultApplicationArguments;
import top.sawied.hs.pubsub.service.DefaultPubsubService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.time.Duration;
import java.util.Date;

import static top.sawied.hs.pubsub.service.DefaultPubsubService.projectId;
import static top.sawied.hs.pubsub.service.DefaultPubsubService.subscriptionId;

public class PubsubTestCases {

    private static final Logger LOGGER = LoggerFactory.getLogger(PubsubApplication.class);

    private CredentialsProvider credentialsProvider = null;
    private ManagedChannel channel;
    private TransportChannelProvider channelProvider;
    private TopicAdminClient topicAdmin;

    private Publisher publisher;
    private SubscriberStub subscriberStub;
    private SubscriptionAdminClient subscriptionAdminClient;

    private TopicName topicName = TopicName.of(projectId, DefaultPubsubService.topic);
    private SubscriptionName subscriptionName = SubscriptionName.of(projectId, subscriptionId);
    private Subscription subscription;


    @BeforeEach
    public void setup() throws IOException {
        channel = ManagedChannelBuilder.forTarget(DefaultPubsubService.endpoint).usePlaintext().build();
        channelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));

        CredentialsProvider credentialsProvider = NoCredentialsProvider.create();

        topicAdmin = createTopicAdmin(credentialsProvider);
        try{topicAdmin.createTopic(topicName);}catch(Exception e){
            LOGGER.error("create Topic failed. {}", e.getMessage());
        };
        publisher = createPublisher(credentialsProvider);

        subscriptionAdminClient = createSubscriptionAdmin(credentialsProvider);
        try {
            subscription = subscriptionAdminClient.createSubscription(subscriptionName, topicName, PushConfig.getDefaultInstance(), 0);
        }catch (Exception e){
            LOGGER.error("create subscription failed. {}", e.getMessage());
        }
    }

    @AfterEach
    public void tearDown() throws Exception {
        subscriptionAdminClient.deleteSubscription(subscription.getName());
        topicAdmin.deleteTopic(topicName);
        channel.shutdownNow();
    }


    @Test
    public void pushMessage() throws Exception {

        new DefaultPubsubService().run(new DefaultApplicationArguments());
        LOGGER.info("pubsub running . trying push message ..... ");

        for (int i =0 ;i <10000; i++){
            String messageText = "text ,date : " + DateFormat.getTimeInstance().format(new Date());
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                    .setData(ByteString.copyFrom(messageText, Charset.defaultCharset()))
                    .build();
            publisher.publish(pubsubMessage);
            LOGGER.info("publish message {}", new String(messageText.getBytes()));
            Uninterruptibles.sleepUninterruptibly(Duration.ofSeconds(3));
        }

        Uninterruptibles.sleepUninterruptibly(Duration.ofSeconds(10000));
    }



    private TopicAdminClient createTopicAdmin(CredentialsProvider credentialsProvider) throws IOException {
        return TopicAdminClient.create(
                TopicAdminSettings.newBuilder()
                        .setTransportChannelProvider(channelProvider)
                        .setCredentialsProvider(credentialsProvider)
                        .build()
        );
    }

    private SubscriptionAdminClient createSubscriptionAdmin(CredentialsProvider credentialsProvider) throws IOException {
        SubscriptionAdminSettings subscriptionAdminSettings = SubscriptionAdminSettings.newBuilder()
                .setCredentialsProvider(credentialsProvider)
                .setTransportChannelProvider(channelProvider)
                .build();
        return SubscriptionAdminClient.create(subscriptionAdminSettings);
    }

    private Publisher createPublisher(CredentialsProvider credentialsProvider) throws IOException {
        return Publisher.newBuilder(topicName)
                .setChannelProvider(channelProvider)
                .setCredentialsProvider(credentialsProvider)
                .build();
    }



}
