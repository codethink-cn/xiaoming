package cn.codethink.xiaoming.message.basic;

import java.util.List;

/**
 * 合并转发消息
 *
 * @author Chuanwise
 */
public interface Forward
    extends SingletonMessage {
    
    String getSummary();
    
    String getTitle();
    
    List<String> getPreview();
    
    List<String> getMessages();
}
