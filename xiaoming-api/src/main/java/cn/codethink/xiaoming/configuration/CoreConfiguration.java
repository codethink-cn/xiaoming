package cn.codethink.xiaoming.configuration;

import lombok.Data;

/**
 * 机器人配置
 *
 * @author Chuanwise
 */
@Data
public class CoreConfiguration {
    
    /**
     * 核心线程池大小
     */
    protected int threadCount = 20;
}
