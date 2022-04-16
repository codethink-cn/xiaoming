package cn.codethink.xiaoming;

import cn.codethink.common.util.Preconditions;
import cn.codethink.common.util.StaticUtilities;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 机器人驱动管理器
 *
 * @author Chuanwise
 */
public class Bots
    extends StaticUtilities {
    
    /**
     * 机器人驱动表
     */
    private static final Map<IM, BotDriver> DRIVERS = new ConcurrentHashMap<>();
    
    /**
     * 获取机器人驱动表
     *
     * @return 机器人驱动表
     */
    public static Map<IM, BotDriver> getDrivers() {
        return Collections.unmodifiableMap(DRIVERS);
    }
    
    /**
     * 获取某通讯软件的机器人驱动
     *
     * @param IM 通讯软件
     * @return 如果存在该平台的驱动，返回驱动，否则返回 null
     */
    public static BotDriver getDriver(IM IM) {
        Preconditions.nonNull(IM, "instant messenger");
        
        return DRIVERS.get(IM);
    }
    
    /**
     * 注册某通讯软件的机器人驱动
     *
     * @param IM 通讯软件
     * @param botDriver 机器人驱动
     * @return 如果之前有一个同平台驱动，则返回之前的驱动，否则返回 null
     */
    public static BotDriver registerDriver(IM IM, BotDriver botDriver) {
        Preconditions.nonNull(IM, "instant messenger");
        Preconditions.nonNull(botDriver, "bot driver");
    
        return DRIVERS.put(IM, botDriver);
    }
}
