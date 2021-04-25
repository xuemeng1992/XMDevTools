package com.xuemeng.xmdevtools.bean;

/**
 * Created by Administrator on 2016/4/26.
 * 简单的键值对 对象
 */
public class KeyValue extends JsonBean {
    private String key;
    private String value;

    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
