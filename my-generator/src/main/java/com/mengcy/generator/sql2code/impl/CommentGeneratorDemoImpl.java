package com.mengcy.generator.sql2code.impl;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.XmlElement;
import java.util.Properties;

/**
 * @author mengcy
 * @date 2019/4/23
 */
public class CommentGeneratorDemoImpl implements CommentGenerator{


    public CommentGeneratorDemoImpl(){}

    /**
     * 配置初始化
     * @param properties
     */
    @Override
    public void addConfigurationProperties(Properties properties) {

    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        // 生成 Entity.class 属性以及get,set方法调用
        System.out.println("addFieldComment(field, introspectedTable, introspectedColumn)-" + field.getName());
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
        // 生成 Example.class 属性以及get,set方法调用
        System.out.println("addFieldComment(field, introspectedTable)-" + field.getName());
    }

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // 生成 Entity.class 调用
        System.out.println("addModelClassComment(topLevelClass, introspectedTable)-" + topLevelClass.getType());
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
        // 生成 Example.class 内部类 GeneratedCriteria，Criteria 调用
        System.out.println("addClassComment(innerClass, introspectedTable)-" + innerClass.getType());
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean b) {
        // 生成 Example.class 内部类 Criteria 调用
        System.out.println("addClassComment(innerClass, introspectedTable, b)-" + innerClass.getType());
    }

    @Override
    public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
        // 没有调用
        System.out.println("addEnumComment(innerEnum, IntrospectedTable, introspectedTable)-" + innerEnum.toString());
    }

    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        // 生成 Entity.class 属性getter方法调用
        System.out.println("addGetterComment(method, introspectedTable, introspectedColumn)-" + method.getName());
    }

    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        // 生成 Entity.class 属性setter方法调用
        System.out.println("addSetterComment(method, introspectedTable, introspectedColumn)-" + method.getName());
    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        // 生成 Mapper.class 调用
        System.out.println("addGeneralMethodComment(method, introspectedTable, introspectedColumn)-" + method.getName());
    }

    @Override
    public void addJavaFileComment(CompilationUnit compilationUnit) {
        // 生成 Entity.java,Mapper.java,Example.java 文件时调用
        System.out.println("addJavaFileComment(compilationUnit)-" + compilationUnit.getType());
    }

    @Override
    public void addComment(XmlElement xmlElement) {
        // 生成 Mapper.xml 调用
        System.out.println("addComment(xmlElement)-" + xmlElement.getName());
    }

    @Override
    public void addRootComment(XmlElement xmlElement) {
        // 生成 Mapper.xml 调用
        System.out.println("addRootComment(xmlElement)-" + xmlElement.getName());
    }
}
