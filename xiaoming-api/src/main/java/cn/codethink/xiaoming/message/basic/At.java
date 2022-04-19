package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.message.Summarizable;

/**
 * <h1>提及</h1>
 *
 * <p>表示提及某个对象，如账户、所有人甚至文件。</p>
 *
 * @author Chuanwise
 *
 * @see AccountAt
 * @see SingletonAccountAt
 * @see AllAccountAt
 */
public interface At
    extends BasicMessage, Summarizable, SpacedMessage {
}
