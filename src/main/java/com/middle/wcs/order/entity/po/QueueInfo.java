package com.middle.wcs.order.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

/**
 * (QueueInfo)实体类
 *
 * @author makejava
 * @since 2025-01-01 12:44:45
 */
@Data
@TableName("queue_info")
public class QueueInfo {
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
    * 队列名字
    */
    private String queueName;

    /**
    * 队列信息
    */
    private String trayInfo;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * queue_num 队列序号
     */
    private String queueNum;
    /**
     * tray_status 托盘状态 状态，''无托盘
     * 0-在2800等待AGV取货
     * 1-已在2800取货，正往缓存区运送
     * 2-已送至2楼缓存区
     * 20-缓存区到缓存区命令状态-在缓存区等待取货
     * 21-缓存区到缓存区命令状态-已在缓存区取货
     * 22-缓存区到缓存区命令状态-已送至目的地终点
     * 23-缓存区到机械臂区域命令状态-在缓存区等待取货
     * 24-缓存区到机械臂区域命令状态-已在缓存区取货
     * 25-缓存区到机械臂区域命令状态-已送至目的地终点（a1、b1、d1、e1、a2、b1、d2）
     * 3-在缓存区等待AGV取货
     * 4-已在缓存区取货，正往运往目的地
     * 5-已送至2楼目的地
     * 6-等待一/三楼AGV取货
     * 7-AGV已在一/三楼 AGV1-1/AGV3-1取货，正运往目的地
     * 8-在c1 c2等待AGV取货
     * 9-AGV已在c1 c2取货，正运往目的地
     * 10-已经给PLC发送过送货完指令
     * 11-在机械臂位置等待AGV取货-运往空托盘区域
     * 12-AGV已在机械臂位置取货，正运往空托盘区域
     * 13-已送至空托盘区域
     * 14-已经给PLC发送过清理托盘/杂物完成命令
     * 15-本空托盘缓存货位已集满
     * 16-空托盘处理命令已发送，正在等待AGV取货
     * 17-AGV已在空托盘缓存区取货，正运往目的地
     * 18-空托盘已经送输送线
     * 19-杂物托盘AGV已送至输送线
     */
    /**
     * 托盘状态 tray_status 状态
     * '' 2500车间，说明托盘在来料缓存区进行缓存
     * 0- 在AGV5-1等待取货
     * 1- 已在AGV5-1取货，正运往来料缓存区
     * 2- 已送至来料缓存区
     * 3- 在来料缓存区等待取货
     * 4- 已在来料缓存区取货
     * 5- 已送至目的地终点
     */
    private String trayStatus;

    /**
     * 给AGV下发命令后返回的任务号，用于查询当前托盘运送状态
     */
    private String robotTaskCode;

    /**
     * 托盘详细信息
     */
    private String trayInfoAdd;

    /**
     * target_position 从缓存区发送到哪个目的地
     */
    private String targetPosition;

    /**
     * 是否正在等待取消完成 is_wait_cancel
     */
    private String isWaitCancel;

    /**
     * 是否已经锁定-2500车间专用字段 is_lock
     */
    private String isLock;

    /**
     * 原订单目的地-2500车间专用字段
     */
    private String mudidi;

    /**
     * 原订单目的地
     */
    private Long targetId;
}
