package com.marsyoung.factory;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.marsyoung.OnsConstant;
import com.marsyoung.annotation.OnsConsumer;
import com.marsyoung.annotation.OnsProducer;
import com.marsyoung.config.OnsProperty;
import com.marsyoung.exception.OnsStarterException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Properties;


@Configuration
@EnableConfigurationProperties(OnsProperty.class)
@Slf4j
public class OnsConfiguration implements ApplicationContextAware, BeanPostProcessor {

    @Autowired
    private OnsProperty onsProperty;

    ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        Class clazz = bean.getClass();
        if (AopUtils.isAopProxy(bean)) {
            clazz = AopUtils.getTargetClass(bean);
        }
        do {
            for (Field field : clazz.getDeclaredFields()) {
                try {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    OnsConsumer consumer = field.getAnnotation(OnsConsumer.class);
                    if (consumer != null) {
                        Object value = getOrCreateConsumerBean(consumer, field.getType());
                        if (value != null) {
                            field.set(bean, value);
                        }
                    }
                    OnsProducer producer = field.getAnnotation(OnsProducer.class);
                    if (producer != null) {
                        Object value = getOrCreateProducerBean(producer, field.getType());
                        if (value != null) {
                            field.set(bean, value);
                        }
                    }
                } catch (Exception e) {
                    throw new BeanInitializationException("Failed to init remote service reference at filed " + field.getName() + " in class " + bean.getClass().getName(), e);
                }
            }
            for (Method method : clazz.getDeclaredMethods()) {
                String name = method.getName();
                if (name.length() > 3 && name.startsWith("set")
                        && method.getParameterTypes().length == 1
                        && Modifier.isPublic(method.getModifiers())
                        && !Modifier.isStatic(method.getModifiers())) {
                    try {
                        OnsConsumer consumer = method.getAnnotation(OnsConsumer.class);
                        if (consumer != null) {
                            Object value = getOrCreateConsumerBean(consumer, method.getParameterTypes()[0]);
                            if (value != null) {
                                method.invoke(bean, new Object[]{value});
                            }
                        }
                        OnsProducer producer = method.getAnnotation(OnsProducer.class);
                        if (producer != null) {
                            Object value = getOrCreateProducerBean(producer, method.getParameterTypes()[0]);
                            if (value != null) {
                                method.invoke(bean, new Object[]{value});
                            }
                        }
                    } catch (Exception e) {
                        throw new BeanInitializationException("Failed to init remote service reference at method " + name + " in class " + bean.getClass().getName(), e);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);
        return bean;
    }

    private Object getOrCreateProducerBean(OnsProducer producer, Class<?> parameterType) {
        //去Springfactory中寻找有木有这个bean，没有的话创建一个，并放入Spring的factoryBean
        String producerId = producer.produceId();
        if (StringUtils.isNotBlank(producerId)) {
            Object exist = null;
            try {
                exist = applicationContext.getBean(producerId, parameterType);
            } catch (BeansException e) {
                return createProducerBean(producer, producerId);
            }
            if (exist != null) {
                return exist;
            } else {
                return createProducerBean(producer, producerId);
            }
        } else {
            log.error("{} need consumerId " , JSON.toJSONString(producer));
            return null;
        }
    }

    private ProducerBean createProducerBean(OnsProducer producer, String producerId) {
        ProducerBean producerBean = new ProducerBean();
        Properties properties = buildBaseProperties(onsProperty);
        //匹配配置文件中是否存在consumer，否这报错
        OnsProperty.Producer p = onsProperty.getProducers().stream().filter(
                x -> StringUtils.equals(x.getProducerId(), producerId)
        ).findFirst().orElseThrow(OnsStarterException::new);
        properties.put(PropertyKeyConst.ProducerId, p.getProducerId());
        //topic 和tag用于receive message的时候
        properties.put(OnsConstant.TOPIC, producer.topic());
        properties.put(OnsConstant.TAG, producer.tag());
        producerBean.setProperties(properties);
        producerBean.start();
        return producerBean;
    }

    private Object getOrCreateConsumerBean(OnsConsumer consumer, Class<?> parameterType) {
        //去Springfactory中寻找有木有这个bean，没有的话创建一个，并放入Spring的factoryBean
        String consumerId = consumer.consumerId();
        if (StringUtils.isNotBlank(consumerId)) {
            Object existConsumer = null;
            try {
                existConsumer = applicationContext.getBean(consumerId, parameterType);
            } catch (BeansException e) {
                return createConsumerBean(consumer, consumerId);
            }
            if (existConsumer != null) {
                return existConsumer;
            } else {
                return createConsumerBean(consumer, consumerId);
            }
        } else {
            log.error("{} need consumerId " , JSON.toJSONString(consumer));
            return null;
        }
    }

    private ConsumerBean createConsumerBean(OnsConsumer consumer, String consumerId) {
        ConsumerBean consumerBean = new ConsumerBean();
        Properties properties = buildBaseProperties(onsProperty);
        //匹配配置文件中是否存在consumer，否这报错
        OnsProperty.Consumer c = onsProperty.getConsumers().stream().filter(
                x -> StringUtils.equals(x.getConsumerId(), consumerId)
        ).findFirst().orElseThrow(OnsStarterException::new);
        properties.put(PropertyKeyConst.ConsumerId, c.getConsumerId());


        //topic 和tag用于createlistener的时候
        properties.put(OnsConstant.TOPIC, consumer.topic());
        properties.put(OnsConstant.TAG, consumer.tag());
        consumerBean.setProperties(properties);
        return consumerBean;
    }

    private Properties buildBaseProperties(OnsProperty onsProperty) {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.AccessKey, onsProperty.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, onsProperty.getSecretKey());
        properties.put(PropertyKeyConst.ONSAddr, onsProperty.getOnsAddress());
        return properties;
    }
}
