package cn.codethink.xiaoming;

/**
 * 机器人驱动
 *
 * 用于构造某平台的机器人
 *
 * @author Chuanwise
 */
@FunctionalInterface
public interface BotDriver {
    
    /**
     * 构造一个机器人
     *
     * @return 机器人
     */
    Bot generate();
}
