package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.IM;
import cn.codethink.xiaoming.annotation.ExpectantAPI;
import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.message.AutoSummarizable;
import cn.codethink.xiaoming.spi.XiaoMing;

/**
 * <h1>闪照消息</h1>
 *
 * <ul>
 *     <li>消息码：{@code [flash:$value...]}</li>
 *     <li>摘要：{@code [闪照]}</li>
 * </ul>
 *
 * @author Chuanwise
 *
 * @see Image
 */
@ExpectantAPI(IM.QQ)
public interface FlashImage
    extends SingletonMessage, AutoSerializable, AutoSummarizable {
    
    /**
     * 将图片包装为闪照
     *
     * @param image 图片
     * @return 闪照
     * @throws NullPointerException image 为 null
     */
    static FlashImage of(Image image) {
        return XiaoMing.get().newFlashImage(image);
    }
    
    /**
     * 获取闪照对应的图片
     *
     * @return 闪照对应的图片
     */
    Image getImage();
}