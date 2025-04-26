package com.middle.wcs.config.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * @classDesc: 实体类:(CssConfig)
 * @author: makejava
 * @date: 2023-07-04 15:31:49
 * @copyright 作者
 */
@Data
@TableName("css_config")
public class PcsConfig {
    /**
    * 主键
    */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long configId;

    /**
    * 连接plcip地址
    */    
    private String ccsIp;
    
}
