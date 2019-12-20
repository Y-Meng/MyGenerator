package com.mengcy.generator.entity2table.handler.mysql;

import com.mengcy.generator.entity2table.config.GeneratorConfig;
import com.mengcy.generator.entity2table.handler.BaseTableHandler;
import org.springframework.beans.factory.config.BeanDefinition;

import java.util.Set;

/**
 * @author mengcy 2019/9/10
 * mysql数据表生成
 */
public class MysqlTableHandler extends BaseTableHandler {

    @Override
    public void generateTable(GeneratorConfig config, Set<BeanDefinition> beans) {

        beans.forEach(item -> System.out.println(item.getBeanClassName()));
    }
}
