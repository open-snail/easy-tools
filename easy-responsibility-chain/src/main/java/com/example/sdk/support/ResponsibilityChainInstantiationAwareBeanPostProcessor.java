package com.example.sdk.support;

import com.example.sdk.annotation.ResponsibilityChainMethod;
import com.example.sdk.chain.BaseContext;
import com.example.sdk.chain.ResponsibilityChainException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * @author zhangshuguang
 * @date 2020/04/07
 */
@Slf4j
public class ResponsibilityChainInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

    @Autowired
    private ResponsibilityChainProcessor<BaseContext> processor;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Method[] declaredMethods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        for (Method declaredMethod : declaredMethods) {
            ResponsibilityChainMethod chainMethod = AnnotationUtils.findAnnotation(declaredMethod, ResponsibilityChainMethod.class);

            if (chainMethod != null) {

                if (StringUtils.isBlank(chainMethod.groupName())) {
                    throw new ResponsibilityChainException("责任链groupName不能为空");
                }

                Parameter[] parameters = declaredMethod.getParameters();

                if (parameters.length != 1) {
                    throw new IllegalArgumentException("参数数量不能大于1" + chainMethod.desc());
                }

                if (chainMethod.isReturn()) {
                    Type returnType = declaredMethod.getGenericReturnType();
                    if (!"boolean".equals(returnType.getTypeName())) {
                        throw new IllegalArgumentException(String.format("%s方法的isReturn=true但是实际的返回值类型%s",declaredMethod.getName(), returnType.getTypeName()));
                    }
                }

                for (Parameter parameter : parameters) {
                    if (!parameter.getType().getSuperclass().getName().equals(BaseContext.class.getName())
                            && !parameter.getType().getName().equals(BaseContext.class.getName())) {
                        throw new IllegalArgumentException("参数只能为BaseContext desc:" + chainMethod.desc() + "className:" + bean.getClass().getName());
                    }
                }

                if (chainMethod.isReturn()) {
                    processor.addChain(declaredMethod.getName(), BaseContext::isExecuteNextNode, context -> {
                        ReflectionUtils.makeAccessible(declaredMethod);

                        if (chainMethod.isPrintTime()) {
                            context.getStopWatch().start(String.format("groupName:%s name:%s",chainMethod.groupName(),declaredMethod.getName()));
                        }
                        Object result = ReflectionUtils.invokeMethod(declaredMethod, bean, context);

                        if (chainMethod.isPrintTime()) {
                            context.getStopWatch().stop();
                        }

                        log.info("当前执行的带有返回值的 {} groupName = {} result= {} order= {} desc= {}", chainMethod.isReturn(), chainMethod.groupName(), result, chainMethod.order(), chainMethod.desc());
                        if (result != null) {
                            return (boolean) result;
                        } else {
                            return true;
                        }

                    }, chainMethod.order(), chainMethod.groupName());
                } else {
                    processor.addChainFromConsumer(declaredMethod.getName(), BaseContext::isExecuteNextNode, context -> {
                        ReflectionUtils.makeAccessible(declaredMethod);
                        log.info("当前执行的没有返回值的 {}  groupName = {}  order= {}  desc= {}", chainMethod.isReturn(), chainMethod.groupName(), chainMethod.order(), chainMethod.desc());

                        if (chainMethod.isPrintTime()) {
                            context.getStopWatch().start(String.format("groupName:%s name:%s",chainMethod.groupName(),declaredMethod.getName()));
                        }
                        ReflectionUtils.invokeMethod(declaredMethod, bean, context);
                        if (chainMethod.isPrintTime()) {
                            context.getStopWatch().stop();
                        }

                    }, chainMethod.order(), chainMethod.groupName());
                }
            }
        }

        return bean;
    }

}
