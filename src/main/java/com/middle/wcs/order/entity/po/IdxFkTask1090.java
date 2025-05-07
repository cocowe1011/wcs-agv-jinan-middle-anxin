package com.middle.wcs.order.entity.po;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * (IdxFkTask1090)实体类
 *
 * @author makejava
 * @since 2025-04-26 15:34:41
 */
@Data
@TableName("IDX_FK_TASK_1090")
public class IdxFkTask1090 {
    /**
    * 流水号
    */
    @TableId
    private Long uuid;

    /**
    * 物料编码
    */
    private String sku;

    /**
    * 物料描述
    */
    private String descrC;

    /**
    * 生产日期
    */
    private String lotatt01;

    /**
    * 失效日期
    */
    private String lotatt02;

    /**
    * 入库日期
    */
    private String column3;

    /**
    * 库存批号
    */
    private String column4;

    /**
    * 复验日期
    */
    private String column5;

    /**
    * 件数
    */
    private String column6;

    /**
    * 包装规格
    */
    private String column7;

    /**
    * 质量状态
    */
    private String column8;

    /**
    * 托盘类型（1单物料，2拼盘）
    */
    private String type;

    /**
    * 托盘号
    */
    private String traceid;

    /**
    * 下发时间
    */
    private Date column1;

    /**
    * 目标ID
    */
    private String mudidi;

    /**
    * 状态(N未使用Y已使用)
    */
    private String zt;

    /**
    * 车间
    */
    private String cheijian;

    /**
    * 生产厂家批号
    */
    private String column9;

    /**
    * 检验批号
    */
    private String column11;
}
