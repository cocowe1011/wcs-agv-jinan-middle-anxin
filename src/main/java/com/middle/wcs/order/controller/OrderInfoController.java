package com.middle.wcs.order.controller;

import com.middle.wcs.hander.ResponseResult;
import com.middle.wcs.order.entity.po.IdxFkTask1090;
import com.middle.wcs.order.entity.po.OrderInfo;
import com.middle.wcs.order.service.OrderInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * (OrderInfo)控制器
 *
 * @author makejava
 * @since 2024-12-28 23:59:48
 */

@Api(tags = "订单管理接口")
@RestController
@RequestMapping("/order_info")
public class OrderInfoController {

    @Resource
    private OrderInfoService orderInfoService;
    
    @ApiOperation("更新订单信息")
    @PostMapping("/update")
    public ResponseResult<Integer> update(@ApiParam(value = "订单信息", required = true) @RequestBody IdxFkTask1090 po) {
        return ResponseResult.success(this.orderInfoService.update(po));
    }

    @ApiOperation("综合查询")
    @PostMapping("/selectList")
    public ResponseResult<List<IdxFkTask1090>> selectList(@ApiParam(value = "订单信息", required = true) @RequestBody IdxFkTask1090 po) {
        return ResponseResult.success(this.orderInfoService.selectList(po));
    }
}
