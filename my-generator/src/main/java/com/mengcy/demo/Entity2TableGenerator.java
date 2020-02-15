package com.mengcy.demo;

import com.mengcy.generator.entity2table.config.GeneratorConfig;
import com.mengcy.generator.entity2table.handler.mysql.MysqlTableHandler;

/**
 * @author mengcy
 * @date 2019/4/25
 */
public class Entity2TableGenerator {

    public static void main(String[] args){

        GeneratorConfig config = new GeneratorConfig();

        config.setDbUrl("jdbc:mysql://localhost:3306/fdp_test");
        config.setDbUser("marin");
        config.setDbPwd("marin110");
        config.setModelScan("com.mengcy");
        config.setDdlAuto("update");

        MysqlTableHandler generator = new MysqlTableHandler();
        generator.generate(config);
    }
}
