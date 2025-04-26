package com.middle.wcs.config.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @classDesc: 实体类:(CssConfig)
 * @author: makejava
 * @date: 2023-07-04 15:31:49
 * @copyright 作者
 */
@Data
@TableName("css_config")
public class CssConfig {
    /**
    * 主键
    */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long configId;

    /**
    * 连接plcip地址
    */    
    private String plcIp;
                
    /**
    * 连接plc端口
    */    
    private String plcPort;
                
    /**
    * L11长度
    */    
    private String oneOneLength;
                
    /**
    * l2长度
    */    
    private String twoLength;
                
    /**
    * 判断禁止上货点位
    */    
    private String judgeLoadPoint;

    /**
     * J
     */
    private BigDecimal pointjLength;

    /**
     * K
     */
    private BigDecimal pointkLength;

    /**
     * L
     */
    private BigDecimal pointlLength;

    /**
     * V1比例系数
     */
    private BigDecimal speedOne;

    /**
     * V2比例系数
     */
    private BigDecimal speedTwo;

    /**
     * X1长度
     */
    private BigDecimal lengthOne;

    /**
     * X2长度
     */
    private BigDecimal lengthTwo;

    /**
     * 延迟时间
     */
    private Integer newDelayPointTime;


    /**
     * 中英文配置
     */
    private String languageSet;

}
