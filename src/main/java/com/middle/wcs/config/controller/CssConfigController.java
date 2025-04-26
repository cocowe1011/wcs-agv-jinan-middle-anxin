package com.middle.wcs.config.controller;

import com.middle.wcs.config.entity.CssConfig;
import com.middle.wcs.config.entity.PcsConfig;
import com.middle.wcs.config.service.CssConfigService;
import com.middle.wcs.hander.ResponseResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * @classDesc: 控制器:(CssConfig)
 * @author: makejava
 * @date: 2023-07-04 15:31:37
 * @copyright 作者
 */
@RestController
@RequestMapping("/cssConfig")
public class CssConfigController {
    
    @Resource
    private CssConfigService cssConfigService;

    @PostMapping("/update")
    public ResponseResult<Integer> update (@RequestBody CssConfig cssConfig) {
        return ResponseResult.success(cssConfigService.update(cssConfig));
    }

    @PostMapping("/updatePcsConfig")
    public ResponseResult<Integer> updatePcsConfig (@RequestBody PcsConfig pcsConfig) {
        return ResponseResult.success(cssConfigService.updatePcsConfig(pcsConfig));
    }

    @RequestMapping("/getConfig")
    public ResponseResult<CssConfig> getConfig() {
        return ResponseResult.success(cssConfigService.getConfig());
    }

    @RequestMapping("/getPcsConfig")
    public ResponseResult<PcsConfig> getPcsConfig() {
        return ResponseResult.success(cssConfigService.getPcsConfig());
    }
}
