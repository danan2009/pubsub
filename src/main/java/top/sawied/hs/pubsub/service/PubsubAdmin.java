package top.sawied.hs.pubsub.service;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.paging.AbstractPagedListResponse;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.*;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static top.sawied.hs.pubsub.service.DefaultPubsubService.*;

public class PubsubAdmin {

    private static final Logger LOGGER = LoggerFactory.getLogger(PubsubAdmin.class);

    private CredentialsProvider credentialsProvider = NoCredentialsProvider.create();
    private ManagedChannel channel = ManagedChannelBuilder.forTarget(DefaultPubsubService.endpoint).usePlaintext().build();
    private TransportChannelProvider channelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));

    private TopicAdminClient topicAdmin;
    private Publisher publisher;
    private SubscriptionAdminClient subscriptionAdminClient;

    private TopicName topicName = TopicName.of(projectId, DefaultPubsubService.topic);
    private SubscriptionName subscriptionName = SubscriptionName.of(projectId, subscriptionId);


    public Topic createTopic() {
        Topic createdTopic = null;
        try {
            if (this.topicAdmin == null) {
                this.topicAdmin = createTopicAdmin();
            }
            createdTopic = this.topicAdmin.createTopic(topicName);
        } catch (IOException e) {
            LOGGER.error("create topic {} failed.",topicName);
            throw new RuntimeException(e);
        }
        return createdTopic;
    }

    public String deleteTopic() {
        try {
            if (this.topicAdmin == null) {
                this.topicAdmin = createTopicAdmin();
            }
           this.topicAdmin.deleteTopic(topicName);
        } catch (IOException e) {
            LOGGER.error("create topic {} failed.",topicName);
            throw new RuntimeException(e);
        }
        return DefaultPubsubService.topic;
    }

    public Subscription createSubscriptionId(){
        Subscription createdSubscriptionId = null;
        try{
            if(subscriptionAdminClient == null){
                this.subscriptionAdminClient=createSubscriptionAdmin();
            }
            createdSubscriptionId = this.subscriptionAdminClient.createSubscription(subscriptionName, topicName, PushConfig.getDefaultInstance(), 0);
        } catch (IOException e) {
            LOGGER.error("create subscription {} failed.",topicName);
            throw new RuntimeException(e);
        }
        return createdSubscriptionId;
    }

    public SubscriptionName deleteSubscriptionId(){
        try{
            if(subscriptionAdminClient == null){
                this.subscriptionAdminClient=createSubscriptionAdmin();
            }
           this.subscriptionAdminClient.deleteSubscription(subscriptionName);
        } catch (IOException e) {
            LOGGER.error("delete subscription {} failed.",subscriptionName);
            throw new RuntimeException(e);
        }
        return subscriptionName;
    }

    public Map<String, Object> getInfo(){
        Map<String, Object> pubsubInfo = new HashMap<String, Object>();
        try{
            if (this.topicAdmin == null) {
                this.topicAdmin = createTopicAdmin();
            }
            if(subscriptionAdminClient == null){
                this.subscriptionAdminClient=createSubscriptionAdmin();
            }

            Topic exsitingTopic = this.topicAdmin.getTopic(topicName);
            pubsubInfo.put("topics",exsitingTopic.getName());

            Subscription subscription = this.subscriptionAdminClient.getSubscription(subscriptionName);
            pubsubInfo.put("subscriptions",subscription.getName());

        } catch (IOException e) {
            LOGGER.error("delete subscription {} failed.",subscriptionName);
            throw new RuntimeException(e);
        }
        return pubsubInfo;
    }

    public void pushMessage(String message){
        try{
            if(publisher == null){
                this.publisher = createPublisher();
            }

            PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                    .setData(ByteString.copyFrom(message, Charset.defaultCharset()))
                    .build();
            this.publisher.publish(pubsubMessage).get();
            LOGGER.info("published with id {} message {} success.",pubsubMessage.getMessageId(),message);
        } catch (IOException e) {
            LOGGER.error("publish message {} failed.",e);
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            LOGGER.error("publish message {} failed.",e);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            LOGGER.error("publish message {} failed.",e);
            throw new RuntimeException(e);
        }
    }



    private TopicAdminClient createTopicAdmin() throws IOException {
        return TopicAdminClient.create(
                TopicAdminSettings.newBuilder()
                        .setTransportChannelProvider(channelProvider)
                        .setCredentialsProvider(credentialsProvider)
                        .build()
        );
    }

    private SubscriptionAdminClient createSubscriptionAdmin() throws IOException {
        SubscriptionAdminSettings subscriptionAdminSettings = SubscriptionAdminSettings.newBuilder()
                .setCredentialsProvider(credentialsProvider)
                .setTransportChannelProvider(channelProvider)
                .build();
        return SubscriptionAdminClient.create(subscriptionAdminSettings);
    }

    private Publisher createPublisher() throws IOException {
        return Publisher.newBuilder(topicName)
                .setChannelProvider(channelProvider)
                .setCredentialsProvider(credentialsProvider)
                .build();
    }

}
