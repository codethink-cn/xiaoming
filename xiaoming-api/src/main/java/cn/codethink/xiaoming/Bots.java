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
    private static final Map<InstantMessenger, BotDriver> DRIVERS = new ConcurrentHashMap<>();
    
    /**
     * 获取机器人驱动表
     *
     * @return 机器人驱动表
     */
    public static Map<InstantMessenger, BotDriver> getDrivers() {
        return Collections.unmodifiableMap(DRIVERS);
    }
    
    /**
     * 获取某通讯软件的机器人驱动
     *
     * @param instantMessenger 通讯软件
     * @return 如果存在该平台的驱动，返回驱动，否则返回 null
     */
    public static BotDriver getDriver(InstantMessenger instantMessenger) {
        Preconditions.namedArgumentNonNull(instantMessenger, "instant messenger");
        
        return DRIVERS.get(instantMessenger);
    }
    
    /**
     * 注册某通讯软件的机器人驱动
     *
     * @param instantMessenger 通讯软件
     * @param botDriver 机器人驱动
     * @return 如果之前有一个同平台驱动，则返回之前的驱动，否则返回 null
     */
    public static BotDriver registerDriver(InstantMessenger instantMessenger, BotDriver botDriver) {
        Preconditions.namedArgumentNonNull(instantMessenger, "instant messenger");
        Preconditions.namedArgumentNonNull(botDriver, "bot driver");
    
        return DRIVERS.put(instantMessenger, botDriver);
    }
}
