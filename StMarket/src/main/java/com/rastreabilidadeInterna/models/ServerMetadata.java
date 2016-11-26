package com.rastreabilidadeInterna.models;

import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by felipe on 08/07/15.
 */
public class ServerMetadata extends SugarRecord<ServerMetadata> {
    private String tag;
    private String value;

    public ServerMetadata() {
    }

    public ServerMetadata(String tag, String value) {
        this.tag = tag;
        this.value = value;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
