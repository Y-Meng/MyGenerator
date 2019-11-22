package com.mengcy.generator.code2sql.config;

/**
 * @author mengcy 2019/9/10
 */
public class GeneratorConfig {

    public static final String AUTO_NONE = "none";
    public static final String AUTO_CREATE = "create";
    public static final String AUTO_UPDATE = "update";

    /**
     * 生成表类型：update表示更新，create表示删除原表重新创建，none表示不作任何事
     * */
    private String ddlAuto = AUTO_UPDATE;

    /** 包扫描路径 */
    private String modelScan;

    private String dbUrl;

    private String dbUser;

    private String dbPwd;

    public String getDdlAuto() {
        return ddlAuto;
    }

    public void setDdlAuto(String ddlAuto) {
        this.ddlAuto = ddlAuto;
    }

    public String getModelScan() {
        return modelScan;
    }

    public void setModelScan(String modelScan) {
        this.modelScan = modelScan;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPwd() {
        return dbPwd;
    }

    public void setDbPwd(String dbPwd) {
        this.dbPwd = dbPwd;
    }
}
