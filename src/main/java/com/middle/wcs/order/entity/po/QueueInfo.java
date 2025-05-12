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
     * 3-在缓存区等待AGV取货
     * 4-已在缓存区取货，正往运往目的地
     * 5-已送至2楼目的地
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
}
