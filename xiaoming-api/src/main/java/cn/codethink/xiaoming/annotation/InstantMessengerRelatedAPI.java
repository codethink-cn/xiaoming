package cn.codethink.xiaoming.annotation;

import cn.codethink.xiaoming.InstantMessenger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 具备该注解的方法或属性，和即时通讯软件有关
 *
 * @author Chuanwise
 */
@Retention(RetentionPolicy.CLASS)
public @interface InstantMessengerRelatedAPI {
    
    /**
     * 获取即时通讯软件列表
     *
     * @return 即时通讯软件列表
     */
    InstantMessenger[] value();
}
