package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.spi.XiaoMing;

import java.util.Set;

/**
 * <h1>图片类型</h1>
 *
 * <p>用于确定图片的类型，通过 {@link Image#getImageCodec()}</p>
 *
 * @author Chuanwise
 *
 * @see Image
 * @see ResourceImage
 */
public interface ImageCodec {
    
    /**
     * 获取该类型的扩展名
     *
     * @return 扩展名
     */
    String getExtension();
    
    /**
     * PNG
     */
    ImageCodec PNG = XiaoMing.get().newImageType("png");
    
    /**
     * BMP
     */
    ImageCodec BMP = XiaoMing.get().newImageType("bmp");
    
    /**
     * JPG
     */
    ImageCodec JPG = XiaoMing.get().newImageType("jpg");
    
    /**
     * GIF
     */
    ImageCodec GIF = XiaoMing.get().newImageType("gif");
    
    /**
     * WEBP
     */
    ImageCodec WEBP = XiaoMing.get().newImageType("webp");
    
    /**
     * RGB
     */
    ImageCodec RGB = XiaoMing.get().newImageType("rgb");
    
    /**
     * 获取全部图片类型
     *
     * @return 图片类型
     */
    static Set<ImageCodec> getImageType() {
        return XiaoMing.get().getImageTypes();
    }
    
    /**
     * 通过扩展名获取图片类型
     *
     * @param extension 扩展名
     * @return 图片类型
     * @throws NullPointerException extension 为 null
     * @throws IllegalArgumentException extension 为 ""
     * @throws java.util.NoSuchElementException 没有找到图片类型
     */
    static ImageCodec of(String extension) {
        return XiaoMing.get().getImageType(extension);
    }
}
