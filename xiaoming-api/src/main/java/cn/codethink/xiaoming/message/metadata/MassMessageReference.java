package cn.codethink.xiaoming.message.metadata;

import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.Mass;

/**
 * 和集体相关的消息源
 *
 * @author Chuanwise
 */
public interface MassMessageReference
    extends OnlineMessageReference {
    
    /**
     * 获取集体号
     *
     * @return 集体号
     */
    Code getMassCode();
    
    /**
     * 获取集体
     *
     * @return 集体
     */
    Mass getMass();
}
