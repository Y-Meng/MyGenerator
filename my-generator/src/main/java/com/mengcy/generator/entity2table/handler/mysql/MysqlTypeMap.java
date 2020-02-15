package com.mengcy.generator.entity2table.handler.mysql;


/**
 * @author mengcy
 * @date 2019/4/25
 */
public class MysqlTypeMap {
    public static MysqlTableColumn create(String fieldType){
        switch (fieldType){
            case "byte":
            case "java.lang.Byte":
                return new MysqlTableColumn("tinyint", 0, 0);

            case "short":
            case "java.lang.Short":
                return new MysqlTableColumn("smallint", 0, 0);

            case "int":
            case "java.lang.Integer":
                return new MysqlTableColumn("int", 0, 0);

            case "long":
            case "java.lang.Long":
                return new MysqlTableColumn("bigint", 0, 0);

            case "float":
            case "java.lang.Float":
                return new MysqlTableColumn("float", 0, 0);

            case "double":
            case "java.lang.Double":
                return new MysqlTableColumn("double", 0, 0);

            case "java.math.BigDecimal":
                return new MysqlTableColumn("double", 0, 0);

            case "char":
            case "java.lang.Char":
            case "java.lang.String":
                return new MysqlTableColumn("varchar", 255, 0);

            case "java.util.Date":
            case "java.sql.Timestamp":
                return new MysqlTableColumn("timestamp", 0, 0);

            case "java.sql.Date":
                return new MysqlTableColumn("date", 0, 0);

            case "java.sql.Time":
                return new MysqlTableColumn("time", 0, 0);

            default:
                return new MysqlTableColumn("varchar", 255, 0);
        }
    }
}
