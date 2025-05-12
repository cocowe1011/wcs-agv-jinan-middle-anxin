package com.middle.wcs.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.middle.wcs.order.dao.QueueInfoMapper;
import com.middle.wcs.order.entity.po.QueueInfo;
import com.middle.wcs.order.service.QueueInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * (QueueInfo)表服务实现类
 *
 * @author makejava
 * @since 2025-01-01 12:44:45
 */
@Service("queueInfoService")
public class QueueInfoServiceImpl implements QueueInfoService {

    @Resource
    private QueueInfoMapper queueInfoMapper;


    /**
     * 修改数据
     *
     * @param entity 实例对象
     * @return 实例对象
     */
    @Override
    public int update(QueueInfo entity) {
        return this.queueInfoMapper.updateById(entity);
    }

    @Override
    public List<QueueInfo> queryQueueList(QueueInfo dto) {
        QueryWrapper<QueueInfo> wrapper= new QueryWrapper<>(dto);
        // 根据字段id进行排序
        wrapper.orderByAsc("id");
        return queueInfoMapper.selectList(wrapper);
    }

    @Override
    public QueueInfo getQueueInfoById(Long id) {
        return queueInfoMapper.selectById(id);
    }

    /**
     * 处理AGV2-2队列的数据
     *
     * @param dto 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateAgv22(QueueInfo dto) {
        // 1、先把当前队列信息清空
        QueueInfo queueInfo = new QueueInfo();
        queueInfo.setId(dto.getId());
        queueInfo.setTrayInfo("");
        queueInfo.setTrayStatus("");
        queueInfo.setRobotTaskCode("");
        queueInfo.setTrayInfoAdd("");
        queueInfo.setTargetPosition("");
        int i = this.queueInfoMapper.updateById(queueInfo);
        if (i == 0) {
            throw new RuntimeException("清空队列信息失败");
        }
        // 2、插入队列信息
        QueueInfo queueInfoForInsert = new QueueInfo();
        queueInfoForInsert.setQueueName("AGV2-2");
        queueInfoForInsert.setTrayInfo(dto.getTrayInfo());
        queueInfoForInsert.setTrayStatus(dto.getTrayStatus());
        queueInfoForInsert.setRobotTaskCode(dto.getRobotTaskCode());
        queueInfoForInsert.setTrayInfoAdd(dto.getTrayInfoAdd());
        queueInfoForInsert.setTargetPosition(dto.getTargetPosition());
        return this.queueInfoMapper.insert(queueInfoForInsert);
    }

    @Override
    public int delete(QueueInfo dto) {
        return this.queueInfoMapper.deleteById(dto);
    }

}
