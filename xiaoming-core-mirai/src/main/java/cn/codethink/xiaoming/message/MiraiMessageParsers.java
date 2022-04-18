package cn.codethink.xiaoming.message;

import cn.chuanwise.common.util.Numbers;
import cn.codethink.xiaoming.message.basic.*;
import cn.codethink.xiaoming.message.module.deserialize.DeserializerValue;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.Image;

/**
 * mirai 特有的消息的反序列化器
 *
 * @author Chuanwise
 */
public class MiraiMessageParsers {
    
    public MiraiOrigin parseMiraiOrigin(@DeserializerValue String miraiCode) {
        return new MiraiOrigin(MiraiCode.deserializeMiraiCode(miraiCode));
    }
    
    public MiraiMarketFace parseMarketFace(@DeserializerValue String codeString,
                                           @DeserializerValue String name) {
        
        return new MiraiMarketFace(Numbers.parseInt(codeString), name);
    }
    
    public MiraiImage parseMiraiImage(@DeserializerValue String imageCode) {
        return new MiraiImage(Image.fromId(imageCode));
    }
    
    public VipFace parseVipFace(@DeserializerValue String typeCode,
                                @DeserializerValue String countCode) {
    
        final VipFaceType vipFaceType = VipFaceType.of(Numbers.parseInt(typeCode));
        final int count = Numbers.parseInt(countCode);
        
        return new VipFace(vipFaceType, count);
    }
}
