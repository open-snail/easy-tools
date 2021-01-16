# easy-responsibility-chain

#### 介绍
简单易用，轻量责任链，让你的代码更精简

#### 安装教程

1. 引入maven
```java
<dependency>
  <groupId>com.byteblogs</groupId>
  <artifactId>easy-responsibility-chain</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

#### 使用说明

##### 属性描述
groupName：责任链组名
name：责任链名称
desc：描述
order：责任链节点执行顺序，不可重复
isReturn：是为了判断当前方法是否有返回布尔值，如果返回的为true则继续执行，false则终止整个链
isPrintTime：这个为了对节点进行性能监控，可以打印出节点的执行情况， 比如这样
```
StopWatch '': running time = 386691 ns
---------------------------------------------
ns         %     Task name
---------------------------------------------
000153973  040%  groupName:BusinessDemo name:test1
000030714  008%  groupName:BusinessDemo name:test2
000038957  010%  groupName:BusinessDemo name:test3
000056935  015%  groupName:BusinessDemo name:test5
000053362  014%  groupName:BusinessDemo name:test6
000052750  014%  groupName:BusinessDemo name:test8
```

本着友好原则，默认打印出调用链节点信息
```当前执行的节点为: [test1->test2->test3->test5->test6->test8]```

> 注意事项：
1、有这样一个方法demoBaseContext.setExecuteNextNode(false);表示下一个节点是否被执行，默认是true，如果是false下一个节点将被跳过。
2、这里需要注意groupName不为空，order不能有重复的，切记。
3、每个方法的参数只能有一个参数，且继承BaseContext。
4、isReturn是代表方法的返回值是否是布尔类型 
      isReturn = true 说明返回值是布尔类型，则由方法的返回值来表明是否执行后续节点，true继续执行，false则中断执行链
      isReturn =  false 说明返回值不是布尔类型，则框架默认返回是true继续执行后续节点
5、这里说明一下，本框架目前不支持动态跳转到指定节点，可以支持跳过下一个节点。

#### 源码分析
##### 责任链加载执行流程图
描述：在spring 启动加载bean进行初始化的时候，spring会执行后置处理中的postProcessAfterInitialization，这样就可以对bean的方法通过反射进行判断是否含有ResponsibilityChainMethod注解，获取到方法上的注解进行解析，组装成链加载到内存中，等待调用。

<iframe id="embed_dom" name="embed_dom" frameborder="0" style="display:block;width:525px; height:245px;" src="https://www.processon.com/embed/60015a3b7d9c080e58d6c7d5"></iframe>

##### 责任链调用执行流程
描述： 其实使用很简单只需要注入ResponsibilityChainProcessor并且调用handle(demoBaseContext, BusinessDemo.GROUP_NAME)就可以执行了。通过对每个节点的判断决定是否执行下一个节点，直到所有节点被直接完。
<iframe id="embed_dom" name="embed_dom" frameborder="0" style="display:block;width:525px; height:245px;" src="https://www.processon.com/embed/6001600107912914e75e7a2a"></iframe>
