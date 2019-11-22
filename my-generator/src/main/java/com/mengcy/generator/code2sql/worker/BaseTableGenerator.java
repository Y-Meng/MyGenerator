package com.mengcy.generator.code2sql.worker;

import com.mengcy.generator.code2sql.config.GeneratorConfig;
import com.mengcy.generator.code2sql.scan.EntityScanner;
import org.springframework.beans.factory.config.BeanDefinition;

import java.util.Set;

/**
 * @author mengcy 2019/9/10
 */
public abstract class BaseTableGenerator {

    public void generate(GeneratorConfig config){

        if(GeneratorConfig.AUTO_NONE.equals(config.getDdlAuto())){
            // do nothing
            return;
        }

        EntityScanner scanner = new EntityScanner();
        Set<BeanDefinition> beans = scanner.scanAnnotationEntity(config.getModelScan());

        BaseTableGenerator tableGenerator = new MysqlTableGenerator();
        tableGenerator.generateTable(config, beans);
    }

    public abstract void generateTable(GeneratorConfig config, Set<BeanDefinition> beans);
}
