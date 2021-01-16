package com.example.sdk.annotation;

import java.lang.annotation.*;

/**
 * 获取流程编排的具体执行方法信息
 *
 * @author zhangshuguang
 * @date 2020/04/03
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponsibilityChainMethod {

    /**
     * 责任链组名
     *
     * @return
     */
    String groupName() default "";

    /**
     * 责任链名称，为空则取当前方法的名称
     *
     * @return
     */
    String name() default "";

    /**
     * 描述
     *
     * @return
     */
    String desc() default "";

    /**
     * 责任链节点执行顺序，不可重复
     *
     * @return
     */
    int order() default 0;

    /**
     * 是否有返回值
     *
     * @return
     */
    boolean isReturn() default false;

    /**
     * 是否打印耗时
     *
     * @return true:打印 false:不打印
     */
    boolean isPrintTime() default false;
}
