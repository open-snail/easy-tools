package com.example.demo;

import com.example.sdk.support.ResponsibilityChainProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author www.byteblogs.com
 * @date 2021-01-13
 * @since 2.0
 */
@RestController
public class Controller {

    @Autowired
    private ResponsibilityChainProcessor<DemoBaseContext> processor;

    @GetMapping("call")
    public void call(DemoBaseContext demoBaseContext) {
        processor.handle(demoBaseContext, BusinessDemo.GROUP_NAME);
    }
}
