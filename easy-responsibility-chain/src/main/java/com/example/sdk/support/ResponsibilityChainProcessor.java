package com.example.sdk.support;

import com.example.sdk.chain.BaseResponsibilityChainHandler;
import com.example.sdk.chain.Processor;
import com.example.sdk.chain.ResponsibilityChainException;
import com.example.sdk.chain.ResponsibilityChainHandlerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 负责生产责任链中的节点
 *
 * @author shuguang.zhang
 * @date 2021-01-15
 */
@Slf4j
public class ResponsibilityChainProcessor<TContext extends Processor> {

    /**
     * 处理链
     */
    private Map<String, List<BaseResponsibilityChainHandler<TContext>>> multiChains = new HashMap<>();

    /**
     * 添加处理节点到链路中，会根据返回的值决定是否继续执行链
     *
     * @param hitPredicate {@hitPredicate Predicate<TContext>} 提供给外部注册的判断是否命中当前责任链节点的逻辑块
     * @param handleAction {@handleAction Predicate<TContext>} 提供给外部注册的责任链上下文处理的逻辑块, 返回值为true时，将会继续执行下一个节点
     */
    protected void addChain(String name, Predicate<TContext> hitPredicate,
                         Predicate<TContext> handleAction, int order, String groupName) {

        verification(hitPredicate, handleAction);
        BaseResponsibilityChainHandler<TContext> chain;
        chain = new BaseResponsibilityChainHandler<TContext>(name, hitPredicate, handleAction, null);
        setNodeAndSort(order, groupName, chain);
    }

    /**
     * 添加处理节点到链路中，用户无法干预链表的执行，会一直执行结束
     *
     * @param hitPredicate 提供给外部注册的判断是否命中当前责任链节点的逻辑块
     * @param handleAction 提供给外部注册的责任链上下文处理的逻辑块,
     */
    protected void addChainFromConsumer(String name, Predicate<TContext> hitPredicate,
                                     Consumer<TContext> handleAction, int order, String groupName) {

        verification(hitPredicate, handleAction);
        BaseResponsibilityChainHandler<TContext> chain;
        chain = ResponsibilityChainHandlerFactory.generate(name, hitPredicate, (context) -> {
            handleAction.accept(context);
            return true;
        }, null);

        setNodeAndSort(order, groupName, chain);
    }

    /**
     * 往组中放入节点并且排序
     *
     * @param order     排序
     * @param groupName 组名
     * @param chain     链中的节点
     */
    private void setNodeAndSort(int order, String groupName, BaseResponsibilityChainHandler<TContext> chain) {

        List<BaseResponsibilityChainHandler<TContext>> chains = multiChains.get(groupName);
        if (CollectionUtils.isEmpty(chains)) {
            chains = new ArrayList<>();
            multiChains.put(groupName, chains);
        }

        chain.setSort(order);
        if (!chains.isEmpty()) {
            // 存在相同的顺序的接口直接返回
            chains.forEach(a -> {
                if (a.getSort() == order) {
                    throw new ResponsibilityChainException(String.format("节点:[%s]与节点:[%s] order:[%s]相同", a.getName(), chain.getName(), order));
                }
            });

            chains.get(chains.size() - 1).setNext(chain);
        }

        chains.add(chain);

        // 排序
        sort(chains);
    }

    /**
     * 转换器
     *
     * @param source 源节点
     * @param target 目标节点
     */
    private static void covert(BaseResponsibilityChainHandler source, BaseResponsibilityChainHandler target) {
        target.setSort(source.getSort());
        target.setHandleAction(source.getHandleAction());
        target.setHitPredicate(source.getHitPredicate());
        target.setId(source.getId());
        target.setName(source.getName());
    }

    /**
     * 链表排序
     *
     * @param chains 执行链
     */
    private void sort(List<BaseResponsibilityChainHandler<TContext>> chains) {
        BaseResponsibilityChainHandler temp, p;
        for (int i = 0; i < chains.size(); i++) {
            p = chains.get(0);
            for (int j = 0; j < chains.size() - i; j++) {
                if (p.getNext() != null && p.getSort() > p.getNext().getSort()) {
                    temp = new BaseResponsibilityChainHandler(p.getName(), p.getHitPredicate(), p.getHandleAction(), p.getNext());
                    temp.setSort(p.getSort());
                    covert(p.getNext(), p);
                    covert(temp, p.getNext());
                }
                p = p.getNext();
            }
        }
    }

    /**
     * 责任链上下文处理
     *
     * @param context {@link TContext} 当前处理的上下文信息
     */
    public void handle(TContext context, String groupName) {

        List<BaseResponsibilityChainHandler<TContext>> chains = multiChains.get(groupName);
        if (chains.isEmpty()) {
            return;
        }

        context.clearLog();
        chains.get(0).handle(context);
    }

    /**
     * 校验参数
     */
    private <THandleAction> void verification(Predicate<TContext> hitPredicate,
                                              THandleAction handleAction) {
        if (hitPredicate == null) {
            throw new IllegalArgumentException("hitPredicate");
        }

        if (handleAction == null) {
            throw new IllegalArgumentException("handleAction");
        }
    }

}
