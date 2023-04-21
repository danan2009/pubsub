package top.sawied.hs.pubsub.api;


import com.google.api.gax.paging.AbstractPagedListResponse;
import com.google.common.collect.Maps;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.SubscriptionName;
import com.google.pubsub.v1.Topic;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import top.sawied.hs.pubsub.api.model.MessageDto;
import top.sawied.hs.pubsub.service.PubsubAdmin;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PubsubMessageController {

    @Autowired
    private PubsubAdmin pubsubAdmin;

    @RequestMapping(value = "/pubsub",method= RequestMethod.POST)
    public MessageDto pushMessage(@Valid @RequestBody MessageDto messageDto){
        pubsubAdmin.pushMessage(messageDto.getMessage());
        return messageDto;
    }

    @RequestMapping(value = "/pubsub",method= RequestMethod.GET)
    public Map<String, Object> getinfo(){
       return pubsubAdmin.getInfo();
    }

    @RequestMapping(value = "/pubsub/topic",method= RequestMethod.POST)
    public Map<String, Object> createTopic(){
        HashMap<String, Object> map = Maps.newHashMap();
        Topic createdTopic = pubsubAdmin.createTopic();
        map.put("topic",createdTopic.getName());
        return map;
    }

    @RequestMapping(value = "/pubsub/subscription",method= RequestMethod.POST)
    public Map<String, Object> createSubscription(){
        HashMap<String, Object> map = Maps.newHashMap();
        Subscription subscription = pubsubAdmin.createSubscriptionId();
        map.put("subscription",subscription.getName());
        return map;
    }

    @RequestMapping(value = "/pubsub/topic",method= RequestMethod.DELETE)
    public Map<String, Object> deleteSubscription(){
        HashMap<String, Object> map = Maps.newHashMap();
        SubscriptionName subscriptionName = pubsubAdmin.deleteSubscriptionId();
        map.put("subscription",subscriptionName.getSubscription());
        return map;
    }

}
