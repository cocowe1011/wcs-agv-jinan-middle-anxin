package com.middle.wcs.order.entity.dto;
import lombok.Data;
/**
 * 自定义扩展字段
 * 机器人任务请求接口入参
 */

@Data
public class Values {
    /**
     * 地图编号
     */
    private String mapCode;
    /**
     * 存储类型，枚举值：
     * BIN
     * 仓位
     * SITE
     * 站点
     */
    private String slotCategory;
    /**
     * 当前站点编号
     */
    private String slotCode;
    /**
     * 站点别名
     * 1.走出储位：起点
     * 2. 任务完成：目标点
     */
    private String slotName;
    /**
     * 机器人当前位置 x 坐标
     */
    private String x;
    /**
     * 机器人当前位置 y 坐标
     */
    private String y;
    /**
     * 任务执行过程中消息上报的方法名
     * 默认使用方式:
     * start : 任务开始
     * outbin : 走出储位
     * end : 任务完成
     */
    private String method;
    /**
     * 载具种类
     */
    private String carrierCategory;
    /**
     * 载具类型
     */
    private String carrierType;
    /**
     * 载具编号
     */
    private String carrierCode;
    private Integer pileCount;
    private String orgCode;
    /**
     * 机器人种类
     */
    private String amrCategory;
    /**
     * 机器人类型
     */
    private String amrType;
    private String amrCode;
    /**
     * 载具名称
     */
    private String carrierName;
    /**
     * 载具角度
     */
    private String carrierDir;
    private Integer layerNo;
}
