package com.mengcy.generator.tpl2code.worker;


import com.mengcy.generator.tpl2code.config.GeneratorConfig;
import com.mengcy.generator.tpl2code.model.Field;
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
    public static List<Table> loadTables(GeneratorConfig config) throws SQLException {

        // 查询定制前缀项目的所有表
        JdbcUtil jdbcUtil = new JdbcUtil(config.getJdbcDriver(), config.getJdbcUrl(),
                config.getJdbcUser(), config.getJdbcPassword());
        String sql = "SELECT table_name FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = '"
                + config.getModuleDatabase()
                + "' AND table_name LIKE '"
                + config.getModuleTableName() + "%';";
        List<Map> result = jdbcUtil.selectByParams(sql, null);

        List<Table> tables = new ArrayList<>();
        for (Map map : result) {
            System.out.println(map.get("TABLE_NAME"));
            Table table = new Table();
            table.setTableName(map.get("TABLE_NAME").toString());
            table.setModelName(StringHelper.lineToHump(table.getTableName().replace(config.getModelTrimPrefix(),"")));

            table.setFields(loadFields(jdbcUtil, table.getTableName(), config.getModelFieldTrimPrefix()));
            tables.add(table);
        }
        jdbcUtil.release();

        return tables;
    }

    public static List<Field> loadFields(JdbcUtil jdbcUtil, String tableName, String prefix) throws SQLException {
        List<Field> fields = new ArrayList<>();
        String sql = "SELECT TABLE_NAME,COLUMN_NAME,DATA_TYPE,COLUMN_COMMENT FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='"
                + tableName + "'";
        List<Map> result = jdbcUtil.selectByParams(sql,null);
        for(Map map : result){
            Field field = new Field();
            field.setFieldName(map.get("COLUMN_NAME").toString());
            field.setFieldComment(map.get("COLUMN_COMMENT").toString());
            field.setFieldType(map.get("DATA_TYPE").toString());
            field.setModelFieldName(StringHelper.lineToHump(field.getFieldName().replace(prefix,"")));
        }

        return fields;
    }
}
