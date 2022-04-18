package cn.codethink.xiaoming.message.module.modules;

import cn.codethink.xiaoming.message.metadata.MessageSource;
import cn.codethink.xiaoming.message.metadata.Quote;
import cn.codethink.xiaoming.message.module.MessageModule;
import cn.codethink.xiaoming.message.module.convert.Convertor;
import cn.codethink.xiaoming.message.module.deserialize.DeserializeContext;
import cn.codethink.xiaoming.message.module.deserialize.Deserializer;
import cn.codethink.xiaoming.message.module.deserialize.DeserializerValue;
import cn.codethink.xiaoming.message.module.serialize.SerializeContext;
import cn.codethink.xiaoming.message.module.serialize.Serializer;
import cn.codethink.xiaoming.util.MessageCodeTexts;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.metadata.MessageMetadata
 * @see cn.codethink.xiaoming.message.metadata.MessageMetadataType
 */
public class MetadataModules {
    
    ///////////////////////////////////////////////////////////////////////////
    // quote
    ///////////////////////////////////////////////////////////////////////////
    
    @Serializer(Quote.class)
    List<String> serializeQuote(Quote quote, SerializeContext context) {
        final MessageSource messageSource = quote.getMessageSource();
        final List<String> list = new ArrayList<>(MessageModule.serialize(messageSource, context.getProperties()));
        
        list.set(0, "quote");
        return list;
    }
    
    @Deserializer("quote:??")
    Quote deserializeQuote(@DeserializerValue String param,
                           DeserializeContext context) {
        
        final List<String> arguments = new ArrayList<>(MessageCodeTexts.parseArguments(param));
        arguments.set(0, "source");
        final MessageSource source = (MessageSource) MessageModule.deserialize(arguments, context.getProperties());
        return Quote.of(source);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // message reference
    ///////////////////////////////////////////////////////////////////////////
    
    
}
