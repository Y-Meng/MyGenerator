package com.mengcy.generator.entity2table.handler.mysql;

import com.mengcy.generator.annotation.Comment;
import com.mengcy.generator.entity2table.config.GeneratorConfig;
import com.mengcy.generator.entity2table.handler.AbstractTableHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author mengcy 2019/9/10
 * mysql数据表生成
 */
public class MysqlTableHandler extends AbstractTableHandler {

    static Logger logger = LoggerFactory.getLogger(MysqlTableHandler.class);

    @Override
    protected void generateTable(GeneratorConfig config, Set<BeanDefinition> beans) {

        MysqlDao tableDao = new MysqlDao(config.getDbUrl(), config.getDbUser(), config.getDbPwd());

        // 获取所有表实体（合并同表实体类）
        Map<String, MysqlTable> tables = new HashMap<>(256);
        beans.forEach(item -> {
            String clzName = item.getBeanClassName();
            // 属性列表
            try {
                Class clz = Class.forName(clzName);
                // 表处理
                Table table = (Table) clz.getDeclaredAnnotation(Table.class);
                if(table != null) {
                    String tableName = table.name();
                    MysqlTable mysqlTable = getTable(tableName, clz);

                    MysqlTable uniqueTable = tables.get(mysqlTable.getName());
                    if(uniqueTable == null){
                        tables.put(mysqlTable.getName(), mysqlTable);
                    }else {
                        // 合并表字段
                        uniqueTable.getColumns().addAll(mysqlTable.getColumns());
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        // 处理表
        for(Map.Entry<String, MysqlTable> entry : tables.entrySet()){
            MysqlTable mysqlTable = entry.getValue();
            String tableName = mysqlTable.getName();
            if (config.getDdlAuto().equals(GeneratorConfig.AUTO_CREATE)) {
                // 删除表
                tableDao.dropTable(tableName);
                // 创建表
                tableDao.createTable(mysqlTable.toSql());
            } else if (config.getDdlAuto().equals(GeneratorConfig.AUTO_UPDATE)) {
                // 检查表是否存在
                boolean exist = tableDao.checkExist(tableName);
                if (!exist) {
                    // 创建表
                    tableDao.createTable(mysqlTable.toSql());
                } else {
                    // 修改表
                    List<MysqlTableColumn> oldColumnList = tableDao.selectColumns(tableName);
                    MysqlTable oldTable = new MysqlTable();
                    Set<String> primaryKey = new HashSet<>();
                    for(MysqlTableColumn column : oldColumnList){
                        if(column.isPrimaryKey()){
                            primaryKey.add(column.getName());
                        }
                    }
                    oldTable.setPrimaryKey(primaryKey);
                    oldTable.setColumns(oldColumnList);
                    modifyColumns(tableDao, tableName, oldTable, mysqlTable);
                }
            } else {
                logger.error("not support ddl auto config " + config.getDdlAuto());
                break;
            }
        }
        tableDao.close();
    }

    private void modifyColumns(MysqlDao tableDao, String table, MysqlTable oldTable, MysqlTable newTable) {

        // 需要添加的普通字段
        List<MysqlTableColumn> createColumns = new ArrayList<>();

        // 需要修改的普通字段
        List<MysqlTableColumn> modifyColumns = new ArrayList<>();

        // 需要删除的唯一约束；需要删除的字段暂不处理
        List<MysqlTableColumn> dropUniqueColumns = new ArrayList<>();


        for(MysqlTableColumn newColumn : newTable.getColumns()){

            boolean exit = false;
            for(MysqlTableColumn oldColumn : oldTable.getColumns()){

                if(oldColumn.getName().equals(newColumn.getName())){
                    exit = true;
                    // 1.原本是主键，现在不是
                    if(oldColumn.isPrimaryKey() && !newColumn.isPrimaryKey()){
                        // 取消主键，自增必须去除
                        newColumn.setAutoIncrement(false);
                        modifyColumns.add(newColumn);
                        break;
                    }

                    // 2.原本是唯一约束，现在不是
                    if(oldColumn.isUnique() && !newColumn.isUnique()){
                        // 自增必须去除
                        newColumn.setAutoIncrement(false);
                        dropUniqueColumns.add(newColumn);
                        modifyColumns.add(newColumn);
                        break;
                    }

                    // 3.1.注解定义检查
                    if(!StringUtils.isEmpty(newColumn.getDefinition())
                            && !newColumn.getDefinition().equals(oldColumn.getDefinition())){
                        modifyColumns.add(newColumn);
                        break;
                    }

                    // 3.2.注解定义检查
                    if(StringUtils.isEmpty(newColumn.getDefinition())
                            && !StringUtils.isEmpty(oldColumn.getDefinition())){
                        modifyColumns.add(newColumn);
                        break;
                    }

                    // 4.注解属性检查
                    if(StringUtils.isEmpty(newColumn.getDefinition()) && StringUtils.isEmpty(oldColumn)){
                        // 4.1.类型检查
                        if(!newColumn.getType().equals(oldColumn.getType())){
                            modifyColumns.add(newColumn);
                            break;
                        }

                        // 4.2.长度变化检查
                        if(newColumn.getLength() != oldColumn.getLength()){
                            modifyColumns.add(newColumn);
                            break;
                        }

                        // 4.3.小数点长度变化检查
                        if(newColumn.getScale() != oldColumn.getScale()){
                            modifyColumns.add(newColumn);
                            break;
                        }

                        // 4.4.非空变化检查
                        if(newColumn.isNullable() != oldColumn.isNullable()){
                            modifyColumns.add(newColumn);
                            break;
                        }

                        // 4.5.自增属性变化检查
                        if(newColumn.isAutoIncrement() != oldColumn.isAutoIncrement()){
                            modifyColumns.add(newColumn);
                            break;
                        }

                        // 4.6.原来不是主键现在是
                        if(newColumn.isPrimaryKey() && !oldColumn.isPrimaryKey()){
                            modifyColumns.add(newColumn);
                            break;
                        }

                        // 4.7.原来不是唯一约束，现在是
                        if(newColumn.isUnique() && !oldColumn.isUnique()){
                            modifyColumns.add(newColumn);
                            break;
                        }
                    }

                    break;
                }
            }

            if(!exit){
                createColumns.add(newColumn);
            }
        }

        // 删除唯一约束（需要在创建新字段和修改字段之前执行）
        tableDao.dropUniqueKey(table, dropUniqueColumns);

        // 主键变化
        boolean primaryKeyChange = false;
        Set<String> oldKeys = oldTable.getPrimaryKey();
        Set<String> newKeys = newTable.getPrimaryKey();
        if(oldKeys.size() != newKeys.size()){
            primaryKeyChange = true;
        }else {
            for(String newKey : newKeys){
                if(!oldKeys.contains(newKey)){
                    primaryKeyChange = true;
                    break;
                }
            }
        }

        // 创建新字段, 修改字段
        tableDao.modifyColumns(table, primaryKeyChange, newKeys,  createColumns, modifyColumns);
    }

    private MysqlTable getTable(String tableName, Class clz) {

        MysqlTable table = new MysqlTable();
        table.setName(tableName);

        Set<String> primaryKey = new HashSet<>();
        List<MysqlTableColumn> columns = new ArrayList<>();

        Field[] fields = clz.getDeclaredFields();
        for(Field field : fields){
            Class fieldType = field.getType();
            logger.info("column " + field.getName() + "-" + fieldType.getName());

            Transient trans = field.getAnnotation(Transient.class);
            if(trans != null){
                break;
            }

            Id id = field.getAnnotation(Id.class);
            GeneratedValue generated = field.getAnnotation(GeneratedValue.class);
            Column column = field.getAnnotation(Column.class);
            Comment comment = field.getAnnotation(Comment.class);

            MysqlTableColumn tableColumn = MysqlTypeMap.create(fieldType.getName());
            if(column != null){
                tableColumn.setName(column.name());
                String definition = column.columnDefinition();
                if(StringUtils.isEmpty(definition)) {
                    if(column.length() != 255) {
                        tableColumn.setLength(column.length());
                    }
                    if(column.scale() > 0){
                        tableColumn.setScale(column.scale());
                    }

                    tableColumn.setUnique(column.unique());
                    tableColumn.setNullable(column.nullable());
                }else {
                    tableColumn.setDefinition(definition);
                }
                tableColumn.setPrimaryKey(false);
            }else {
                tableColumn.setName(field.getName());
            }

            if(id != null){
                tableColumn.setPrimaryKey(true);
                tableColumn.setUnique(false);
                tableColumn.setNullable(false);
                if (generated != null) {
                    if (generated.strategy().equals(GenerationType.IDENTITY)) {
                        tableColumn.setAutoIncrement(true);
                    } else {
                        tableColumn.setAutoIncrement(false);
                    }
                } else {
                    tableColumn.setAutoIncrement(true);
                }
                primaryKey.add(tableColumn.getName());
            }

            if(comment != null){
                tableColumn.setComment(comment.value());
            }

            columns.add(tableColumn);
        }
        table.setPrimaryKey(primaryKey);
        table.setColumns(columns);

        if(clz.getAnnotation(Comment.class) != null){
            Comment comment = (Comment) clz.getDeclaredAnnotation(Comment.class);
            table.setComment(comment.value());
        }

        return table;
    }
}
