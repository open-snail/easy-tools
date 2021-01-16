package com.example.demo;

import com.example.sdk.annotation.ResponsibilityChainMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author www.byteblogs.com
 * @date 2021-01-13
 * @since 2.0
 */
@Service
public class BusinessDemo2 {

    public static final String GROUP_NAME = "BusinessDemo2";

    @Autowired
    private DemoA demoA;

    @ResponsibilityChainMethod(groupName = GROUP_NAME, order = 1, isReturn = true, desc = "测试二")
    public boolean test1(DemoBaseContext demoBaseContext) {
        demoBaseContext.addNodeLog("进入了第一个节点");
        demoA.test();
        System.out.println("test1" + demoBaseContext.getName());
        return true;
    }

    @ResponsibilityChainMethod(groupName = GROUP_NAME, order = 2, isReturn = true)
    public boolean test2(DemoBaseContext demoBaseContext) {
        demoBaseContext.setExecuteNextNode(false);
        System.out.println("test2" + demoBaseContext.getName());
        return true;
    }

    // 测试返回false的情况，中断执行链
    @ResponsibilityChainMethod(groupName = GROUP_NAME, order = 3, isReturn = true)
    public boolean test3(DemoBaseContext demoBaseContext) {
        System.out.println("test3" + demoBaseContext.getName());
        return false;
    }

    @ResponsibilityChainMethod(groupName = GROUP_NAME, order = 4, isReturn = true)
    public boolean test4(DemoBaseContext demoBaseContext) {
        System.out.println("test4" + demoBaseContext.getName());
        return true;
    }

    // 没有放值的情况，默认返回true
    @ResponsibilityChainMethod(groupName = GROUP_NAME, order = 5, isReturn = false)
    public void test5(DemoBaseContext demoBaseContext) {
        System.out.println("test5" + demoBaseContext.getName());
    }

    // 测试是否执行下一个节点
    @ResponsibilityChainMethod(groupName = GROUP_NAME, order = 6, isReturn = false)
    public void test6(DemoBaseContext demoBaseContext) {
        demoBaseContext.setExecuteNextNode(false);
        System.out.println("test6" + demoBaseContext.getName());
    }

    @ResponsibilityChainMethod(groupName = GROUP_NAME, order = 7, isReturn = false)
    public void test7(DemoBaseContext demoBaseContext) {
        System.out.println("test7" + demoBaseContext.getName());
    }
}
