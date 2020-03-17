package com.mengcy.demo.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

/**
 * generate at 2020-02-15 14:43:34
 */
@Table(name = "t_entity")
public class Entity {
    /**
     * id字段
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    /**
     * short类型
     */
    @Column(name = "short_value", columnDefinition="smallint DEFAULT '1'")
    private Short shortValue;

    @Column(name = "short_type")
    private Short shortType;

    @Column(name = "int_value")
    private Integer intValue;

    @Column(name = "int_type")
    private Integer intType;

    @Column(name = "long_value", columnDefinition="bigint DEFAULT '0'")
    private Long longValue;

    @Column(name = "long_type", columnDefinition="bigint DEFAULT '0'")
    private Long longType;

    @Column(name = "double_value", length = 11, scale = 2)
    private Double doubleValue;

    @Column(name = "double_type")
    private Double doubleType;

    /**
     * 姓名
     */
    @Column(name = "name", nullable = false, length = 1000)
    private String name;

    @Column(name = "createTime", columnDefinition="timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private Date createtime;

    @Column(name = "updateTime", columnDefinition="timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'")
    private Date updatetime;

    @Column(name = "dateValue", columnDefinition="date NOT NULL")
    private Date datevalue;

    @Column(name = "timeValue", columnDefinition="time NOT NULL")
    private Date timevalue;

    @Column(name = "date_time")
    private Date dateTime;

    @Column(name = "bit_decimal", length = 10, scale = 2)
    private BigDecimal bitDecimal;

    @Column(name = "content", columnDefinition="longtext")
    private String content;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Short getShortValue() {
        return shortValue;
    }

    public void setShortValue(Short shortValue) {
        this.shortValue = shortValue;
    }

    public Short getShortType() {
        return shortType;
    }

    public void setShortType(Short shortType) {
        this.shortType = shortType;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }

    public Integer getIntType() {
        return intType;
    }

    public void setIntType(Integer intType) {
        this.intType = intType;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public Long getLongType() {
        return longType;
    }

    public void setLongType(Long longType) {
        this.longType = longType;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public Double getDoubleType() {
        return doubleType;
    }

    public void setDoubleType(Double doubleType) {
        this.doubleType = doubleType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public Date getDatevalue() {
        return datevalue;
    }

    public void setDatevalue(Date datevalue) {
        this.datevalue = datevalue;
    }

    public Date getTimevalue() {
        return timevalue;
    }

    public void setTimevalue(Date timevalue) {
        this.timevalue = timevalue;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public BigDecimal getBitDecimal() {
        return bitDecimal;
    }

    public void setBitDecimal(BigDecimal bitDecimal) {
        this.bitDecimal = bitDecimal;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}