package com.example.sdk.conf;

import com.example.sdk.chain.BaseContext;
import com.example.sdk.support.ResponsibilityChainProcessor;
import com.example.sdk.support.ResponsibilityChainInstantiationAwareBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangshuguang
 * @date 2020/01/08
 */
@Configuration
public class ResponsibilityChainAutoConfiguration {

    @Bean
    public ResponsibilityChainProcessor responsibilityChainProcessor() {
        return new ResponsibilityChainProcessor<BaseContext>();
    }

    @Bean
    public ResponsibilityChainInstantiationAwareBeanPostProcessor responsibilityChainInstantiationAwareBeanPostProcessor() {
        return new ResponsibilityChainInstantiationAwareBeanPostProcessor();
    }

}
