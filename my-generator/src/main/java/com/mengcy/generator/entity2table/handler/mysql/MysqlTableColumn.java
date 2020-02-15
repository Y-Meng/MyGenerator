package com.mengcy.generator.entity2table.handler.mysql;

import org.springframework.util.StringUtils;

/**
 * @author mengcy 2020-02-11
 * mysql 数据字段配置
 */
public class MysqlTableColumn {

    /**
     * 字段名
     */
    public static final String COLUMN_NAME = "COLUMN_NAME";
    /**
     * 默认值
     */
    public static final String COLUMN_DEFAULT = "COLUMN_DEFAULT";
    /**
     * 是否可为null，值：(YES,NO)
     */
    public static final String IS_NULLABLE = "IS_NULLABLE";
    /**
     * 数据类型
     */
    public static final String DATA_TYPE = "DATA_TYPE";

    /**
     * varchar长度
     */
    public static final String CHARACTER_MAXIMUM_LENGTH = "CHARACTER_MAXIMUM_LENGTH";
    /**
     * 长度，如果是0的话是null
     */
    public static final String NUMERIC_PRECISION = "NUMERIC_PRECISION";
    /**
     * 小数点数
     */
    public static final String NUMERIC_SCALE = "NUMERIC_SCALE";
    /**
     * 是否为主键，是的话是PRI
     */
    public static final String COLUMN_KEY = "COLUMN_KEY";

    /**
     * 是否为自动增长，是的话为auto_increment
     */
    public static final String EXTRA = "EXTRA";

    /**
     * 字段名
     */
    private String	name;

    /**
     * 定义（覆盖下面所有参数）
     */
    private String definition;

    /**
     * 字段类型
     */
    private String type;

    /**
     * 类型长度（double等双精度类型总长度）
     */
    private int length;

    /**
     * 双精度类型小数点长度（默认两位）
     */
    private int scale;

    /**
     * 字段是否非空
     */
    private boolean	nullable;

    /**
     * 字段是否是主键
     */
    private boolean	primaryKey;

    /**
     * 主键是否自增
     */
    private boolean	autoIncrement;

    /**
     * 值是否唯一
     */
    private boolean	unique;

    /**
     * 备注
     */
    private String comment;

    public MysqlTableColumn(){}

    public MysqlTableColumn(String type, int length, int scale) {
        this.type = type;
        this.length = length;
        this.scale = scale;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String toSql() {

        StringBuilder builder = new StringBuilder();

        builder.append("`").append(name).append("`");
        if(StringUtils.isEmpty(definition)){
            builder.append(" ").append(type);
            if(type.equals("double") || type.equals("decimal")){
                if(length > 0 && scale > 0){
                    builder.append("(").append(length).append(",").append(scale).append(")");
                }
            }else {
                if(length > 0){
                    builder.append("(").append(length).append(")");
                }
            }

            if(nullable){
                builder.append(" NULL");
            }else {
                builder.append(" NOT NULL");
            }

            if(autoIncrement){
                builder.append(" AUTO_INCREMENT");
            }
        }else {
            builder.append(" ").append(definition);
        }

        if(!StringUtils.isEmpty(comment)){
            builder.append(" ").append("COMMENT '").append(comment).append("'");
        }
        return builder.toString();
    }
}
