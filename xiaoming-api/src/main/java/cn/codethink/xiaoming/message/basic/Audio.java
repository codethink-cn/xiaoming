package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.resource.Resource;
import cn.codethink.xiaoming.spi.XiaoMing;

/**
 * <h1>音频</h1>
 *
 * <p>表示一段声音，如语音信息。</p>
 *
 * @author Chuanwise
 */
public interface Audio
    extends BasicMessage, SingletonMessage {
    
    /**
     * 构造一个语音
     *
     * @param resource 资源
     * @return 语音
     */
    static Audio of(Resource resource) {
//        XiaoMing.get().new
        // TODO: 2022/4/19 finish
        return null;
    }
    
    /**
     * 获取语音的资源。
     *
     * @return 语音的资源
     */
    Resource getResource();
}
