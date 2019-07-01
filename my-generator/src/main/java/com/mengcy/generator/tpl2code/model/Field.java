package com.mengcy.generator.tpl2code.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zkzc-mcy on 2017/11/8.
 */
public class Field {

    public static final String FORM_NORMAL = "input";
    public static final String FORM_SELECT = "select";
    public static final String FORM_TEXTAREA = "textarea";
    public static final String FORM_CHECKBOX = "checkox";

    /** 数据表字段名称 */
    private String fieldName;
    /** 数据字段类型 */
    private String fieldType;
    /** 备注信息 */
    private String fieldComment;
    /** 实体类名称 */
    private String modelFieldName;
    /** 页面输入形式*/
    private String modelInputType = FORM_NORMAL;

    private List<KeyValue> modelDefaultValue;

    public Field(){
        modelDefaultValue = new ArrayList<>();
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldComment() {
        return fieldComment;
    }

    public void setFieldComment(String fieldComment) {
        this.fieldComment = fieldComment;
        if(fieldComment.toLowerCase().contains(FORM_SELECT)){
            modelInputType = FORM_SELECT;
            String values = fieldComment.substring(fieldComment.lastIndexOf("(") + 1,fieldComment.lastIndexOf(")"));
            if(values != null && !"".equals(values)){
                String[] valueArr = values.split(",");
                for(String item : valueArr){
                    modelDefaultValue.add(new KeyValue(item));
                }
            }
        }else if(fieldComment.toLowerCase().contains(FORM_NORMAL)){
            modelInputType = FORM_NORMAL;
        }else if(fieldComment.toLowerCase().contains(FORM_TEXTAREA)){
            modelInputType = FORM_TEXTAREA;
        }else if(fieldComment.toLowerCase().contains(FORM_CHECKBOX)){
            modelInputType = FORM_CHECKBOX;
        }
    }

    public String getModelFieldName() {
        return modelFieldName;
    }

    public void setModelFieldName(String modelFieldName) {
        this.modelFieldName = modelFieldName;
    }

    public String getModelInputType() {
        return modelInputType;
    }

    public void setModelInputType(String modelInputType) {
        this.modelInputType = modelInputType;
    }

    public List<KeyValue> getModelDefaultValue() {
        return modelDefaultValue;
    }

    public void setModelDefaultValue(List<KeyValue> modelDefaultValue) {
        this.modelDefaultValue = modelDefaultValue;
    }


    public class KeyValue{
        private String key;
        private String value;

        public KeyValue(){};

        /**
         * @param data 数据字符串 形式 key:value
         */
        public KeyValue(String data){
            String[] dataArr = data.split(":");
            if(dataArr.length == 2){
                this.key = dataArr[0];
                this.value = dataArr[1];
            }
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

    public static void main(String[] args){
        String test = "select(0:删除,1:正常,2:禁用)";

        Field field = new Field();
        field.setFieldComment(test);

        System.out.println(field.getModelDefaultValue().get(0).getKey());
    }
}
