package com.mengcy.generator.code2sql.scan;

import com.mengcy.generator.annotation.DataTable;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
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
        provider.addIncludeFilter(new AnnotationTypeFilter(DataTable.class));
        Set<BeanDefinition> beanDefinitionSet = provider.findCandidateComponents(basePackage);
        return beanDefinitionSet;
    }
}
