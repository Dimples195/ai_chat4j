package com.tp.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class Messages implements Serializable {
    /**
     * The role of the author of this message. One of system, user, or assistant.
     */
    private String role;

    /**
     * The contents of the message.
     */
    private String content;

    /**
     * The name of the author of this message. May contain a-z, A-Z, 0-9, and underscores, with a maximum length of 64 characters.
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
