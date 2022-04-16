package cn.codethink.xiaoming.message.element;

import cn.codethink.xiaoming.annotation.IMRelatedAPI;

/**
 * 和平台相关的缓存图片，应该被序列化为 {@code [image:$platform,$arguments...]}
 *
 * @author Chuanwise
 */
@IMRelatedAPI
public interface CacheImage
    extends Image {
}
