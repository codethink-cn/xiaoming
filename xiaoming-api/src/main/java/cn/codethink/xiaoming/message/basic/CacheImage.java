package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.annotation.IMRelatedAPI;

/**
 * <h1>平台相关图片</h1>
 *
 * <p>无法跨平台，消息码也无法持久保存，应该尽量少用。</p>
 *
 * <p>应该序列化为 {@code [image:$platform,$arguments...]}</p>
 *
 * @author Chuanwise
 */
@IMRelatedAPI
public interface CacheImage
    extends Image {
}
