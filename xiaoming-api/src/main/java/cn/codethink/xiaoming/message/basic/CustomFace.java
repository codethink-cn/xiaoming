package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.message.AutoSummarizable;
import cn.codethink.xiaoming.spi.XiaoMing;

/**
 * <h1>自定义表情</h1>
 *
 * <p>表示用户自己收藏的表情。在 QQ 上被称为「动画表情」。</p>
 *
 * <ul>
 *     <li>消息码：{@code [face:custom:$]}</li>
 * </ul>
 *
 * @author Chuanwise
 *
 * @see Image
 * @see Face
 */
public interface CustomFace
    extends Face, AutoSummarizable, AutoSerializable {
    
    /**
     * 将图片包装为自定义表情
     *
     * @param image 图片
     * @return 自定义表情
     * @throws NullPointerException image 为 null
     */
    static CustomFace of(Image image) {
        return XiaoMing.get().newCustomFace(image);
    }
    
    /**
     * 获取表情图片
     *
     * @return 表情图片
     */
    Image getImage();
}
