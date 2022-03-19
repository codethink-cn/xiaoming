package cn.codethink.xiaoming.concurrent;

import cn.codethink.xiaoming.BotObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * Future
 *
 * @author Chuanwise
 */
public interface BotFuture<T>
        extends Future<T>, BotObject, BotTask {
    
    /**
     * 同步，并获取结果
     *
     * @param timeMillis 最长等待时间
     * @return 结果
     * @throws InterruptedException 中断异常
     * @throws ExecutionException   执行失败异常
     * @throws TimeoutException     超时异常
     */
    T get(long timeMillis) throws InterruptedException, ExecutionException, TimeoutException;
}