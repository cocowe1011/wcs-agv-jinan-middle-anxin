package com.middle.wcs.order.entity.dto;
import lombok.Data;

/**
 * 机器人任务请求接口入参
 */

@Data
public class RobotTaskRequest {
    /**
     * 任务号
     */
    private String robotTaskCode;

    /**
     * 当前执行任务的机器人唯一标识。
     */
    private String singleRobotCode;
    /**
     * 自定义扩展字段
     */
    private Extra extra;
}
