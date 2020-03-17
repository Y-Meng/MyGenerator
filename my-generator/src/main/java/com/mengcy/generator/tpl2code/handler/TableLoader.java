package com.mengcy.generator.tpl2code.handler;


import com.mengcy.generator.tpl2code.config.CodeGenConfig;
import com.mengcy.generator.tpl2code.model.Column;
import com.mengcy.generator.tpl2code.model.Table;
import com.mengcy.generator.util.JdbcUtil;
import com.mengcy.generator.util.StringHelper;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by zkzc-mcy on 2017/11/8.
 */
public class TableLoader {

    /**
     * 加载数据表信息
     * @param config 生成配置类
     * @return 数据表
     * @throws SQLException
     */
    public static List<Table> loadTables(CodeGenConfig config) throws SQLException {

        // 查询定制前缀项目的所有表
        JdbcUtil jdbcUtil = new JdbcUtil(config.getJdbcDriver(), config.getJdbcUrl(),
                config.getJdbcUser(), config.getJdbcPassword());
        String sql = "SELECT table_name FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = '"
                + config.getModuleDatabase()
                + "' AND table_name LIKE '"
                + config.getModuleTableName() + "%';";
        List<Map<String, Object>> result = jdbcUtil.selectByParams(sql, null);

        List<Table> tables = new ArrayList<>();
        for (Map map : result) {
            System.out.println(map.get("TABLE_NAME"));
            Table table = new Table();
            table.setTableName(map.get("TABLE_NAME").toString());
            table.setModelName(StringHelper.lineToHump(table.getTableName().replace(config.getModelTrimPrefix(),"")));

            table.setColumns(loadFields(jdbcUtil, table.getTableName(), config.getModelFieldTrimPrefix()));
            tables.add(table);
        }
        jdbcUtil.release();

        return tables;
    }

    public static List<Column> loadFields(JdbcUtil jdbcUtil, String tableName, String prefix) throws SQLException {
        List<Column> columns = new ArrayList<>();
        String sql = "SELECT TABLE_NAME,COLUMN_NAME,DATA_TYPE,COLUMN_COMMENT FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='"
                + tableName + "'";
        List<Map<String, Object>> result = jdbcUtil.selectByParams(sql,null);
        for(Map map : result){
            Column column = new Column();
            column.setFieldName(map.get("COLUMN_NAME").toString());
            column.setFieldComment(map.get("COLUMN_COMMENT").toString());
            column.setFieldType(map.get("DATA_TYPE").toString());
            column.setModelFieldName(StringHelper.lineToHump(column.getFieldName().replace(prefix,"")));
        }

        return columns;
    }
}
