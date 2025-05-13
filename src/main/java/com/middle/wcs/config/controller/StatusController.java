package com.middle.wcs.config.controller;

import com.middle.wcs.hander.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;


/**
 * @classDesc: 控制器:(CssConfig)
 * @author: makejava
 * @date: 2023-07-04 15:31:37
 * @copyright 作者
 */
@RestController
@RequestMapping("/status")
@Slf4j
public class StatusController {

    @RequestMapping("/check")
    public String check() {
        return "OK";
    }
    
    /**
     * 健康检查接口，返回系统健康状态
     * @return 系统健康状态信息
     */
    @RequestMapping("/health")
    public ResponseResult<Map<String, Object>> health() {
        Map<String, Object> healthInfo = new HashMap<>();
        
        // 内存信息
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        
        Map<String, Object> memoryInfo = new HashMap<>();
        memoryInfo.put("heapInit", heapMemoryUsage.getInit() / 1024 / 1024 + " MB");
        memoryInfo.put("heapUsed", heapMemoryUsage.getUsed() / 1024 / 1024 + " MB");
        memoryInfo.put("heapCommitted", heapMemoryUsage.getCommitted() / 1024 / 1024 + " MB");
        memoryInfo.put("heapMax", heapMemoryUsage.getMax() / 1024 / 1024 + " MB");
        memoryInfo.put("heapUsageRate", String.format("%.2f%%", (double) heapMemoryUsage.getUsed() / heapMemoryUsage.getCommitted() * 100));
        
        memoryInfo.put("nonHeapInit", nonHeapMemoryUsage.getInit() / 1024 / 1024 + " MB");
        memoryInfo.put("nonHeapUsed", nonHeapMemoryUsage.getUsed() / 1024 / 1024 + " MB");
        memoryInfo.put("nonHeapCommitted", nonHeapMemoryUsage.getCommitted() / 1024 / 1024 + " MB");
        memoryInfo.put("nonHeapMax", nonHeapMemoryUsage.getMax() / 1024 / 1024 + " MB");
        
        healthInfo.put("memory", memoryInfo);
        
        // 线程信息
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        Map<String, Object> threadInfo = new HashMap<>();
        threadInfo.put("threadCount", threadMXBean.getThreadCount());
        threadInfo.put("daemonThreadCount", threadMXBean.getDaemonThreadCount());
        threadInfo.put("peakThreadCount", threadMXBean.getPeakThreadCount());
        threadInfo.put("totalStartedThreadCount", threadMXBean.getTotalStartedThreadCount());
        
        healthInfo.put("thread", threadInfo);
        
        // JVM信息
        Map<String, Object> jvmInfo = new HashMap<>();
        jvmInfo.put("startTime", ManagementFactory.getRuntimeMXBean().getStartTime());
        jvmInfo.put("uptime", ManagementFactory.getRuntimeMXBean().getUptime() / 1000 / 60 + " 分钟");
        jvmInfo.put("vmName", ManagementFactory.getRuntimeMXBean().getVmName());
        jvmInfo.put("vmVersion", ManagementFactory.getRuntimeMXBean().getVmVersion());
        
        healthInfo.put("jvm", jvmInfo);
        
        // 系统状态评估
        boolean isHealthy = heapMemoryUsage.getUsed() < heapMemoryUsage.getMax() * 0.85;
        healthInfo.put("status", isHealthy ? "healthy" : "unhealthy");
        
        log.info("健康检查 - 当前内存使用率: {}%, 状态: {}", 
                (double) heapMemoryUsage.getUsed() / heapMemoryUsage.getMax() * 100,
                isHealthy ? "healthy" : "unhealthy");
        
        return ResponseResult.success(healthInfo);
    }
}
