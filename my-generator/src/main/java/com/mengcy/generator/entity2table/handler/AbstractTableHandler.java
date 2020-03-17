package com.mengcy.generator.entity2table.handler;

import com.mengcy.generator.entity2table.config.TableGenConfig;
import com.mengcy.generator.entity2table.scan.EntityScanner;
import org.springframework.beans.factory.config.BeanDefinition;

import java.util.Set;

/**
 * @author mengcy 2019/9/10
 */
public abstract class AbstractTableHandler {

    public void generate(TableGenConfig config){

        if(TableGenConfig.AUTO_NONE.equals(config.getDdlAuto())){
            // do nothing
            return;
        }

        // 扫描实体类
        EntityScanner scanner = new EntityScanner();
        Set<BeanDefinition> beans = scanner.scanAnnotationEntity(config.getModelScan());

        // 生成数据表
        generateTable(config, beans);
    }

    protected abstract void generateTable(TableGenConfig config, Set<BeanDefinition> beans);
}
