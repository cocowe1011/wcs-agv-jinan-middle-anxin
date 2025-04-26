package com.middle.wcs.login.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

/**
 * 功能描述: (对外提供预约信息的中台接口)
 * @author 文亮
 * @since 2022年5月13日14:07:01
 */
@Mapper
public interface LoginServiceMapper extends BaseMapper<Object> {

    /**
     * 12
     * @return
     */
    List<HashMap<String, Object>> getValidCode();

}
