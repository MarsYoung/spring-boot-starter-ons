package com.marsyoung.factory;

import com.marsyoung.config.OnsProperty;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

public class OnsBeanFactory implements BeanFactoryAware {

    OnsProperty onsProperty;



    public OnsBeanFactory(OnsProperty onsProperty) {
        this.onsProperty=onsProperty;
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

    }
}
