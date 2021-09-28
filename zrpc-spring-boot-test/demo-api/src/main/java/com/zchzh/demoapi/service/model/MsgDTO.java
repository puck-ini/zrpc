package com.zchzh.demoapi.service.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengchzh
 * @date 2021/9/25
 */

@Data
public class MsgDTO implements Serializable {

    private static final long serialVersionUID = 5783404145009049870L;
    private int id;
    private String msg;

    public MsgDTO(int i) {
        this.id = i;
        this.msg = "msg: " + i;
    }
}
