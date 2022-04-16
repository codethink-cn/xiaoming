package cn.codethink.xiaoming.contact;

/**
 * 服务器下的频道
 *
 * @author Chuanwise
 */
public interface Channel {
    
    /**
     * 获取频道所属的行会
     *
     * @return 行会
     */
    Guild getGuild();
}
