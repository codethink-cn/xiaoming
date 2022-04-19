package cn.codethink.xiaoming.message.basic;

/**
 * <h1>在线语音</h1>
 *
 * <p>在线语音是从事件中收到的语音，可以下载。</p>
 *
 * @author Chuanwise
 */
public interface OnlineAudio
    extends Audio {
    
    /**
     * 获取时间长度
     *
     * @return 时间长度（毫秒）
     */
    long getTimeLength();
    
    /**
     * 获取语音的文件名。
     *
     * @return 语音的文件名
     */
    String getName();
    
    /**
     * 获取音频的 Md5 摘要
     *
     * @return 音频的 Md5 摘要
     */
    byte[] getDigest();
    
    /**
     * 获取音频类型
     *
     * @return 音频类型
     */
    AudioCodec getCodec();
    
    /**
     * 获取文件大小
     *
     * @return 文件大小（字节）
     */
    int getSize();
}
