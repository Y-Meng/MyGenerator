package com.mengcy.generator.code2sql.worker;

import com.mengcy.generator.code2sql.config.GeneratorConfig;
import org.springframework.beans.factory.config.BeanDefinition;

import java.util.Set;

/**
 * @author mengcy 2019/9/10
 * mysql数据表生成
 */
public class MysqlTableGenerator extends BaseTableGenerator {

    @Override
    public void generateTable(GeneratorConfig config, Set<BeanDefinition> beans) {

        beans.forEach(item -> System.out.println(item.getBeanClassName()));
    }
}
