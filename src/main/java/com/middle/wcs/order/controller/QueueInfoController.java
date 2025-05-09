package com.middle.wcs.order.controller;

import com.middle.wcs.hander.ResponseResult;
import com.middle.wcs.order.entity.po.QueueInfo;
import com.middle.wcs.order.service.QueueInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * (QueueInfo)控制器
 *
 * @author makejava
 * @since 2025-01-01 12:44:45
 */

@RestController
@Api(tags = "")
@RequestMapping("/queue_info")
public class QueueInfoController {

    @Resource
    private QueueInfoService queueInfoService;


/**
    * 更新
    *
    * @param  dto
    */
    @ApiOperation("更新")
    @PostMapping("/update")
    public ResponseResult<Integer> update(@ApiParam(value = "信息",required = true) @RequestBody QueueInfo dto) {
        return ResponseResult.success(this.queueInfoService.update(dto));
    }

    /**
     * 查询队列信息列表
     * @return 出参
     */
    @ApiOperation("查询队列信息列表")
    @PostMapping("/queryQueueList")
    public ResponseResult<List<QueueInfo>> queryQueueList(@ApiParam(value = "入参", required = true) @RequestBody QueueInfo dto) {
        return ResponseResult.success(this.queueInfoService.queryQueueList(dto));
    }

    @ApiOperation("根据ID查询队列信息")
    @GetMapping("/getQueueInfoById")
    public ResponseResult<QueueInfo> getQueueInfoById(@ApiParam(value = "队列ID", required = true) @RequestParam Long id) {
        return ResponseResult.success(this.queueInfoService.getQueueInfoById(id));
    }

}
