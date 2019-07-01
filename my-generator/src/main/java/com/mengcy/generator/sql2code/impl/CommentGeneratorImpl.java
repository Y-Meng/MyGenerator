package com.mengcy.generator.sql2code.impl;

import com.mengcy.generator.util.JdbcTypeParser;
import com.mysql.jdbc.StringUtils;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.util.StringUtility;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

/**
 * @author mengcy
 * @date 2019/4/23
 * JPA 注解兼容 Java Persistence API
 * JPA 注解使用参考 https://www.objectdb.com/api/java/jpa/annotations
 */
public class CommentGeneratorImpl implements CommentGenerator{

    private static final String DATE_TYPE = "java.util.Date";
    private static final String ANNOTATION_DEFAULT = "default";
    private static final String ANNOTATION_JPA = "jpa";

    private Properties properties = new Properties();
    private Properties systemPro = System.getProperties();
    private boolean suppressDate = false;
    private boolean suppressDateFormat = false;
    private boolean suppressAllComments = false;
    private boolean suppressAnnotation = true;
    private String annotationType = ANNOTATION_DEFAULT;
    private String currentDateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

    public CommentGeneratorImpl(){}

    /**
     * 配置初始化
     * @param properties
     */
    @Override
    public void addConfigurationProperties(Properties properties) {
        this.properties.putAll(properties);
        this.suppressDate = StringUtility.isTrue(properties.getProperty("suppressDate"));
        this.suppressDateFormat = StringUtility.isTrue(properties.getProperty("suppressDateFormat"));
        this.suppressAllComments = StringUtility.isTrue(properties.getProperty("suppressAllComments"));
        this.suppressAnnotation = StringUtility.isTrue(properties.getProperty("suppressAnnotation"));
    }

    /**
     * 添加属性配置
     * @param field
     * @param introspectedTable
     * @param introspectedColumn
     */
    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        if(!suppressAllComments){
            addFieldJavaDoc(field, introspectedTable, introspectedColumn);
        }

        if(!suppressAnnotation){
            addFieldAnnotation(field, introspectedTable, introspectedColumn);
        }

        if(!suppressDateFormat) {
            if (DATE_TYPE.equals(field.getType().getFullyQualifiedName())) {
                addDateFormatAnnotation(field);
            }
        }
    }

    private void addDateFormatAnnotation(Field field) {
        field.addAnnotation("@DateTimeFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")");
        field.addAnnotation("@JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\", timezone=\"GMT+8\")");
    }

    private void addFieldAnnotation(Field field, IntrospectedTable table, IntrospectedColumn column) {

        if(ANNOTATION_JPA.equals(annotationType)) {
            // jpa注解
            if (column.isIdentity()) {
                // 主键
                StringBuilder builder = new StringBuilder("@Id(");
                builder.append("name = \"").append(column.getActualColumnName()).append("\"");
                builder.append(", length = ").append(column.getLength());
                builder.append(")");
                field.addAnnotation(builder.toString());
                if(column.isAutoIncrement()){
                    field.addAnnotation("@GenerateValue");
                }
            } else {
                // 普通字段
                StringBuilder builder = new StringBuilder("@Column(");
                builder.append("name = \"").append(column.getActualColumnName()).append("\"");
                builder.append(", length = ").append(column.getLength());
                builder.append(", nullable = ").append(column.isNullable());
                builder.append(")");
                field.addAnnotation(builder.toString());
            }
        }else {
            // 默认自定义注解
            if(column.isIdentity()){
                StringBuilder builder = new StringBuilder("@DataId(");

                builder.append(")");
                field.addAnnotation(builder.toString());
            }else {
                StringBuilder builder = new StringBuilder("@DataColumn(");
                builder.append("name = \"").append(column.getActualColumnName()).append("\"");

                builder.append(", type = \"").append(JdbcTypeParser.parseDataType(column)).append("\"");

                if (column.getLength() > 0) {
                    builder.append(", length = ").append(column.getLength());
                }

                builder.append(", nullable = \"").append(column.isNullable()).append("\"");

                if (!StringUtils.isNullOrEmpty(column.getRemarks())) {
                    builder.append(", comment = \"").append(column.getRemarks()).append("\"");
                }

                builder.append(")");
                field.addAnnotation(builder.toString());
            }
        }
    }

    private void addFieldJavaDoc(Field field, IntrospectedTable table, IntrospectedColumn column) {
        if(!StringUtils.isNullOrEmpty(column.getRemarks())) {
            field.addJavaDocLine("/**");
            field.addJavaDocLine(" * " + column.getRemarks());
            field.addJavaDocLine(" */");
        }
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {

    }

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if(!suppressAllComments) {
            addClassJavaDoc(topLevelClass, introspectedTable);
        }

        if(!suppressAnnotation) {
            addClassImportOrmType(topLevelClass);
            addClassAnnotation(topLevelClass, introspectedTable);
        }

        if(!suppressDateFormat){
            addClassImportDateFormatType(topLevelClass);
        }
    }

    private void addClassImportDateFormatType(TopLevelClass clz) {
        clz.addImportedType("com.fasterxml.jackson.annotation.JsonFormat");
        clz.addImportedType("org.springframework.format.annotation.DateTimeFormat");
    }

    private void addClassImportOrmType(TopLevelClass clz) {
        if(ANNOTATION_JPA.equals(annotationType)) {
            clz.addImportedType("javax.persistence.Table");
            clz.addImportedType("javax.persistence.Id");
            clz.addImportedType("javax.persistence.Column");
        }else {
            clz.addImportedType("com.mengcy.generator.annotation.DataTable");
            clz.addImportedType("com.mengcy.generator.annotation.DataColumn");
            clz.addImportedType("com.mengcy.generator.annotation.DataType");
        }
    }

    private void addClassAnnotation(TopLevelClass clz, IntrospectedTable table) {
        if(ANNOTATION_JPA.equals(annotationType)) {
            clz.addAnnotation("@Table(name = " + table.getTableConfiguration().getTableName() + ")");
        }else {
            clz.addAnnotation("@DataTable(name = " + table.getTableConfiguration().getTableName() + ")");
        }
    }

    private void addClassJavaDoc(TopLevelClass clz, IntrospectedTable table) {
        clz.addJavaDocLine("/**");
        if(table.getRemarks() != null && table.getRemarks().length() > 0) {
            clz.addJavaDocLine(" * " + table.getRemarks());
        }
        if(!suppressDate){
            clz.addJavaDocLine(" * generate at " + currentDateStr);
        }
        clz.addJavaDocLine(" */");
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {

    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean b) {

    }

    @Override
    public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {

    }

    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {

    }

    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {

    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {

    }

    @Override
    public void addJavaFileComment(CompilationUnit compilationUnit) {

    }

    @Override
    public void addComment(XmlElement xmlElement) {

    }

    @Override
    public void addRootComment(XmlElement xmlElement) {

    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {

    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> set) {

    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {

    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> set) {

    }

    @Override
    public void addClassAnnotation(InnerClass innerClass, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {

    }
}
