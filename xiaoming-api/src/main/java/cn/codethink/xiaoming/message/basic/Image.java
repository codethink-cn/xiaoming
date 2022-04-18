package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.message.AutoSummarizable;

/**
 * <h1>图片消息</h1>
 *
 * <p>表示一张图片</p>
 *
 * <ul>
 *     <li>消息码：{@code [image:$type,$value...]}</li>
 *     <li>摘要：{@code [图片]}</li>
 * </ul>
 *
 * @author Chuanwise
 *
 * @see ResourceImage
 * @see CacheImage
 * @see FlashImage
 */
public interface Image
    extends BasicMessage, SpacedMessage, AutoSummarizable {
    
    /**
     * 获取图片的类型，无法获取时返回 null
     *
     * @return 图片的类型或 null
     */
    ImageType getImageType();
    
    /**
     * 获取图片的宽度，无法获取时返回 0。
     *
     * @return 图片的宽度或 0
     */
    int getWidth();
    
    /**
     * 获取图片的高度，无法获取时返回 0。
     *
     * @return 图片的高度或 0
     */
    int getHeight();
    
    /**
     * 获取图片大小，无法获取时返回 0。
     *
     * @return 图片的大小（字节）或 0
     */
    long getSize();
}

