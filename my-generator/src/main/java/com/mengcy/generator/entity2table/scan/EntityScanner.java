package com.mengcy.generator.entity2table.scan;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import javax.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author mengcy
 * @date 2019/6/27
 */
public class EntityScanner {

    public Set<BeanDefinition> scanAnnotationEntity(String basePackage){

        ClassPathScanningCandidateComponentProvider provider =
                new ClassPathScanningCandidateComponentProvider(false);

        // 不使用默认的TypeFilter
        provider.addIncludeFilter(new AnnotationTypeFilter(Table.class));

        String[] packages = basePackage.split(",");
        Set<BeanDefinition> beanDefinitionSet = new LinkedHashSet<>();
        for(String pack : packages) {
            Set<BeanDefinition> beans = provider.findCandidateComponents(pack);
            beanDefinitionSet.addAll(beans);
        }
        return beanDefinitionSet;
    }
}
