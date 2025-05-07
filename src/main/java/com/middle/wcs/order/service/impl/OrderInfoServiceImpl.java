package com.middle.wcs.order.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.middle.wcs.order.dao.IdxFkTask1090Mapper;
import com.middle.wcs.order.entity.po.IdxFkTask1090;
import com.middle.wcs.order.entity.po.OrderInfo;
import com.middle.wcs.order.dao.OrderInfoMapper;
import com.middle.wcs.order.service.OrderInfoService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

import java.util.List;

import static com.github.pagehelper.page.PageMethod.startPage;

/**
 * (OrderInfo)服务实现类
 *
 * @author makejava
 * @since 2024-12-28 23:59:48
 */
@Service
public class OrderInfoServiceImpl implements OrderInfoService {
    
    @Resource
    private IdxFkTask1090Mapper idxFkTask1090Mapper;

    @Override
    @DS("db2")
    public Integer update(IdxFkTask1090 idxFkTask1090) {
        return idxFkTask1090Mapper.updateById(idxFkTask1090);
    }

    @Override
    @DS("db2")
    public List<IdxFkTask1090> selectList(IdxFkTask1090 po) {
        // 根据入参构建查询条件
        QueryWrapper<IdxFkTask1090> queryWrapper = new QueryWrapper<>(po);
        return this.idxFkTask1090Mapper.selectList(queryWrapper);
    }
}
