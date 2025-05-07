package com.middle.wcs.order.service;

import com.middle.wcs.order.entity.po.IdxFkTask1090;

import java.util.List;

/**
 * (OrderInfo)服务接口
 *
 * @author makejava
 * @since 2024-12-28 23:59:48
 */
public interface OrderInfoService {

    /**
     * 更新订单信息
     *
     * @param orderInfo 订单信息
     */
    Integer update(IdxFkTask1090 orderInfo);

    /**
     * 综合查询
     *
     * @param po
     * @return 订单信息
     */
    List<IdxFkTask1090> selectList(IdxFkTask1090 po);
}
