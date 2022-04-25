package cn.codethink.xiaoming.concurrent;

import cn.chuanwise.common.concurrent.Promise;

/**
 * 机器人相关异步结果
 *
 * @author Chuanwise
 *
 * @see cn.chuanwise.common.concurrent.Promise
 */
public interface BotPromise<T>
    extends BotTask, Promise<T> {
}
