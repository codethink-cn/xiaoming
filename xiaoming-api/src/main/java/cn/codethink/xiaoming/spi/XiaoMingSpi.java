package cn.codethink.xiaoming.spi;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.annotation.InternalAPI;

import java.util.Iterator;
import java.util.Objects;
import java.util.ServiceLoader;

/**
 * <h1>小明驱动</h1>
 *
 * <p>用于管理小明的核心 API {@link XiaoMing}</p>
 *
 * @author Chuanwise
 *
 * @see XiaoMing
 */
@InternalAPI
public class XiaoMingSpi {
    
    /**
     * 全局唯一核心实例
     */
    private static XiaoMing xiaoMing;
    
    /**
     * 注册一个小明核心，用于提供或替换核心 API
     *
     * @param xiaoMing 小明核心
     * @throws NullPointerException xiaoMing 为 null
     */
    public static void setXiaoMing(XiaoMing xiaoMing) {
        Preconditions.objectNonNull(xiaoMing, "xiaoMing");
    
        synchronized (XiaoMingSpi.class) {
            if (Objects.nonNull(XiaoMingSpi.xiaoMing)) {
                unregisterService();
            }
            XiaoMingSpi.xiaoMing = xiaoMing;
            xiaoMing.onRegister();
        }
    }
    
    /**
     * 取消注册当前的小明核心
     */
    public static void unregisterService() {
        final XiaoMing xiaoMing = XiaoMingSpi.xiaoMing;
        XiaoMingSpi.xiaoMing = null;
        xiaoMing.onDeregister();
    }
    
    /**
     * 检查是否有小明核心可用
     *
     * @return 是否有小明核心可用
     */
    public static boolean isPresent() {
        return Objects.nonNull(xiaoMing);
    }
    
    /**
     * 获取小明核心
     *
     * @return 小明核心
     * @throws java.util.NoSuchElementException 缺少小明核心
     */
    public static XiaoMing getXiaoMing() {
        
        // load xiaoming
        if (Objects.isNull(xiaoMing)) {
            final Iterator<XiaoMing> iterator = ServiceLoader.load(XiaoMing.class).iterator();
            Preconditions.element(iterator.hasNext(), "no xiaoming present");
            
            setXiaoMing(iterator.next());
        }
        
        return xiaoMing;
    }
}
