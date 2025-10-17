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
        // 20-缓存区到缓存区命令状态-在缓存区等待取货
        // 21-缓存区到缓存区命令状态-已在缓存区取货
        // 22-缓存区到缓存区命令状态-已送至目的地终点
        // 23-缓存区到机械臂区域命令状态-在缓存区等待取货
        // 24-缓存区到机械臂区域命令状态-已在缓存区取货
        // 25-缓存区到机械臂区域命令状态-已送至目的地终点（a1、b1、d1、e1、a2、b1、d2）
        // 3-在缓存区等待AGV取货
        // 4-已在缓存区取货，正往运往目的地
        // 5-已送至2楼目的地（AGV2-2 AGV2-3传送带处）
        // 6-等待一/三楼AGV取货
        // 7-AGV已在一/三楼 AGV1-1/AGV3-1取货，正运往目的地
        // 10-已经给PLC发送过送货完指令
        // 11-在机械臂位置等待AGV取货
        // 12-AGV已在机械臂位置取货，正运往目的地
        // 13-已送至空托盘区域
        // 14-已经给PLC发送过清理托盘/杂物完成命令
        // 15-本缓存货位已集满
        // 16-空托盘处理命令已发送，正在等待AGV取货
        // 17-AGV已在空托盘缓存区取货，正运往目的地
        // 18-空托盘或者隔板已经送到目的地（C区）

        // ''-2500车间，说明托盘在来料缓存区进行缓存
        // 0- 在AGV5-1等待取货
        //  1- 已在AGV5-1取货，正运往来料缓存区
        //  2- 已送至来料缓存区
        //  3- 在来料缓存区等待取货
        //  4- 已在来料缓存区取货
        //  5- 已送至目的地终点
        QueueInfo queueInfoForUpdate = new QueueInfo();
        queueInfoForUpdate.setId(lqi.get(0).getId());
        switch (dto.getExtra().getValues().getMethod()) {
            case "start":
                break;
            case "outbin":
                // outbin说明AVG已经成功接货，把托盘状态更新
                if ("0".equals(lqi.get(0).getTrayStatus())) {
                    // 说明是2800缓存区的托盘
                    // 更新成1-已在2800取货，正往缓存区运送
                    log.info("对外开放接口-任务执行过程回馈接口更新托盘状态为1:{}", dto.getRobotTaskCode());
                    queueInfoForUpdate.setTrayStatus("1");
                    this.queueInfoService.update(queueInfoForUpdate);
                } else if("3".equals(lqi.get(0).getTrayStatus())) {
                    // 4-已在缓存区取货，正往运往目的地
                    if (null != lqi.get(0).getTargetId() && lqi.get(0).getTargetId() > 300 && lqi.get(0).getTargetId() < 600) {
                        // getTargetId() > 300说明是2500来料缓存区的托盘,需要把现有队列信息清空，把信息移到新的目的地上
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
                        queueInfoForUpdateMuDi.setTrayStatus("4");
                        queueInfoForUpdateMuDi.setRobotTaskCode(lqi.get(0).getRobotTaskCode());
                        queueInfoForUpdateMuDi.setTrayInfoAdd(lqi.get(0).getTrayInfoAdd());
                        queueInfoForUpdateMuDi.setTargetPosition(lqi.get(0).getTargetPosition());
                        queueInfoForUpdateMuDi.setIsLock("");
                        queueInfoForUpdateMuDi.setMudidi(lqi.get(0).getMudidi());
                        // 把当前出发位置的id存进去，防止任务取消，回更回来
                        queueInfoForUpdateMuDi.setTargetId(lqi.get(0).getId());
                        this.queueInfoService.update(queueInfoForUpdateMuDi);
                    } else {
                        log.info("对外开放接口-任务执行过程回馈接口更新托盘状态为4:{}", dto.getRobotTaskCode());
                        queueInfoForUpdate.setTrayStatus("4");
                        this.queueInfoService.update(queueInfoForUpdate);
                    }
                } else if("6".equals(lqi.get(0).getTrayStatus())) {
                    log.info("对外开放接口-一楼AGV小车已经取到货物，更新托盘状态为7:{}", dto.getRobotTaskCode());
                    queueInfoForUpdate.setTrayStatus("7");
                    this.queueInfoService.update(queueInfoForUpdate);
                } else if("23".equals(lqi.get(0).getTrayStatus())) {
                    // 缓存区到机械臂区域：23-在缓存区等待取货 -> 24-已在缓存区取货
                    log.info("对外开放接口-缓存区到机械臂-AGV已在缓存区取货，转移托盘信息到目标机械臂位置:{}", dto.getRobotTaskCode());
                    // 有targetId，需要把源A队列信息清空，把信息移到目的地机械臂上
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

                    // 把托盘信息挪到目的地机械臂位置上去
                    QueueInfo queueInfoForUpdateMuDi = new QueueInfo();
                    queueInfoForUpdateMuDi.setId(lqi.get(0).getTargetId());
                    queueInfoForUpdateMuDi.setTrayInfo(lqi.get(0).getTrayInfo());
                    queueInfoForUpdateMuDi.setTrayStatus("24");
                    queueInfoForUpdateMuDi.setRobotTaskCode(lqi.get(0).getRobotTaskCode());
                    queueInfoForUpdateMuDi.setTrayInfoAdd(lqi.get(0).getTrayInfoAdd());
                    queueInfoForUpdateMuDi.setTargetPosition(lqi.get(0).getTargetPosition());
                    queueInfoForUpdateMuDi.setMudidi(lqi.get(0).getMudidi());
                    // 把当前出发位置的id存进去，防止任务取消，回更回来
                    queueInfoForUpdateMuDi.setTargetId(lqi.get(0).getId());
                    this.queueInfoService.update(queueInfoForUpdateMuDi);
                } else if("11".equals(lqi.get(0).getTrayStatus())) {
                    // 空托盘清理或杂物托盘清理：11-在机械臂位置等待AGV取货 -> 12-AGV已在机械臂位置取货正运往目的地
                    log.info("对外开放接口-空托盘/杂物托盘清理-AGV已在机械臂位置取货，转移托盘信息到目标位置:{}", dto.getRobotTaskCode());
                    // 有targetId，需要把源机械手位置信息清空，把信息移到目的地上
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
                    queueInfoForUpdateMuDi.setTrayStatus("12");
                    queueInfoForUpdateMuDi.setRobotTaskCode(lqi.get(0).getRobotTaskCode());
                    queueInfoForUpdateMuDi.setTrayInfoAdd(lqi.get(0).getTrayInfoAdd());
                    queueInfoForUpdateMuDi.setTargetPosition(lqi.get(0).getTargetPosition());
                    queueInfoForUpdateMuDi.setIsLock("");
                    // 把当前出发位置的id存进去，防止任务取消，回更回来
                    queueInfoForUpdateMuDi.setTargetId(lqi.get(0).getId());
                    this.queueInfoService.update(queueInfoForUpdateMuDi);
                } else if("16".equals(lqi.get(0).getTrayStatus())) {
                    // 空托盘区域集满：16-空托盘处理命令已发送正在等待AGV取货 -> 17-AGV已在空托盘缓存区取货正运往目的地
                    log.info("对外开放接口-空托盘区域集满-AGV已在空托盘缓存区取货，转移托盘信息到目标位置:{}", dto.getRobotTaskCode());
                    // 有targetId，需要把现有z队列信息清空，把信息移到C队列上
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

                    // 把托盘信息挪到C队列目的地位置上去
                    QueueInfo queueInfoForUpdateMuDi = new QueueInfo();
                    queueInfoForUpdateMuDi.setId(lqi.get(0).getTargetId());
                    queueInfoForUpdateMuDi.setTrayInfo(lqi.get(0).getTrayInfo());
                    queueInfoForUpdateMuDi.setTrayStatus("17");
                    queueInfoForUpdateMuDi.setRobotTaskCode(lqi.get(0).getRobotTaskCode());
                    queueInfoForUpdateMuDi.setTrayInfoAdd(lqi.get(0).getTrayInfoAdd());
                    queueInfoForUpdateMuDi.setTargetPosition(lqi.get(0).getTargetPosition());
                    queueInfoForUpdateMuDi.setIsLock("");
                    // 把当前出发位置的id存进去，防止任务取消，回更回来
                    queueInfoForUpdateMuDi.setTargetId(lqi.get(0).getId());
                    this.queueInfoService.update(queueInfoForUpdateMuDi);
                } else if("20".equals(lqi.get(0).getTrayStatus())){
                    // 说明是下发了缓存区-缓存区的命令，并且已经出站
                    if (null != lqi.get(0).getTargetId() && lqi.get(0).getTargetId() > 0) {
                        // 说明是缓存区-缓存区的托盘,需要把现有队列信息清空，把信息移到新的目的地上
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
                        queueInfoForUpdateMuDi.setTrayStatus("21");
                        queueInfoForUpdateMuDi.setRobotTaskCode(lqi.get(0).getRobotTaskCode());
                        queueInfoForUpdateMuDi.setTrayInfoAdd(lqi.get(0).getTrayInfoAdd());
                        queueInfoForUpdateMuDi.setTargetPosition(lqi.get(0).getTargetPosition());
                        queueInfoForUpdateMuDi.setIsLock("");
                        // 把当前出发位置的id存进去，防止任务取消，回更回来
                        queueInfoForUpdateMuDi.setTargetId(lqi.get(0).getId());
                        log.info("对外开放接口-任务执行过程回馈接口更新托盘状态为21:{}", dto.getRobotTaskCode());
                        this.queueInfoService.update(queueInfoForUpdateMuDi);
                    } else {
                        log.info("对外开放接口-错误！:{}", dto.getRobotTaskCode());
                    }
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
                } else if("24".equals(lqi.get(0).getTrayStatus())) {
                    // 缓存区到机械臂区域：24-已在缓存区取货 -> 25-已送至机械臂目的地
                    log.info("对外开放接口-缓存区到机械臂-已送至机械臂目的地，更新托盘状态为25:{}", dto.getRobotTaskCode());
                    queueInfoForUpdate.setTrayStatus("25");
                    this.queueInfoService.update(queueInfoForUpdate);
                } else if("12".equals(lqi.get(0).getTrayStatus())) {
                    // 空托盘清理或杂物托盘清理：12-AGV已在机械臂位置取货正运往目的地
                    // 判断目的地是z队列还是C队列
                    if ("z".equals(lqi.get(0).getQueueName())) {
                        // 空托盘清理：送到z区 -> 13-已送至空托盘区域
                        log.info("对外开放接口-空托盘清理-已送至空托盘区域，更新托盘状态为13:{}", dto.getRobotTaskCode());
                        queueInfoForUpdate.setTrayStatus("13");
                    } else if ("C".equals(lqi.get(0).getQueueName())) {
                        // 杂物托盘清理：送到C区 -> 18-空托盘或者隔板已经送到目的地（C区）
                        log.info("对外开放接口-杂物托盘清理-已送至C区，更新托盘状态为18:{}", dto.getRobotTaskCode());
                        queueInfoForUpdate.setTrayStatus("18");
                    }
                    this.queueInfoService.update(queueInfoForUpdate);
                } else if("17".equals(lqi.get(0).getTrayStatus())) {
                    // 空托盘区域集满：17-AGV已在空托盘缓存区取货正运往目的地 -> 18-空托盘或者隔板已经送到目的地（C区）
                    log.info("对外开放接口-空托盘区域集满-已送至C区，更新托盘状态为18:{}", dto.getRobotTaskCode());
                    queueInfoForUpdate.setTrayStatus("18");
                    this.queueInfoService.update(queueInfoForUpdate);
                } else if("21".equals(lqi.get(0).getTrayStatus())) {
                    // 说明是缓存区-缓存区的命令，更新成2-已送至2楼缓存区
                    log.info("对外开放接口-任务执行过程回馈接口更新托盘状态为2:{}", dto.getRobotTaskCode());
                    queueInfoForUpdate.setTrayStatus("2");
                    queueInfoForUpdate.setTargetPosition("");
                    this.queueInfoService.update(queueInfoForUpdate);
                } else {
                    log.info("end状态方法-托盘状态不正确:{}", dto.getRobotTaskCode());
                }
                break;
            case "notifyFullSite":
                // 空托盘区域集满通知
                log.info("对外开放接口-空托盘区域集满通知，更新托盘状态为15:{}", dto.getRobotTaskCode());
                queueInfoForUpdate.setTrayStatus("15");
                this.queueInfoService.update(queueInfoForUpdate);
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
                } else if("20".equals(lqi.get(0).getTrayStatus())) {
                    // 说明是还没有在缓存区出站，直接把状态和占用的位置状态恢复即可
                    queueInfoForUpdate.setTrayStatus("2");
                    queueInfoForUpdate.setTargetId(0L);
                    queueInfoForUpdate.setRobotTaskCode("");
                    queueInfoForUpdate.setTargetPosition("");
                    queueInfoForUpdate.setIsWaitCancel("");
                    this.queueInfoService.update(queueInfoForUpdate);
                    // 把占用的位置解开
                    QueueInfo queueInfoForUpdateMuDi = new QueueInfo();
                    queueInfoForUpdateMuDi.setId(lqi.get(0).getTargetId());
                    queueInfoForUpdateMuDi.setIsLock("");
                    log.info("对外开放接口-20状态任务执行过程回馈接口收到取消命令，恢复成2状态，并解除目的地占用:{}", dto.getRobotTaskCode());
                    this.queueInfoService.update(queueInfoForUpdateMuDi);
                } else if("21".equals(lqi.get(0).getTrayStatus())) {
                    // 说明是缓存区-缓存区的命令。已经在缓存区出站，需要把AGV的信息恢复到原位置
                    // 需要把状态再更新回去
                    // 先把当前位置信息给清除掉
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
                    // 再把托盘信息恢复回托盘缓存区
                    QueueInfo queueInfoForUpdateMuDi = new QueueInfo();
                    queueInfoForUpdateMuDi.setId(lqi.get(0).getTargetId());
                    queueInfoForUpdateMuDi.setTrayInfo(lqi.get(0).getTrayInfo());
                    queueInfoForUpdateMuDi.setTrayStatus("2");
                    queueInfoForUpdateMuDi.setRobotTaskCode(lqi.get(0).getRobotTaskCode());
                    queueInfoForUpdateMuDi.setTrayInfoAdd(lqi.get(0).getTrayInfoAdd());
                    this.queueInfoService.update(queueInfoForUpdateMuDi);
                } else if("3".equals(lqi.get(0).getTrayStatus()) || "4".equals(lqi.get(0).getTrayStatus())) {
                    // 2500车间的4状态的取消有一些特殊
                    if ("4".equals(lqi.get(0).getTrayStatus()) && null != lqi.get(0).getTargetId() && lqi.get(0).getTargetId() > 0) {
                        // 需要把状态再更新回去
                        // 先把当前位置信息给清除掉
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
                        // 再把托盘信息恢复回托盘缓存区
                        QueueInfo queueInfoForUpdateMuDi = new QueueInfo();
                        queueInfoForUpdateMuDi.setId(lqi.get(0).getTargetId());
                        queueInfoForUpdateMuDi.setTrayInfo(lqi.get(0).getTrayInfo());
                        queueInfoForUpdateMuDi.setTrayStatus("2");
                        queueInfoForUpdateMuDi.setRobotTaskCode(lqi.get(0).getRobotTaskCode());
                        queueInfoForUpdateMuDi.setTrayInfoAdd(lqi.get(0).getTrayInfoAdd());
                        queueInfoForUpdateMuDi.setTargetPosition(lqi.get(0).getTargetPosition());
                        queueInfoForUpdateMuDi.setIsLock("1");
                        queueInfoForUpdateMuDi.setMudidi(lqi.get(0).getMudidi());
                        queueInfoForUpdateMuDi.setTargetId(lqi.get(0).getTargetId());
                        this.queueInfoService.update(queueInfoForUpdateMuDi);
                    } else{
                        // 说明在缓存区取货后取消了，把托盘状态更新为2-已送至2楼缓存区
                        log.info("对外开放接口-任务执行过程回馈接口收到取消命令，更新托盘状态为2:{}", dto.getRobotTaskCode());
                        queueInfoForUpdate.setTrayStatus("2");
                        queueInfoForUpdate.setIsWaitCancel("");
                        // 2500车间，需要恢复成锁定状态，不然前台程序定时器扫描到会自动发送
                        if (null != lqi.get(0).getTargetId() && lqi.get(0).getTargetId() > 0 && lqi.get(0).getTargetId() > 300 && lqi.get(0).getTargetId() < 600) {
                            queueInfoForUpdate.setIsLock("1");
                        }
                        this.queueInfoService.update(queueInfoForUpdate);
                    }
                } else if("23".equals(lqi.get(0).getTrayStatus())) {
                    // 缓存区到机械臂：23-在缓存区等待取货 -> 恢复为2-已送至2楼缓存区
                    log.info("对外开放接口-缓存区到机械臂任务取消（状态23），恢复托盘状态为2:{}", dto.getRobotTaskCode());
                    queueInfoForUpdate.setTrayStatus("2");
                    queueInfoForUpdate.setTargetId(0L);
                    queueInfoForUpdate.setRobotTaskCode("");
                    queueInfoForUpdate.setTargetPosition("");
                    queueInfoForUpdate.setIsWaitCancel("");
                    this.queueInfoService.update(queueInfoForUpdate);
                } else if("24".equals(lqi.get(0).getTrayStatus())) {
                    // 缓存区到机械臂：24-已在缓存区取货 -> 清空机械臂位置，恢复到A区
                    log.info("对外开放接口-缓存区到机械臂任务取消（状态24），恢复托盘到A区:{}", dto.getRobotTaskCode());
                    // 先清空当前机械臂位置信息
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
                    // 把托盘信息恢复到源A区位置
                    QueueInfo queueInfoForUpdateMuDi = new QueueInfo();
                    queueInfoForUpdateMuDi.setId(lqi.get(0).getTargetId());
                    queueInfoForUpdateMuDi.setTrayInfo(lqi.get(0).getTrayInfo());
                    queueInfoForUpdateMuDi.setTrayStatus("2");
                    queueInfoForUpdateMuDi.setRobotTaskCode("");
                    queueInfoForUpdateMuDi.setTrayInfoAdd(lqi.get(0).getTrayInfoAdd());
                    queueInfoForUpdateMuDi.setMudidi(lqi.get(0).getMudidi());
                    queueInfoForUpdateMuDi.setTargetPosition("");
                    queueInfoForUpdateMuDi.setIsWaitCancel("");
                    queueInfoForUpdateMuDi.setTargetId(0L);
                    this.queueInfoService.update(queueInfoForUpdateMuDi);
                } else if("11".equals(lqi.get(0).getTrayStatus())) {
                    // 机械臂位置空托盘/杂物清理：11-在机械臂位置等待AGV取货 -> 清空任务，恢复为空闲
                    log.info("对外开放接口-机械臂清理任务取消（状态11），清空托盘信息:{}", dto.getRobotTaskCode());
                    queueInfoForUpdate.setTrayStatus("");
                    queueInfoForUpdate.setRobotTaskCode("");
                    queueInfoForUpdate.setTargetPosition("");
                    queueInfoForUpdate.setTargetId(0L);
                    queueInfoForUpdate.setIsWaitCancel("");
                    this.queueInfoService.update(queueInfoForUpdate);
                } else if("12".equals(lqi.get(0).getTrayStatus())) {
                    // 机械臂位置空托盘/杂物清理：12-AGV已在机械臂位置取货 -> 清空目的地，恢复源机械臂位置为空闲
                    log.info("对外开放接口-机械臂清理任务取消（状态12），清空目的地和源位置:{}", dto.getRobotTaskCode());
                    // 先清空当前目的地位置信息
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
                    // 清空源机械臂位置（如果有targetId表示源位置）
                    if (null != lqi.get(0).getTargetId() && lqi.get(0).getTargetId() > 0) {
                        QueueInfo queueInfoForUpdateSource = new QueueInfo();
                        queueInfoForUpdateSource.setId(lqi.get(0).getTargetId());
                        queueInfoForUpdateSource.setTrayStatus("");
                        queueInfoForUpdateSource.setRobotTaskCode("");
                        queueInfoForUpdateSource.setTargetPosition("");
                        queueInfoForUpdateSource.setTargetId(0L);
                        queueInfoForUpdateSource.setIsWaitCancel("");
                        this.queueInfoService.update(queueInfoForUpdateSource);
                    }
                } else if("16".equals(lqi.get(0).getTrayStatus())) {
                    // 空托盘区集满：16-在空托盘区等待AGV取货 -> 恢复为15-集满状态
                    log.info("对外开放接口-空托盘区转运任务取消（状态16），恢复托盘状态为15:{}", dto.getRobotTaskCode());
                    queueInfoForUpdate.setTrayStatus("15");
                    queueInfoForUpdate.setTargetId(0L);
                    queueInfoForUpdate.setRobotTaskCode("");
                    queueInfoForUpdate.setTargetPosition("");
                    queueInfoForUpdate.setIsWaitCancel("");
                    this.queueInfoService.update(queueInfoForUpdate);
                } else if("17".equals(lqi.get(0).getTrayStatus())) {
                    // 空托盘区集满：17-AGV已在空托盘区取货 -> 清空C区目的地，恢复z区为15-集满状态
                    log.info("对外开放接口-空托盘区转运任务取消（状态17），清空C区目的地并恢复z区状态为15:{}", dto.getRobotTaskCode());
                    // 先清空当前C区目的地位置信息
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
                    // 把托盘信息恢复到源z区位置
                    if (null != lqi.get(0).getTargetId() && lqi.get(0).getTargetId() > 0) {
                        QueueInfo queueInfoForUpdateMuDi = new QueueInfo();
                        queueInfoForUpdateMuDi.setId(lqi.get(0).getTargetId());
                        queueInfoForUpdateMuDi.setTrayInfo(lqi.get(0).getTrayInfo());
                        queueInfoForUpdateMuDi.setTrayStatus("15");
                        queueInfoForUpdateMuDi.setRobotTaskCode("");
                        queueInfoForUpdateMuDi.setTrayInfoAdd(lqi.get(0).getTrayInfoAdd());
                        queueInfoForUpdateMuDi.setTargetPosition("");
                        queueInfoForUpdateMuDi.setIsWaitCancel("");
                        queueInfoForUpdateMuDi.setTargetId(0L);
                        this.queueInfoService.update(queueInfoForUpdateMuDi);
                    }
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
