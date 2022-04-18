package cn.codethink.xiaoming.message.basic;

import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.message.AutoSummarizable;
import cn.codethink.xiaoming.spi.XiaoMing;

/**
 * <h1>音乐分享</h1>
 *
 * <p>消息码：{@code [music:share:$software:$title:$description:$summary:$jumpUrl:$coverUrl:$musicUrl]}</p>
 *
 * @author Chuanwise
 */
public interface MusicShare
    extends SingletonMessage, AutoSerializable, AutoSummarizable {
    
    /**
     * 构造一个音乐分享消息
     *
     * @param softwareType 音乐软件类型
     * @param title        标题
     * @param description  描述
     * @param summary      摘要
     * @param jumpUrl      跳转 Url
     * @param coverUrl     封面 Url
     * @param musicUrl     音乐 Url
     * @return 音乐分享消息
     * @throws NullPointerException     softwareType, title, description, summary, jumpUrl, coverUrl 或 musicUrl 为 null
     * @throws IllegalArgumentException title, description, summary, jumpUrl, coverUrl 或 musicUrl 为 ""
     */
    static MusicShare newInstance(MusicSoftwareType softwareType,
                                  String title,
                                  String description,
                                  String summary,
                                  String jumpUrl,
                                  String coverUrl,
                                  String musicUrl) {
        return XiaoMing.get().newMusicShare(softwareType, title, description, summary, jumpUrl, coverUrl, musicUrl);
    }
    
    /**
     * 获取音乐软件类型
     *
     * @return 音乐软件类型
     */
    MusicSoftwareType getSoftwareType();
    
    /**
     * 获取标题
     *
     * @return 标题
     */
    String getTitle();
    
    /**
     * 获取描述
     *
     * @return 描述
     */
    String getDescription();
    
    /**
     * 获取摘要
     *
     * @return 摘要
     */
    String getSummary();
    
    /**
     * 获取点击后跳转的 Url
     *
     * @return 点击后跳转的 Url
     */
    String getJumpUrl();
    
    /**
     * 获取封面 Url
     *
     * @return 封面 Url
     */
    String getCoverUrl();
    
    /**
     * 获取音乐文件 Url
     *
     * @return 音乐文件 Url
     */
    String getMusicUrl();
}