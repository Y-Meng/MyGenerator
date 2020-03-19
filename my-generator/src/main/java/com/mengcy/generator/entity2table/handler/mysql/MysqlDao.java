package com.mengcy.generator.entity2table.handler.mysql;

import com.mengcy.generator.util.JdbcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author mengcy 2019/9/10
 */
public class MysqlDao {

    private static Logger logger = LoggerFactory.getLogger(MysqlDao.class);

    private final String DROP_TABLE = "drop table if exists `name`;";
    private final String CHECK_TABLE = "select count(1) from information_schema.tables where table_name = ? and table_schema = (select database());";
    private final String SELECT_COLUMNS = "select * from information_schema.columns where table_name = ? and table_schema = (select database())";

    private String dbUrl;
    private String dbUser;
    private String dbPwd;
    private JdbcUtil jdbc;

    public MysqlDao(String dbUrl, String dbUser, String dbPwd) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPwd = dbPwd;
        this.jdbc = new JdbcUtil(this.dbUrl, this.dbUser, this.dbPwd);
    }

    public void close(){
        if(jdbc != null){
            jdbc.release();
        }
    }

    public void dropTable(String tableName) {
        logger.info("DROP TABLE " + tableName);
        jdbc.executeSql(DROP_TABLE.replace("name", tableName));
    }

    public void createTable(String sql) {
        logger.info(sql);
        jdbc.executeSql(sql);
    }

    public boolean checkExist(String tableName) {
        logger.info("CHECK TABLE EXIST " + tableName);
        int count = jdbc.countByParams(CHECK_TABLE, tableName);
        return count > 0;
    }

    public List<MysqlTableColumn> selectColumns(String tableName) {
        logger.info("CHECK TABLE COLUMNS " + tableName);
        List<Map<String, Object>> rows = jdbc.selectByParams(SELECT_COLUMNS, tableName);
        List<MysqlTableColumn> columns = new ArrayList<>();
        for(Map<String, Object> row : rows){
            MysqlTableColumn column = new MysqlTableColumn();
            column.setName(row.get(MysqlTableColumn.COLUMN_NAME).toString());

            // 数据类型
            String type = row.get(MysqlTableColumn.DATA_TYPE).toString();

            column.setType(type);
            column.setPrimaryKey("PRI".equals(row.get(MysqlTableColumn.COLUMN_KEY)));
            column.setUnique("UNI".equals(row.get(MysqlTableColumn.COLUMN_KEY)));
            column.setNullable("YES".equals(row.get(MysqlTableColumn.IS_NULLABLE)));

            // char varchar长度
            if(type.equals("char") || type.equals("varchar")){
                column.setLength(Integer.parseInt(row.get(MysqlTableColumn.CHARACTER_MAXIMUM_LENGTH).toString()));
            }

            // float, double, decimal长度
            if(type.equals("float") || type.equals("double") || type.equals("decimal")){
                if(!StringUtils.isEmpty(row.get(MysqlTableColumn.NUMERIC_PRECISION))) {
                    column.setLength(Integer.parseInt(row.get(MysqlTableColumn.NUMERIC_PRECISION).toString()));
                }
                if(!StringUtils.isEmpty(row.get(MysqlTableColumn.NUMERIC_SCALE))){
                    column.setScale(Integer.parseInt(row.get(MysqlTableColumn.NUMERIC_SCALE).toString()));
                }
            }

            if(column.isPrimaryKey() && row.get(MysqlTableColumn.EXTRA) != null){
                if(row.get(MysqlTableColumn.EXTRA).toString().contains("auto_increment")){
                    column.setAutoIncrement(true);
                }
            }

            if(!column.isPrimaryKey()) {
                // text, date, time 或有默认值
                Object defaultValue = row.get(MysqlTableColumn.COLUMN_DEFAULT);

                if (defaultValue != null || type.equals("date") || type.equals("time") || type.contains("text")) {
                    StringBuilder definition = new StringBuilder();
                    switch (type) {
                        case "float":
                            if (column.getLength() > 0 && column.getScale() > 0) {
                                definition.append("float(").append(column.getLength()).append(",").append(column.getScale()).append(")");
                            } else {
                                definition.append("float");
                            }
                            break;
                        case "double":
                            if (column.getLength() > 0 && column.getScale() > 0) {
                                definition.append("double(").append(column.getLength()).append(",").append(column.getScale()).append(")");
                            } else {
                                definition.append("double");
                            }
                            break;
                        case "decimal":
                            if (column.getLength() > 0 && column.getScale() > 0) {
                                definition.append("decimal(").append(column.getLength()).append(",").append(column.getScale()).append(")");
                            } else {
                                definition.append("decimal");
                            }
                            break;
                        case "char":
                            definition.append("char(").append(column.getLength()).append(")");
                            break;
                        case "varchar":
                            definition.append("varchar(").append(column.getLength()).append(")");
                            break;
                        default:
                            definition.append(type);
                            break;
                    }
                    if (!column.isNullable()) {
                        definition.append(" NOT NULL");
                    }


                    if (defaultValue != null) {
                        if (defaultValue.equals("CURRENT_TIMESTAMP")) {
                            definition.append(" DEFAULT ").append(defaultValue.toString());
                        } else {
                            definition.append(" DEFAULT '").append(defaultValue.toString()).append("'");
                        }
                    }
                    column.setDefinition(definition.toString());
                }
            }

            columns.add(column);
        }
        return columns;
    }

    public void dropUniqueKey(String table, List<MysqlTableColumn> dropUniqueColumns) {
        if(dropUniqueColumns == null || dropUniqueColumns.size() == 0){
            return;
        }

        StringBuilder dropSql = new StringBuilder();

        // 例：alter table t_demo drop index uni_key;
        dropSql.append("alter table ").append(table);
        for(MysqlTableColumn column : dropUniqueColumns){
            dropSql.append(" drop index `").append(column.getName()).append("`,");
        }
        dropSql.deleteCharAt(dropSql.length() - 1);
        dropSql.append(";");

        String dropUniqueSql = dropSql.toString();
        logger.info("DROP UNIQUE KEY : " + dropUniqueSql);
        jdbc.executeSql(dropUniqueSql);
    }

    public void modifyColumns(String table, boolean keyChange, Set<String> primaryKey,
                              List<MysqlTableColumn> addColumns, List<MysqlTableColumn> modifyColumns) {

        if(CollectionUtils.isEmpty(addColumns) && CollectionUtils.isEmpty(modifyColumns)){
            return;
        }

        StringBuilder modifySql = new StringBuilder();

        // 例：alter table t_demo modify new_column int;
        modifySql.append("alter table ").append(table);

        if(modifyColumns != null) {
            for (MysqlTableColumn column : modifyColumns) {
                modifySql.append(" modify ").append(column.toSql());
                modifySql.append(",");
            }
        }

        if(addColumns != null){
            for (MysqlTableColumn column : addColumns) {
                modifySql.append(" add ").append(column.toSql());
                modifySql.append(",");
            }
        }

        if(keyChange){
            modifySql.append("drop primary key,");
            if(primaryKey != null && primaryKey.size() > 0) {
                modifySql.append("add primary key (");
                for(String key : primaryKey){
                    modifySql.append("`").append(key).append("`").append(",");
                }
                modifySql.deleteCharAt(modifySql.length() - 1);
                modifySql.append("),");
            }
        }

        modifySql.deleteCharAt(modifySql.length() - 1);
        modifySql.append(";");

        String sql = modifySql.toString();
        logger.info("MODIFY COLUMN : " + sql);
        jdbc.executeSql(sql);
    }
}
