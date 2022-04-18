package cn.codethink.xiaoming.test;

import cn.codethink.xiaoming.MiraiBot;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.spi.XiaoMing;
import cn.codethink.xiaoming.util.MessageCode;
import cn.codethink.xiaoming.util.Mirais;
import net.mamoe.mirai.message.data.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

public class MessageCodeTestBuilder {
    
    @Test
    void testDeserialize() {
        final String messageCode = "[at:singleton,l:1437100907] Java 是世界上最好的语言，[at:all]！[face:6] [image:url,https://qwq!]";
        final CompoundMessage compoundMessage = MessageCode.deserializeMessageCode(messageCode, null);
        System.out.println(compoundMessage);
    }
    
    private MessageChain asMessageChain(SingleMessage... singleMessages) {
        final MessageChainBuilder messageChainBuilder = new MessageChainBuilder(singleMessages.length);
        messageChainBuilder.addAll(Arrays.asList(singleMessages));
        return messageChainBuilder.build();
    }
    
    @Test
    void testConvert() throws ClassNotFoundException {
        System.out.println(Class.forName("cn.codethink.xiaoming.MiraiBot"));
        System.out.println(XiaoMing.get());
        
        final MessageChain messageChain = asMessageChain(
            new PlainText("test\\ [ ; \n: plain text"),
            new At(1437100907),
            AtAll.INSTANCE,
            new Face(Face.DA_KU)
        );
        final CompoundMessage compoundMessage = Mirais.toXiaoMing(messageChain, Collections.emptyMap());
        final String messageCode = compoundMessage.serializeToMessageCode();
        
        System.out.println(messageCode);
        System.out.println(compoundMessage.serializeToMessageSummary());
    
        final CompoundMessage deserializedCompoundMessage = MessageCode.deserializeMessageCode(messageCode, Collections.emptyMap());
        Assertions.assertEquals(compoundMessage.serializeToMessageCode(), deserializedCompoundMessage.serializeToMessageCode());
    }
}
