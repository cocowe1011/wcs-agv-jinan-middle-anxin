package com.middle.wcs.config.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.middle.wcs.config.entity.CssConfig;
import org.apache.ibatis.annotations.Mapper;
/**
 * @classDesc: 数据访问接口:(CssConfig)
 * @author: makejava
 * @date: 2023-07-04 15:31:47
 * @copyright 作者
 */
@Mapper
public interface CssConfigMapper extends BaseMapper<CssConfig> {


    CssConfig getConfig();
}
