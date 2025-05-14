package com.middle.wcs.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 内存监控配置类
 * 定时监控JVM内存使用情况，及时发现内存泄漏问题
 * @author 文亮
 */
@Configuration
@Slf4j
public class MemoryMonitorConfig {

    @Autowired(required = false)
    private List<DataSource> dataSources;

    /**
     * 每5分钟执行一次内存监控，记录内存使用情况
     */
    @Scheduled(fixedRate = 300000)
    public void monitorMemory() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / 1024 / 1024; // 最大内存，单位MB
        long totalMemory = runtime.totalMemory() / 1024 / 1024; // 已分配内存，单位MB
        long freeMemory = runtime.freeMemory() / 1024 / 1024; // 已分配内存中的空闲部分，单位MB
        long usedMemory = totalMemory - freeMemory; // 已使用内存，单位MB
        
        // 计算内存使用率
        double memoryUsageRate = (double) usedMemory / totalMemory * 100;
        
        log.info("内存监控 - 最大可用内存: {}MB, 已分配内存: {}MB, 已使用内存: {}MB, 空闲内存: {}MB, 内存使用率: {:.2f}%",
                maxMemory, totalMemory, usedMemory, freeMemory, memoryUsageRate);
        
        // 内存使用率超过85%发出警告
        if (memoryUsageRate > 85) {
            log.warn("内存使用率超过85%，当前使用率: {:.2f}%，请检查是否存在内存泄漏问题", memoryUsageRate);
            // 当内存使用率过高时，尝试执行一次GC
            System.gc();
        }
        
        // 报告线程信息
        ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
        ThreadGroup parentGroup;
        while ((parentGroup = rootGroup.getParent()) != null) {
            rootGroup = parentGroup;
        }
        
        int threadCount = rootGroup.activeCount();
        log.info("当前活跃线程数: {}", threadCount);
    }
    
    /**
     * 每30分钟检查一次数据库连接
     * 测试数据库连接并主动关闭空闲连接
     */
    @Scheduled(fixedRate = 1800000) // 30分钟
    public void checkDatabaseConnections() {
        if (dataSources == null || dataSources.isEmpty()) {
            log.info("未找到数据源配置，跳过数据库连接检查");
            return;
        }
        
        List<Connection> connections = new ArrayList<>();
        
        try {
            for (DataSource dataSource : dataSources) {
                Connection conn = null;
                Statement stmt = null;
                
                try {
                    log.info("测试数据源连接...");
                    conn = dataSource.getConnection();
                    connections.add(conn);
                    
                    stmt = conn.createStatement();
                    stmt.execute("SELECT 1");
                    
                    log.info("数据源连接正常");
                } catch (SQLException e) {
                    log.error("数据源连接测试失败", e);
                } finally {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (SQLException e) {
                            log.error("关闭Statement失败", e);
                        }
                    }
                }
            }
        } finally {
            // 确保所有连接都被关闭
            for (Connection conn : connections) {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        log.error("关闭数据库连接失败", e);
                    }
                }
            }
        }
    }
    
    /**
     * 每2小时刷新日志文件
     * 通过设置一条强制刷新的日志，确保日志写入磁盘
     */
    @Scheduled(fixedRate = 7200000) // 2小时
    public void forceLogFlush() {
        log.info("强制刷新日志缓冲区 - {}", System.currentTimeMillis());
        System.out.flush();
        System.err.flush();
    }
} 