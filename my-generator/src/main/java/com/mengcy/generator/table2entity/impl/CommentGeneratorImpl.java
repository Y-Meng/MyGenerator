package com.mengcy.generator.table2entity.impl;

import com.mysql.jdbc.StringUtils;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.util.StringUtility;
import org.omg.CORBA.CODESET_INCOMPATIBLE;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author mengcy
 * @date 2019/4/23
 * JPA 注解兼容 Java Persistence API
 * JPA 注解使用参考 https://www.objectdb.com/api/java/jpa/annotations
 */
public class CommentGeneratorImpl implements CommentGenerator {

    private static final String DATE_TYPE = "java.util.Date";

    private static final String ANNOTATION_DEFAULT = "default";
    private static final String ANNOTATION_ORM = "jpa";

    private Properties properties = new Properties();
    private boolean suppressDate = false;
    private boolean suppressDateFormat = false;
    private boolean suppressAllComments = false;
    private boolean suppressAnnotation = true;
    private String annotationType = ANNOTATION_DEFAULT;
    private String currentDateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

    public CommentGeneratorImpl() {
    }

    /**
     * 配置初始化
     *
     * @param properties
     */
    @Override
    public void addConfigurationProperties(Properties properties) {
        this.properties.putAll(properties);
        this.suppressDate = StringUtility.isTrue(properties.getProperty("suppressDate"));
        this.suppressDateFormat = StringUtility.isTrue(properties.getProperty("suppressDateFormat"));
        this.suppressAllComments = StringUtility.isTrue(properties.getProperty("suppressAllComments"));
        this.suppressAnnotation = StringUtility.isTrue(properties.getProperty("suppressAnnotation"));
        this.annotationType = properties.getProperty("annotationType");
    }

    /**
     * 添加属性配置
     *
     * @param field
     * @param introspectedTable
     * @param introspectedColumn
     */
    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        if (!suppressAllComments) {
            addFieldJavaDoc(field, introspectedTable, introspectedColumn);
        }

        if (!suppressAnnotation) {
            addFieldAnnotation(field, introspectedTable, introspectedColumn);
        }

