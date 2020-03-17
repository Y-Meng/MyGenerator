package com.mengcy.generator.tpl2code.config;

import com.mengcy.generator.util.PropertiesUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zkzc-mcy on 2017/11/7.
 */
public class CodeGenConfig {

    private static final String FALSE = "false";

    private String jdbcDriver ;
    private String jdbcUrl;
    private String jdbcUser;
    private String jdbcPassword;

    private String templateRoot;
    private Boolean enableMapper = true;
    private String exportRoot;

    private String moduleName ;
    private String moduleDatabase;
    private String moduleTableName;
    private String modulePackageName;

    private String modelTrimPrefix;
    private String modelFieldTrimPrefix;

    /**
     * 需要insert后返回主键的表配置，key:表名,value:主键名
     */
    private  Map<String, String> lastInsertIdTables = new HashMap<>();

    private CodeGenConfig(){}

    public static CodeGenConfig init(){
        CodeGenConfig config = new CodeGenConfig();
        PropertiesUtil util = PropertiesUtil.getInstance("generator");
        config.setModuleName(util.get("module.name"));
        config.setModuleDatabase(util.get("module.database"));
        config.setModuleTableName(util.get("module.table.name"));
        config.setModulePackageName(util.get("module.package.name"));

        config.setJdbcDriver(util.get("generator.jdbc.driver"));
        config.setJdbcUrl(util.get("generator.jdbc.url"));
        config.setJdbcUser(util.get("generator.jdbc.username"));
        config.setJdbcPassword(util.get("generator.jdbc.password"));

        config.setTemplateRoot("/" + util.get("generator.template.root"));

        // 输出根目录
        String exportRoot = util.get("generator.export.root");
        if(exportRoot == null || "".equals(exportRoot)){
            String basePath = CodeGenConfig.class.getResource("/").getPath();
            if(basePath.contains("target")){
                basePath = basePath.split("/target")[0] + "/target/";
            }
            exportRoot = basePath;
        }
        config.setExportRoot(exportRoot);

        String enable = util.get("generator.enable.mapper");
        if(FALSE.equals(enable)){
            config.setEnableMapper(false);
        }

        config.setModelTrimPrefix(util.get("model.trim.prefix"));
        config.setModelFieldTrimPrefix(util.get("model.field.trim.prefix"));

        // 初始化insert返回主键配置
        config.getLastInsertIdTables().put("*", "id");

        return config;
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getJdbcUser() {
        return jdbcUser;
    }

    public void setJdbcUser(String jdbcUser) {
        this.jdbcUser = jdbcUser;
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }

    public void setJdbcPassword(String jdbcPassword) {
        this.jdbcPassword = jdbcPassword;
    }

    public String getTemplateRoot() {
        return templateRoot;
    }

    public void setTemplateRoot(String templateRoot) {
        this.templateRoot = templateRoot;
    }

    public Boolean getEnableMapper() {
        return enableMapper;
    }

    public void setEnableMapper(Boolean enableMapper) {
        this.enableMapper = enableMapper;
    }

    public String getExportRoot() {
        return exportRoot;
    }

    public void setExportRoot(String exportRoot) {
        this.exportRoot = exportRoot;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleDatabase() {
        return moduleDatabase;
    }

    public void setModuleDatabase(String moduleDatabase) {
        this.moduleDatabase = moduleDatabase;
    }

    public String getModuleTableName() {
        return moduleTableName;
    }

    public void setModuleTableName(String moduleTableName) {
        this.moduleTableName = moduleTableName;
    }

    public String getModulePackageName() {
        return modulePackageName;
    }

    public void setModulePackageName(String modulePackageName) {
        this.modulePackageName = modulePackageName;
    }

    public Map<String, String> getLastInsertIdTables() {
        return lastInsertIdTables;
    }

    public void setLastInsertIdTables(Map<String, String> lastInsertIdTables) {
        this.lastInsertIdTables = lastInsertIdTables;
    }

    public String getModelTrimPrefix() {
        return modelTrimPrefix;
    }

    public void setModelTrimPrefix(String modelTrimPrefix) {
        this.modelTrimPrefix = modelTrimPrefix;
    }

    public String getModelFieldTrimPrefix() {
        return modelFieldTrimPrefix;
    }

    public void setModelFieldTrimPrefix(String modelFieldTrimPrefix) {
        this.modelFieldTrimPrefix = modelFieldTrimPrefix;
    }
}
