package com.tp.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 消息对象
 */
@Data
public class Messages implements Serializable {
    /**
     * 消息角色
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息作者名称
     */
    private String name;


    public Messages(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public Messages(String role, String content, String name) {
        this.role = role;
        this.content = content;
        this.name = name;
    }

    @Serial
    private static final long serialVersionUID = 1;
}
