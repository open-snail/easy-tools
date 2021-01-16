package com.example.sdk.chain;

import org.springframework.util.StopWatch;

import java.util.List;

/**
 * 提供对节点操作的处理器
 *
 * @author shuguang.zhang
 * @date 2021-01-15
 */
public interface Processor {

  /**
   * 返回执行节点日志
   *
   * @return 执行的节点信息
   */
  List<String> getNodeLog();

  /**
   * 获取StopWatch
   *
   * @return {@link StopWatch} stop watch
   */
  StopWatch getStopWatch();

  /**
   * 输入执行的节点信息
   *
   * @return
   */
  String prettyPrint();

  /**
   * 清除节点日志
   * @return
   */
  void clearLog();

  /**
   * 记录链路执行节点日志
   * @param chainName
   */
  void addNodeLog(String chainName);

  /**
   * 设置是否执行下一个节点
   *
   * @return
   */
  void setExecuteNextNode(boolean executeNextNode);
}
