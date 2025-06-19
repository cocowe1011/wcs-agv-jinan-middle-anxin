package com.middle.wcs.order.controller;

import com.middle.wcs.hander.ResponseResult;
import com.middle.wcs.order.entity.dto.RobotTaskRequest;
import com.middle.wcs.order.entity.po.QueueInfo;
import com.middle.wcs.order.entity.vo.TaskVO;
import com.middle.wcs.order.service.QueueInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 对外开放接口
 * {
 *   "extra": {
 *     "values": {
 *       "method": "end"
 *     }
 *   },
 *   "robotTaskCode": "1748017879688"
 * }
 * @author makejava
 * @since 2024-12-28 23:59:48
 */

@Api(tags = "对外开放接口")
@RestController
@RequestMapping("/api")
@Slf4j
public class ExternalInterfaceController {

    @Resource
    private QueueInfoService queueInfoService;
    
    @ApiOperation("任务执行过程回馈接口")
    @PostMapping("/robot/reporter/task")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<TaskVO> task(@ApiParam(value = "入参", required = true) @RequestBody RobotTaskRequest dto) {
        log.info("对外开放接口-任务执行过程回馈接口入参：{}", dto);
        // 根据任务编号查询托盘信息
        QueueInfo queueInfo = new QueueInfo();
        queueInfo.setRobotTaskCode(dto.getRobotTaskCode());
        List<QueueInfo> lqi = this.queueInfoService.queryQueueList(queueInfo);
        // 判断lqi是否为空
        if (lqi == null || lqi.isEmpty()) {
            TaskVO taskVO = new TaskVO();
            taskVO.setRobotTaskCode(dto.getRobotTaskCode());
            return ResponseResult.successWithCode0(taskVO);
        }
        log.info("对外开放接口-任务执行过程回馈接口查询到的托盘信息：{}", lqi);
        // 看看AGV当前状态 默认使用方式:
        //outbin : 走出储位
        //end : 任务完成
        // 0-在2800等待AGV取货
        // 1-已在2800取货，正往缓存区运送
        // 2-已送至2楼缓存区
        // 3-在缓存区等待AGV取货
        // 4-已在缓存区取货，正往运往目的地
        // 5-已送至2楼目的地

        // ''-
        // 0-在2500来料缓存区等待取货
        // 1-已在2500来料缓存区取货，正往目的地运送
        // 2-已送至目的地来料缓存区
        QueueInfo queueInfoForUpdate = new QueueInfo();
        queueInfoForUpdate.setId(lqi.get(0).getId());
        switch (dto.getExtra().getValues().getMethod()) {
            case "start":
                break;
            case "outbin":
                // outbin说明AVG已经成功接货，把托盘状态更新
                if ("0".equals(lqi.get(0).getTrayStatus())) {
                    if (null != lqi.get(0).getTargetId() && lqi.get(0).getTargetId() > 0) {
                        // 说明是2500来料缓存区的托盘,需要把现有队列信息清空，把信息移到新的目的地上
                        queueInfoForUpdate.setTrayInfo("");
                        queueInfoForUpdate.setTrayStatus("");
                        queueInfoForUpdate.setRobotTaskCode("");
                        queueInfoForUpdate.setTrayInfoAdd("");
                        queueInfoForUpdate.setTargetPosition("");
                        queueInfoForUpdate.setIsWaitCancel("");
                        queueInfoForUpdate.setIsLock("");
                        queueInfoForUpdate.setMudidi("");
                        queueInfoForUpdate.setTargetId(0L);
                        this.queueInfoService.update(queueInfoForUpdate);
                        // 把托盘信息挪到目的地位置上去
                        QueueInfo queueInfoForUpdateMuDi = new QueueInfo();
                        queueInfoForUpdateMuDi.setId(lqi.get(0).getTargetId());
                        queueInfoForUpdateMuDi.setTrayInfo(lqi.get(0).getTrayInfo());
                        queueInfoForUpdateMuDi.setTrayStatus("1");
                        queueInfoForUpdateMuDi.setRobotTaskCode(lqi.get(0).getRobotTaskCode());
                        queueInfoForUpdateMuDi.setTrayInfoAdd(lqi.get(0).getTrayInfoAdd());
                        queueInfoForUpdateMuDi.setTargetPosition(lqi.get(0).getTargetPosition());
                        queueInfoForUpdateMuDi.setIsLock("");
                        queueInfoForUpdateMuDi.setMudidi(lqi.get(0).getMudidi());
                        this.queueInfoService.update(queueInfoForUpdateMuDi);
                    } else {
                        // 说明是2800缓存区的托盘
                        // 更新成1-已在2800取货，正往缓存区运送
                        log.info("对外开放接口-任务执行过程回馈接口更新托盘状态为1:{}", dto.getRobotTaskCode());
                        queueInfoForUpdate.setTrayStatus("1");
                        this.queueInfoService.update(queueInfoForUpdate);
                    }
                } else if("3".equals(lqi.get(0).getTrayStatus())) {
                    // 4-已在缓存区取货，正往运往目的地
                    log.info("对外开放接口-任务执行过程回馈接口更新托盘状态为4:{}", dto.getRobotTaskCode());
                    queueInfoForUpdate.setTrayStatus("4");
                    this.queueInfoService.update(queueInfoForUpdate);
                } else if("6".equals(lqi.get(0).getTrayStatus())) {
                    log.info("对外开放接口-一楼AGV小车已经取到货物，更新托盘状态为7:{}", dto.getRobotTaskCode());
                    queueInfoForUpdate.setTrayStatus("7");
                    this.queueInfoService.update(queueInfoForUpdate);
                } else {
                    log.info("outbin状态方法-托盘状态不正确:{}", dto.getRobotTaskCode());
                }
                break;
            case "end":
                // 说明AVG已经成功放货，把托盘状态更新为最新的
                if ("1".equals(lqi.get(0).getTrayStatus())) {
                    // 更新成2-已送至2楼缓存区
                    log.info("对外开放接口-任务执行过程回馈接口更新托盘状态为2:{}", dto.getRobotTaskCode());
                    queueInfoForUpdate.setTrayStatus("2");
                    this.queueInfoService.update(queueInfoForUpdate);
                } else if("4".equals(lqi.get(0).getTrayStatus())) {
                    // 更新成5-已送至2楼目的地
                    log.info("对外开放接口-任务执行过程回馈接口更新托盘状态为5:{}", dto.getRobotTaskCode());
                    queueInfoForUpdate.setTrayStatus("5");
                    this.queueInfoService.update(queueInfoForUpdate);
                } else {
                    log.info("end状态方法-托盘状态不正确:{}", dto.getRobotTaskCode());
                }
                break;
            case "cancel":
                // 说明任务取消了，把托盘状态恢复回去
                if ("0".equals(lqi.get(0).getTrayStatus()) || "1".equals(lqi.get(0).getTrayStatus())) {
                    // 说明在2800取货后取消了，直接把这个托盘在队列中删除
                    log.info("对外开放接口-任务执行过程回馈接口收到取消命令，直接删除托盘信息:{}", dto.getRobotTaskCode());
                    queueInfoForUpdate.setTrayInfo("");
                    queueInfoForUpdate.setTrayStatus("");
                    queueInfoForUpdate.setRobotTaskCode("");
                    queueInfoForUpdate.setTrayInfoAdd("");
                    queueInfoForUpdate.setTargetPosition("");
                    queueInfoForUpdate.setIsWaitCancel("");
                    queueInfoForUpdate.setIsLock("");
                    queueInfoForUpdate.setMudidi("");
                    queueInfoForUpdate.setTargetId(0L);
                    this.queueInfoService.update(queueInfoForUpdate);
                } else if("3".equals(lqi.get(0).getTrayStatus()) || "4".equals(lqi.get(0).getTrayStatus())) {
                    // 说明在缓存区取货后取消了，把托盘状态更新为2-已送至2楼缓存区
                    log.info("对外开放接口-任务执行过程回馈接口收到取消命令，更新托盘状态为2:{}", dto.getRobotTaskCode());
                    queueInfoForUpdate.setTrayStatus("2");
                    queueInfoForUpdate.setIsWaitCancel("");
                    this.queueInfoService.update(queueInfoForUpdate);
                } else if ("6".equals(lqi.get(0).getTrayStatus()) || "7".equals(lqi.get(0).getTrayStatus())) {
                    // 说明在一楼AGV取货后取消了，把托盘状态更新为2-已送至2楼缓存区
                    log.info("对外开放接口-任务执行过程回馈接口收到取消命令，更新托盘状态为5:{}", dto.getRobotTaskCode());
                    queueInfoForUpdate.setTrayStatus("5");
                    queueInfoForUpdate.setIsWaitCancel("");
                    this.queueInfoService.update(queueInfoForUpdate);
                } else {
                    log.info("cancel状态方法-托盘状态不正确:{}", dto.getRobotTaskCode());
                }
                break;
            default:
                log.info("对外开放接口-未知的任务状态:{}", dto.getRobotTaskCode());
                break;
        }
        TaskVO taskVO = new TaskVO();
        taskVO.setRobotTaskCode(dto.getRobotTaskCode());
        return ResponseResult.successWithCode0(taskVO);
    }
}
