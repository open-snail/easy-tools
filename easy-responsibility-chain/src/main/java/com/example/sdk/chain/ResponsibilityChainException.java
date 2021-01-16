package com.example.sdk.chain;

import lombok.Data;

/**
 * 责任链的异常
 *
 * @author shuguang.zhang
 * @date 2021-01-15
 */
@Data
public class ResponsibilityChainException extends RuntimeException {

    private static final long serialVersionUID = -215073194559629227L;

    /**
     * 节点名称
     */
    private String name;

    public ResponsibilityChainException(String name, Exception e) {
        super(e.getMessage(), e);
        this.setName(name);
    }

    public ResponsibilityChainException(String detailMessage) {
        super(detailMessage);
    }
}
