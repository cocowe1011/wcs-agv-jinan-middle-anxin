package com.middle.wcs.config.service.impl;

import com.middle.wcs.config.entity.CssConfig;
import com.middle.wcs.config.dao.CssConfigMapper;
import com.middle.wcs.config.entity.PcsConfig;
import com.middle.wcs.config.service.CssConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @classDesc: 业务逻辑:(CssConfig)
 * @author: makejava
 * @date: 2023-07-04 15:31:43
 * @copyright 作者
 */
@Service
public class CssConfigServiceImpl implements CssConfigService {
    @Resource
    private CssConfigMapper cssConfigMapper;

    @Override
    public Integer update (CssConfig cssConfig) {
        return cssConfigMapper.updateById(cssConfig);
    }

    @Override
    public CssConfig getConfig() {
        return cssConfigMapper.getConfig();
    }

    @Override
    public Integer updatePcsConfig(PcsConfig pcsConfig) {
        return null;
    }

    @Override
    public PcsConfig getPcsConfig() {
        return null;
    }
}
