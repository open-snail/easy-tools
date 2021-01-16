package com.example.sdk.chain;

import java.util.function.Predicate;

/**
 * 负责生产责任链中的节点
 *
 * @author shuguang.zhang
 * @date 2021-01-15
 */
public class ResponsibilityChainHandlerFactory {

    /**
     * 创建责任链处理节点
     *
     * @param name          名称
     * @param hitPredicate  提供给外部注册的判断是否命中当前责任链节点的逻辑块
     * @param handleAction  提供给外部注册的责任链上下文处理的逻辑块
     */
    public static <TContext extends Processor> BaseResponsibilityChainHandler<TContext> generate(
            String name, Predicate<TContext> hitPredicate, Predicate<TContext> handleAction, BaseResponsibilityChainHandler<TContext> next) {
        return new BaseResponsibilityChainHandler<>(name, hitPredicate, handleAction, next);
    }
}
