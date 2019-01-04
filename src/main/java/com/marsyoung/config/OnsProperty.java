package com.marsyoung.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

import static com.aliyun.openservices.ons.api.PropertyValueConst.CLUSTERING;
import static com.aliyun.openservices.ons.api.impl.rocketmq.ONSChannel.ALIYUN;

@ConfigurationProperties(prefix = "ons")
public class OnsProperty {

    String accessKey;
    String secretKey;
    String onsChannel;
    String onsAddress = ALIYUN.name();
    String nameServerAddress;
    List<Consumer> consumers = new ArrayList<>();
    List<Producer> producers = new ArrayList<>();

    public static class Consumer{
        /*
        * 设置 Consumer 实例的消费模式，集群消费：CLUSTERING，广播消费：BROADCASTING。
        * */
        String messageModel = CLUSTERING;
        String consumerId;
        String topic;
        String threadNum = "64";//default 64
        /*
        * 设置消息消费失败的最大重试次数。
        * */
        String maxReconsumeTimes;
        /*设置每条消息消费的最大超时时间，超过设置时间则被视为消费失败，
        等下次重新投递再次消费。每个业务需要设置一个合理的值，单位（分钟）。*/
        String consumeTimeout = "15";//15
        /*
        * BatchConsumer每次批量消费的最大消息数量，默认值为1，允许自定义范围为[1, 32]，
        * 实际消费数量可能小于该值。
        * */
        String consumeMessageBatchMaxSize;
        /*
        * 设置事务消息第一次回查的最快时间，单位（秒）。
        * */
        String checkImmunityTimeInSeconds;
        /*
        * 只适用于顺序消息，设置消息消费失败的重试间隔时间，单位（毫秒）。
        * */
        String suspendTimeMillis;

        public String getMessageModel() {
            return messageModel;
        }

        public void setMessageModel(String messageModel) {
            this.messageModel = messageModel;
        }

        public String getConsumerId() {
            return consumerId;
        }

        public void setConsumerId(String consumerId) {
            this.consumerId = consumerId;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getThreadNum() {
            return threadNum;
        }

        public void setThreadNum(String threadNum) {
            this.threadNum = threadNum;
        }

        public String getMaxReconsumeTimes() {
            return maxReconsumeTimes;
        }

        public void setMaxReconsumeTimes(String maxReconsumeTimes) {
            this.maxReconsumeTimes = maxReconsumeTimes;
        }

        public String getConsumeTimeout() {
            return consumeTimeout;
        }

        public void setConsumeTimeout(String consumeTimeout) {
            this.consumeTimeout = consumeTimeout;
        }

        public String getConsumeMessageBatchMaxSize() {
            return consumeMessageBatchMaxSize;
        }

        public void setConsumeMessageBatchMaxSize(String consumeMessageBatchMaxSize) {
            this.consumeMessageBatchMaxSize = consumeMessageBatchMaxSize;
        }

        public String getCheckImmunityTimeInSeconds() {
            return checkImmunityTimeInSeconds;
        }

        public void setCheckImmunityTimeInSeconds(String checkImmunityTimeInSeconds) {
            this.checkImmunityTimeInSeconds = checkImmunityTimeInSeconds;
        }

        public String getSuspendTimeMillis() {
            return suspendTimeMillis;
        }

        public void setSuspendTimeMillis(String suspendTimeMillis) {
            this.suspendTimeMillis = suspendTimeMillis;
        }
    }

    public static class Producer{

        String topic;

        String producerId;

        String sendMsgTimeoutMillis;

        String instanceName;

        String maxMsgSize;

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getProducerId() {
            return producerId;
        }

        public void setProducerId(String producerId) {
            this.producerId = producerId;
        }

        public String getSendMsgTimeoutMillis() {
            return sendMsgTimeoutMillis;
        }

        public void setSendMsgTimeoutMillis(String sendMsgTimeoutMillis) {
            this.sendMsgTimeoutMillis = sendMsgTimeoutMillis;
        }

        public String getInstanceName() {
            return instanceName;
        }

        public void setInstanceName(String instanceName) {
            this.instanceName = instanceName;
        }

        public String getMaxMsgSize() {
            return maxMsgSize;
        }

        public void setMaxMsgSize(String maxMsgSize) {
            this.maxMsgSize = maxMsgSize;
        }
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getOnsChannel() {
        return onsChannel;
    }

    public void setOnsChannel(String onsChannel) {
        this.onsChannel = onsChannel;
    }

    public String getOnsAddress() {
        return onsAddress;
    }

    public void setOnsAddress(String onsAddress) {
        this.onsAddress = onsAddress;
    }

    public String getNameServerAddress() {
        return nameServerAddress;
    }

    public void setNameServerAddress(String nameServerAddress) {
        this.nameServerAddress = nameServerAddress;
    }

    public List<Consumer> getConsumers() {
        return consumers;
    }

    public void setConsumers(List<Consumer> consumers) {
        this.consumers = consumers;
    }

    public List<Producer> getProducers() {
        return producers;
    }

    public void setProducers(List<Producer> producers) {
        this.producers = producers;
    }
}
