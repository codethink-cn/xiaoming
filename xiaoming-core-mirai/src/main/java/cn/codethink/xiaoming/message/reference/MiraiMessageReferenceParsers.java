package cn.codethink.xiaoming.message.reference;

import cn.chuanwise.common.util.Arrays;
import cn.chuanwise.common.util.Numbers;
import cn.codethink.common.util.Preconditions;
import cn.codethink.xiaoming.message.parser.BasicMessageArgument;
import cn.codethink.xiaoming.message.parser.BasicMessageParser;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageSourceBuilder;
import net.mamoe.mirai.message.data.MessageSourceKind;

import java.util.ArrayList;
import java.util.List;

/**
 * mirai 消息引用解析器。
 *
 * mirai 的消息引用的序列化规范是：
 *
 * <ul>
 *     <li>{@link MiraiFromFriendMessageReference} - {@code [source:mirai,incoming,friend,$botId,$timestamp,$ids,$internalIds,$fromId,$toId,$message]}</li>
 * </ul>
 *
 * @author Chuanwise
 */
public class MiraiMessageReferenceParsers {
    
    private static int[] parseIntArray(String input) {
        Preconditions.argumentNonEmpty(input, "array");
        Preconditions.argument(input.charAt(0) == '[', "'" + input + "' is not a legal array");
    
        int number = 0;
        final List<Integer> integers = new ArrayList<>();
    
        final int length = input.length();
        for (int i = 1; i < length; i++) {
            final char ch = input.charAt(i);
    
            if (ch == ',') {
                integers.add(number);
                number = 0;
                integers.add(number);
            } else {
                final int thisNumber = ch - '0';
                Preconditions.argument(thisNumber < 10, "illegal number");
        
                number *= 10;
                number += thisNumber;
            }
        }
        
        return Arrays.unbox(integers.toArray(new Integer[0]));
    }
    
    @BasicMessageParser({"source", "mirai", "online", "incoming", "friend", "?", "?", "?", "?", "?", "?", "?"})
    public MiraiOfflineMessageReference parseFromFriendReference(@BasicMessageArgument String botCodeString,
                                                                 @BasicMessageArgument String timestampString,
                                                                 @BasicMessageArgument String idsString,
                                                                 @BasicMessageArgument String internalIdsString,
                                                                 @BasicMessageArgument String fromIdString,
                                                                 @BasicMessageArgument String toIdString,
                                                                 @BasicMessageArgument String miraiCode) {
        
        final long botId = Numbers.parseLong(botCodeString);
        final int timestamp = Numbers.parseInt(timestampString);
        final int[] ids = parseIntArray(idsString);
        final int[] internalIds = parseIntArray(internalIdsString);
        final long fromId = Numbers.parseLong(fromIdString);
        final long toId = Numbers.parseLong(toIdString);
        final MessageChain messageChain = MiraiCode.deserializeMiraiCode(miraiCode);
    
        // build an offline message source
        final MessageSourceBuilder messageSourceBuilder = new MessageSourceBuilder()
            .messages(messageChain)
            .id(ids)
            .internalId(internalIds)
            .time(timestamp);
    
        messageSourceBuilder.setFromId(fromId);
        messageSourceBuilder.setTargetId(toId);
    
        return new MiraiOfflineMessageReference(
            messageSourceBuilder.build(botId, MessageSourceKind.FRIEND)
        );
    }
    
    @BasicMessageParser({"source", "mirai", "online", "incoming", "member", "?", "?", "?", "?", "?", "?", "?"})
    public MiraiOfflineMessageReference parseFromMemberReference(@BasicMessageArgument String botCodeString,
                                                                 @BasicMessageArgument String timestampString,
                                                                 @BasicMessageArgument String idsString,
                                                                 @BasicMessageArgument String internalIdsString,
                                                                 @BasicMessageArgument String fromIdString,
                                                                 @BasicMessageArgument String toIdString,
                                                                 @BasicMessageArgument String miraiCode) {
        
        final long botId = Numbers.parseLong(botCodeString);
        final int timestamp = Numbers.parseInt(timestampString);
        final int[] ids = parseIntArray(idsString);
        final int[] internalIds = parseIntArray(internalIdsString);
        final long fromId = Numbers.parseLong(fromIdString);
        final long toId = Numbers.parseLong(toIdString);
        final MessageChain messageChain = MiraiCode.deserializeMiraiCode(miraiCode);
    
        // build an offline message source
        final MessageSourceBuilder messageSourceBuilder = new MessageSourceBuilder()
            .messages(messageChain)
            .id(ids)
            .internalId(internalIds)
            .time(timestamp);
    
        messageSourceBuilder.setFromId(fromId);
        messageSourceBuilder.setTargetId(toId);
        
        return new MiraiOfflineMessageReference(
            messageSourceBuilder.build(botId, MessageSourceKind.TEMP)
        );
    }
    
    @BasicMessageParser({"source", "mirai", "online", "incoming", "group", "?", "?", "?", "?", "?", "?", "?"})
    public MiraiOfflineMessageReference parseFromGroupReference(@BasicMessageArgument String botCodeString,
                                                                @BasicMessageArgument String timestampString,
                                                                @BasicMessageArgument String idsString,
                                                                @BasicMessageArgument String internalIdsString,
                                                                @BasicMessageArgument String fromIdString,
                                                                @BasicMessageArgument String toIdString,
                                                                @BasicMessageArgument String miraiCode) {
        
        final long botId = Numbers.parseLong(botCodeString);
        final int timestamp = Numbers.parseInt(timestampString);
        final int[] ids = parseIntArray(idsString);
        final int[] internalIds = parseIntArray(internalIdsString);
        final long fromId = Numbers.parseLong(fromIdString);
        final long toId = Numbers.parseLong(toIdString);
        final MessageChain messageChain = MiraiCode.deserializeMiraiCode(miraiCode);
    
        // build an offline message source
        final MessageSourceBuilder messageSourceBuilder = new MessageSourceBuilder()
            .messages(messageChain)
            .id(ids)
            .internalId(internalIds)
            .time(timestamp);
    
        messageSourceBuilder.setFromId(fromId);
        messageSourceBuilder.setTargetId(toId);
        
        return new MiraiOfflineMessageReference(
            messageSourceBuilder.build(botId, MessageSourceKind.GROUP)
        );
    }
}