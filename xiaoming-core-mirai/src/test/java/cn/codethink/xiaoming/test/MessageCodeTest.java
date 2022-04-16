package cn.codethink.xiaoming.test;

import cn.codethink.xiaoming.message.MessageCode;
import cn.codethink.xiaoming.message.MiraiMessageChain;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.message.element.Text;
import net.mamoe.mirai.message.data.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class MessageCodeTest {
    
    @Test
    void testDeserialize() {
        final String messageCode = "[at:singleton,l:1437100907] Java 是世界上最好的语言，[at:all]！[face:6] [image:url,https://qwq!]";
        final CompoundMessage compoundMessage = MessageCode.deserializeToCompoundMessage(messageCode, null);
        System.out.println(compoundMessage);
    }
    
    private MessageChain asMessageChain(SingleMessage... singleMessages) {
        final MessageChainBuilder messageChainBuilder = new MessageChainBuilder(singleMessages.length);
        messageChainBuilder.addAll(Arrays.asList(singleMessages));
        return messageChainBuilder.build();
    }
    
    @Test
    void testConvert() {
        final MessageChain messageChain = asMessageChain(
            new PlainText("test plain text"),
            new At(1437100907),
            AtAll.INSTANCE,
            new Face(Face.DA_KU)
        );
        final CompoundMessage compoundMessage = MiraiMessageChain.toCompoundMessage(messageChain, null, null);
        System.out.println(compoundMessage.serializeToMessageCode());
        System.out.println(compoundMessage.serializeToSummary());
    }
}
