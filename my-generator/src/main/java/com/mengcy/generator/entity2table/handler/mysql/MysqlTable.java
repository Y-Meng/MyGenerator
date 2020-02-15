package com.mengcy.generator.entity2table.handler.mysql;

import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * @author mengcy 2020-02-11
 */
public class MysqlTable {
    private String name;
    private Set<String> primaryKey;
    private String comment;
    private List<MysqlTableColumn> columns;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Set<String> primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<MysqlTableColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<MysqlTableColumn> columns) {
        this.columns = columns;
    }

    public String toSql(){

        if(columns == null || columns.size() == 0){
            return "";
        }

        StringBuilder builder = new StringBuilder();
        StringBuilder primaryKey = new StringBuilder();
        StringBuilder uniqueKey = new StringBuilder();

        builder.append("CREATE TABLE IF NOT EXISTS `").append(name).append("` (");
        for(MysqlTableColumn column : columns){
            if(column.isPrimaryKey()){
                primaryKey.append("`").append(column.getName()).append("`").append(",");
            }
            if(column.isUnique()){
                uniqueKey.append("`").append(column.getName()).append("`").append(",");
            }
            builder.append(" ").append(column.toSql()).append(",");
        }

        if(primaryKey.length() > 0) {
            primaryKey.deleteCharAt(primaryKey.length() - 1);
            builder.append(" PRIMARY KEY (").append(primaryKey.toString()).append("),");
        }

        if(uniqueKey.length() > 0){
            uniqueKey.deleteCharAt(uniqueKey.length() - 1);
            builder.append(" UNIQUE KEY (").append(uniqueKey.toString()).append("),");
        }

        builder.deleteCharAt(builder.length() - 1);
        builder.append(")");

        if(!StringUtils.isEmpty(comment)){
            builder.append(" COMMENT='").append(comment).append("';");
        }

        return builder.toString();
    }


}