        if (!suppressDateFormat) {
            if (DATE_TYPE.equals(field.getType().getFullyQualifiedName())) {
                addDateFormatAnnotation(field, introspectedColumn);
            }
        }
    }

    private void addDateFormatAnnotation(Field field, IntrospectedColumn column) {

        int jdbcType = column.getJdbcType();
        switch (jdbcType){
            case Types.DATE:
                field.addAnnotation("@DateTimeFormat(pattern = \"yyyy-MM-dd\")");
                field.addAnnotation("@JsonFormat(pattern = \"yyyy-MM-dd\", timezone=\"GMT+8\")");
                break;
            case Types.TIME:
                field.addAnnotation("@DateTimeFormat(pattern = \"HH:mm:ss\")");
                field.addAnnotation("@JsonFormat(pattern = \"HH:mm:ss\", timezone=\"GMT+8\")");
                break;
            default:
                field.addAnnotation("@DateTimeFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")");
                field.addAnnotation("@JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\", timezone=\"GMT+8\")");
        }

    }

    private void addFieldAnnotation(Field field, IntrospectedTable table, IntrospectedColumn column) {

        if(ANNOTATION_ORM.equals(annotationType)) {
            // orm注解
            if (column.isIdentity() || primaryCheck(table, column)) {
                // 主键
                StringBuilder builder = new StringBuilder("@Id");
                field.addAnnotation(builder.toString());
                if (column.isAutoIncrement()) {
                    field.addAnnotation("@GeneratedValue(strategy=GenerationType.IDENTITY)");
                } else {
                    field.addAnnotation("@GeneratedValue");
                }

                if (!field.getName().equals(column.getActualColumnName())) {
                    field.addAnnotation("@Column(name = \"" + column.getActualColumnName() + "\")");
                }
            } else {
                // 普通字段
                StringBuilder builder = new StringBuilder("@Column(");
                builder.append("name = \"").append(column.getActualColumnName()).append("\"");

                int jdbcType = column.getJdbcType();
                if (jdbcType == Types.LONGVARCHAR
                        || jdbcType == Types.DATE
                        || jdbcType == Types.TIME
                        || column.getDefaultValue() != null) {

                    // text、date、time 类型或有默认值 使用 columnDefinition
                    StringBuilder definition = new StringBuilder();
                    switch (jdbcType) {
                        case Types.LONGVARCHAR:
                            int len = column.getLength();
                            switch (len) {
                                case 65535: // 2^16 - 1
                                    definition.append("text");
                                    break;
                                case 16777215: // 2^24 - 1
                                    definition.append("mediumtext");
                                    break;
                                case 2147483647: // 2^32 - 1
                                    definition.append("longtext");
                                    break;
                                default:
                                    definition.append("text");
                                    break;
                            }
                            break;
                        case Types.DATE:
                            definition.append("date");
                            break;
                        case Types.TIME:
                            definition.append("time");
                            break;
                        case Types.BIT:
                            definition.append("bit");
                            break;
                        case Types.TINYINT:
                            definition.append("tinyint");
                            break;
                        case Types.SMALLINT:
                            definition.append("smallint");
                            break;
                        case Types.INTEGER:
                            definition.append("int");
                            break;
                        case Types.BIGINT:
                            definition.append("bigint");
                            break;
                        case Types.FLOAT:
                            if (column.getLength() > 0 && column.getScale() > 0) {
                                definition.append("float(").append(column.getLength()).append(",").append(column.getScale()).append(")");
                            } else {
                                definition.append("float");
                            }
                            break;
                        case Types.DOUBLE:
                            if (column.getLength() > 0 && column.getScale() > 0) {
                                definition.append("double(").append(column.getLength()).append(",").append(column.getScale()).append(")");
                            } else {
                                definition.append("double");
                            }
                            break;
                        case Types.DECIMAL:
                            if (column.getLength() > 0 && column.getScale() > 0) {
                                definition.append("decimal(").append(column.getLength()).append(",").append(column.getScale()).append(")");
                            } else {
                                definition.append("decimal");
                            }
                            break;
                        case Types.CHAR:
                            definition.append("char(").append(column.getLength()).append(")");
                            break;
                        case Types.VARCHAR:
                            definition.append("varchar(").append(column.getLength()).append(")");
                            break;
                        default:
                            definition.append(column.getJdbcTypeName().toLowerCase());
                            break;
                    }
                    if (!column.isNullable()) {
                        definition.append(" NOT NULL");
                    }
                    if (column.getDefaultValue() != null) {
                        if (column.getDefaultValue().equals("CURRENT_TIMESTAMP")) {
                            definition.append(" DEFAULT ").append(column.getDefaultValue());
                        } else {
                            definition.append(" DEFAULT '").append(column.getDefaultValue()).append("'");
                        }
                    }

                    builder.append(", columnDefinition=\"").append(definition.toString()).append("\"");
                } else {
                    if (!column.isNullable()) {
                        builder.append(", nullable = ").append(column.isNullable());
                    }

                    if (jdbcType == Types.VARCHAR || jdbcType == Types.CHAR) {
                        builder.append(", length = ").append(column.getLength());
                    }

                    if (jdbcType == Types.FLOAT || jdbcType == Types.DOUBLE || jdbcType == Types.DECIMAL) {
                        builder.append(", length = ").append(column.getLength());
                        builder.append(", scale = ").append(column.getScale());
                    }
                }

                builder.append(")");
                field.addAnnotation(builder.toString());
            }
        }
    }

    private boolean primaryCheck(IntrospectedTable table, IntrospectedColumn column) {
        List<IntrospectedColumn> primaryKeys = table.getPrimaryKeyColumns();
        if(primaryKeys != null){
            for (IntrospectedColumn primaryKey : primaryKeys) {
                if(primaryKey.getActualColumnName().equals(column.getActualColumnName())){
                    return true;
                }
            }
        }

        return false;
    }

    private void addFieldJavaDoc(Field field, IntrospectedTable table, IntrospectedColumn column) {
        if (!StringUtils.isNullOrEmpty(column.getRemarks())) {
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
        if (!suppressAllComments) {
            addClassJavaDoc(topLevelClass, introspectedTable);
        }

        if (!suppressAnnotation) {
            addClassImportOrmType(topLevelClass);
            addClassAnnotation(topLevelClass, introspectedTable);
        }

        if (!suppressDateFormat) {
            addClassImportDateFormatType(topLevelClass);
        }
    }

    private void addClassImportDateFormatType(TopLevelClass clz) {
        clz.addImportedType("com.fasterxml.jackson.annotation.JsonFormat");
        clz.addImportedType("org.springframework.format.annotation.DateTimeFormat");
    }

    private void addClassImportOrmType(TopLevelClass clz) {
        if (ANNOTATION_ORM.equals(annotationType)) {
            clz.addImportedType("javax.persistence.*");
        }
    }

    private void addClassAnnotation(TopLevelClass clz, IntrospectedTable table) {
        if (ANNOTATION_ORM.equals(annotationType)) {
            clz.addAnnotation("@Entity");
            clz.addAnnotation("@Table(name = \"" + table.getTableConfiguration().getTableName() + "\")");
        }
    }

    private void addClassJavaDoc(TopLevelClass clz, IntrospectedTable table) {
        clz.addJavaDocLine("/**");
        if (table.getRemarks() != null && table.getRemarks().length() > 0) {
            clz.addJavaDocLine(" * " + table.getRemarks());
        }
        if (!suppressDate) {
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
