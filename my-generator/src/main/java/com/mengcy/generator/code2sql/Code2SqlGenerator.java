package com.mengcy.generator.code2sql;

import com.mengcy.generator.code2sql.scan.EntityScanner;
import org.springframework.beans.factory.config.BeanDefinition;

import java.util.Set;

/**
 * @author mengcy
 * @date 2019/4/25
 */
public class Code2SqlGenerator {

    public static void main(String[] args){
        EntityScanner scanner = new EntityScanner();
        Set<BeanDefinition> beans = scanner.scanAnnotationedEntity("com.mengcy.generator");
        beans.forEach(item -> System.out.println(item.getBeanClassName()));
    }
}
