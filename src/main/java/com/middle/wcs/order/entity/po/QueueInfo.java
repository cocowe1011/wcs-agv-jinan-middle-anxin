package com.middle.wcs.order.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
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
}
