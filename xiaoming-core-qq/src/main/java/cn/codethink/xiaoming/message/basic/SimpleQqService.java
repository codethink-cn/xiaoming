package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.message.AutoSerializable;
import cn.codethink.xiaoming.message.AutoSummarizable;
import lombok.Data;

/**
 * @author Chuanwise
 *
 * @see Service
 */
@Data
public class SimpleQqService
    extends AbstractBasicMessage
    implements AutoSerializable, AutoSummarizable {
    
    /**
     * 常用的 json code
     */
    private static final int USUAL_JSON_CODE = 1;
    
    /**
     * 常用的 xml code
     */
    private static final int USUAL_XML_CODE = 1;
    
    /**
     * 服务码
     */
    private final int code;
    
    /**
     * 内容
     */
    private final String content;
    
    /**
     * 构造一个常用的 json 服务消息
     *
     * @param json json 消息体
     * @return json 服务消息
     */
    public static SimpleQqService ofJson(String json) {
        Preconditions.objectArgumentNonEmpty(json, "json");
        
        return new SimpleQqService(USUAL_JSON_CODE, json);
    }
    
    /**
     * 构造一个常用的 xml 服务消息
     *
     * @param xml xml 消息体
     * @return xml 服务消息
     */
    public static SimpleQqService ofXml(String xml) {
        Preconditions.objectArgumentNonEmpty(xml, "xml");
        
        return new SimpleQqService(USUAL_XML_CODE, xml);
    }
}
