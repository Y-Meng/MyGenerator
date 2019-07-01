package com.mengcy.generator.tpl2code.model;

import java.util.List;

/**
 * Created by zkzc-mcy on 2017/11/7.
 */
public class Table {
    /** 表名*/
    private String tableName;
    /** 实体名*/
    private String modelName;
    /** 主键名*/
    private String pkName = "id";
    /** 主键是否自增*/
    private Boolean generatedId = true;

    /** 字段列表*/
    private List<Field> fields;

    public Table(){}

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getPkName() {
        return pkName;
    }

    public void setPkName(String pkName) {
        this.pkName = pkName;
    }

    public Boolean getGeneratedId() {
        return generatedId;
    }

    public void setGeneratedId(Boolean generatedId) {
        this.generatedId = generatedId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
