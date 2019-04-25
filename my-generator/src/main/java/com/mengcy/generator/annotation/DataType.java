package com.mengcy.generator.annotation;

/**
 * @author mengcy
 * @date 2019/4/25
 */
public enum DataType {

    MYSQL_TININT("tinint", 4);

    public String name;
    public int length;
    DataType(String name, int length){
        this.name = name;
        this.length = length;
    }
}
