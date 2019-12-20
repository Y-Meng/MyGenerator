package com.mengcy.demo.entity;


import com.mengcy.generator.annotation.Comment;

import javax.persistence.*;

/**
 * @author mengcy
 * @date 2019/6/27
 */
@Table(name = "t_entity")
@Comment("数据库生成注释表")
public class Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Comment("id字段")
    private Integer id;

    @Column(name = "name", nullable = false)
    @Comment("姓名")
    private String name;



}
