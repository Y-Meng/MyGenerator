package com.mengcy.generator.entity2table.handler;

import com.mengcy.generator.entity2table.config.GeneratorConfig;
import com.mengcy.generator.entity2table.handler.mysql.MysqlTableHandler;
import com.mengcy.generator.entity2table.scan.EntityScanner;
import org.springframework.beans.factory.config.BeanDefinition;

import java.util.Set;

/**
 * @author mengcy 2019/9/10
 */
public abstract class BaseTableHandler {

    public void generate(GeneratorConfig config){

        if(GeneratorConfig.AUTO_NONE.equals(config.getDdlAuto())){
            // do nothing
            return;
        }

        EntityScanner scanner = new EntityScanner();
        Set<BeanDefinition> beans = scanner.scanAnnotationEntity(config.getModelScan());

        BaseTableHandler tableGenerator = new MysqlTableHandler();
        tableGenerator.generateTable(config, beans);
    }

    public abstract void generateTable(GeneratorConfig config, Set<BeanDefinition> beans);
}
