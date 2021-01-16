package com.example.sdk.chain;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.util.UUID;
import java.util.function.Predicate;

/**
 * 链的处理节点处理器
 *
 * @author shuguang.zhang
 * @date 2021-01-15
 */
@Data
@Slf4j
public class BaseResponsibilityChainHandler<TContext extends Processor> {

    public BaseResponsibilityChainHandler(String name, Predicate<TContext> hitPredicate,
                                          Predicate<TContext> handleAction, BaseResponsibilityChainHandler<TContext> next) {
        this.name = name;
        this.hitPredicate = hitPredicate;
        this.handleAction = handleAction;
        this.next = next;
    }

    /**
     * 节点ID
     */
    private UUID id = UUID.randomUUID();

    /**
     * 节点的名称
     */
    private String name;

    /**
     * 提供给外部注册的判断是否命中当前责任链节点的逻辑块
     */
    private Predicate<TContext> hitPredicate;

    /**
     * 提供给外部注册的责任链上下文处理的逻辑块, 返回值为true时，将会继续执行下一个节点
     */
    private Predicate<TContext> handleAction;

    /**
     * 下一个处理节点
     */
    private BaseResponsibilityChainHandler<TContext> next;

    /**
     * 节点排序
     */
    private int sort;

    /**
     * 责任链上下文处理
     *
     * @param context {@context TContext} 当前处理的上下文信息
     */
    public boolean handle(TContext context) {
        try {

            if (!hit(context)) {
                if (next != null) {
                    // 如果当前节点不执行，则重置isCurrentExecuteNode；否则下面的节点都不会执行
                    context.setExecuteNextNode(true);
                    log.info("未命中节点:{} 执行下一个节点:{}.",  name, next.name);
                    return next.handle(context);
                }
                log.info("未命中节点并且不存在下一个节点:{} ",name);
                return false;
            }

            log.info("命中节点: {}", name);
            if (handleAction != null) {
                // 执行逻辑
                if (handleAction.test(context)) {
                    if (next != null) {
                        log.info("命中节点:{} 执行下一个节点:{}", name, next.name);
                        context.addNodeLog(name);
                        return next.handle(context);
                    }
                    log.info("命中节点:{} 不存在下一个节点", name);
                } else {
                    log.info("命中节点:{} 中断后续节点执行", name);
                }
            }

            StopWatch stopWatch = context.getStopWatch();
            if (stopWatch.getTaskCount() > 0) {
                log.info("\n"+context.getStopWatch().prettyPrint());
            }

            context.addNodeLog(name);
            log.info("当前执行的节点为: [{}]", context.prettyPrint());
            return true;
        } catch (ResponsibilityChainException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponsibilityChainException(name, e);
        }
    }

    /**
     * 判断是否命中当前责任链节点
     *
     * @param context {@context TContext} 当前处理的上下文信息
     */
    public boolean hit(TContext context) {
        if (hitPredicate != null) {
            return hitPredicate.test(context);
        }
        return true;
    }

    public int getSort() {
        return sort;
    }

}
