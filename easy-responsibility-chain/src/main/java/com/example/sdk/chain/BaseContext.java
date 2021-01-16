package com.example.sdk.chain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

/**
 * 整个责任链的上下文并且提供一些基础能力，对链中的节点进行操作比如日志、执行时间的监控
 *
 * @author shuguang.zhang
 * @date 2021-01-15
 */
@Slf4j
public class BaseContext implements Processor {

    private static final List<String> CHAIN_LOGS = new ArrayList<>();

    private static final StopWatch STOP_WATCH = new StopWatch();

    /**
     * 是否执行下一个节点
     */
    private boolean isExecuteNextNode = true;

    @Override
    public final List<String> getNodeLog() {
        return CHAIN_LOGS;
    }

    @Override
    public StopWatch getStopWatch() {
        return STOP_WATCH;
    }

    @Override
    public String prettyPrint() {
        StringBuilder stringBuilder = new StringBuilder();

        CHAIN_LOGS.forEach(info -> stringBuilder.append(info).append("->"));
        String str = stringBuilder.toString();
        return str.substring(0, str.lastIndexOf("->"));
    }

    @Override
    public final void clearLog() {
        CHAIN_LOGS.clear();
    }

    @Override
    public final void addNodeLog(String chainName) {
        CHAIN_LOGS.add(chainName);
    }

    public boolean isExecuteNextNode() {
        return isExecuteNextNode;
    }

    @Override
    public void setExecuteNextNode(boolean executeNextNode) {
        isExecuteNextNode = executeNextNode;
    }
}
