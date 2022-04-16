package cn.codethink.xiaoming.message;

import cn.chuanwise.common.util.Numbers;
import cn.codethink.xiaoming.message.basic.MiraiOrigin;
import cn.codethink.xiaoming.message.basic.VipFace;
import cn.codethink.xiaoming.message.basic.VipFaceType;
import cn.codethink.xiaoming.message.parser.MessageParser;
import cn.codethink.xiaoming.message.parser.ParserArgument;
import net.mamoe.mirai.message.code.MiraiCode;

/**
 * mirai 特有的消息的解析器
 *
 * @author Chuanwise
 */
public class MiraiMessageParsers {
    
    @MessageParser({"origin", "mirai", "?"})
    public MiraiOrigin parseMiraiOrigin(@ParserArgument String miraiCode) {
        return new MiraiOrigin(MiraiCode.deserializeMiraiCode(miraiCode));
    }
    
    @MessageParser({"vip", "face", "?", "?"})
    public VipFace parseVipFace(@ParserArgument String typeCode,
                                @ParserArgument String countCode) {
    
        final VipFaceType vipFaceType = VipFaceType.of(Numbers.parseInt(typeCode));
        final int count = Numbers.parseInt(countCode);
        
        return new VipFace(vipFaceType, count);
    }
}
