package com.x.xrpc.protocol;

import lombok.Getter;

/**
 * @author lsx
 * @date 2024-07-27
 */
@Getter
public enum MessageHeaderIndexEnum {

    MAGIC_INDEX(0, "magic"),
    VERSION_INDEX(1, "version"),
    SERIALIZE_INDEX(2, "serialize"),
    TYPE_INDEX(3, "type"),
    STATUS_INDEX(4, "status"),
    ;

    private final int index;
    private final String desc;

    MessageHeaderIndexEnum(int index, String desc) {
        this.index = index;
        this.desc = desc;
    }
}
