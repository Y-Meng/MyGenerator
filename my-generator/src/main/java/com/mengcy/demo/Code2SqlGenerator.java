package com.mengcy.demo;

import com.mengcy.generator.code2sql.config.GeneratorConfig;
import com.mengcy.generator.code2sql.worker.MysqlTableGenerator;

/**
 * @author mengcy
 * @date 2019/4/25
 */
public class Code2SqlGenerator {

    public static void main(String[] args){

        GeneratorConfig config = new GeneratorConfig();

        config.setModelScan("com.mengcy");
        config.setDdlAuto("create");

        MysqlTableGenerator generator = new MysqlTableGenerator();
        generator.generate(config);
    }
}
