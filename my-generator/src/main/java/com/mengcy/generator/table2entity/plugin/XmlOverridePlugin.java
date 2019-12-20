package com.mengcy.generator.table2entity.plugin;

import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import java.util.List;

/**
 * @author mengcy
 * @date 2019/4/26
 * 强制覆盖已存在mapper.xml
 */
public class XmlOverridePlugin extends PluginAdapter{

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        // 此方法1.3.5及以前版本不支持
        sqlMap.setMergeable(false);

        return super.sqlMapGenerated(sqlMap, introspectedTable);
    }
}
