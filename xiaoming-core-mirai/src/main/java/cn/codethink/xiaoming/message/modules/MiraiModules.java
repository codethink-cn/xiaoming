package cn.codethink.xiaoming.message.modules;

import cn.chuanwise.common.util.Collections;
import cn.chuanwise.common.util.Strings;
import cn.codethink.xiaoming.Priority;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.MiraiContact;
import cn.codethink.xiaoming.message.basic.*;
import cn.codethink.xiaoming.message.basic.Dice;
import cn.codethink.xiaoming.message.basic.FlashImage;
import cn.codethink.xiaoming.message.basic.Image;
import cn.codethink.xiaoming.message.basic.MusicShare;
import cn.codethink.xiaoming.message.basic.VipFace;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.message.metadata.*;
import cn.codethink.xiaoming.message.module.MessageModule;
import cn.codethink.xiaoming.message.module.convert.ConvertContext;
import cn.codethink.xiaoming.message.module.convert.Convertor;
import cn.codethink.xiaoming.message.module.deserialize.DeserializeContext;
import cn.codethink.xiaoming.message.module.deserialize.Deserializer;
import cn.codethink.xiaoming.message.module.deserialize.DeserializerValue;
import cn.codethink.xiaoming.message.module.serialize.Serializer;
import cn.codethink.xiaoming.message.module.summary.Summarizer;
import cn.codethink.xiaoming.message.resource.MiraiImageResource;
import cn.codethink.xiaoming.property.Property;
import cn.codethink.xiaoming.resource.UrlResource;
import cn.codethink.xiaoming.util.MessageCode;
import cn.codethink.xiaoming.util.MessageCodeTexts;
import cn.codethink.xiaoming.util.Mirais;
import lombok.Data;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MarketFace;
import net.mamoe.mirai.message.data.MessageSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Chuanwise
 */
public class MiraiModules {
    
    ///////////////////////////////////////////////////////////////////////////
    // origin
    ///////////////////////////////////////////////////////////////////////////
    
    @Serializer(MiraiOrigin.class)
    List<String> serializeMiraiOrigin(MiraiOrigin origin) {
        return Collections.asUnmodifiableList(
            "origin",
            "mirai",
            origin.getMessageChain().serializeToMiraiCode()
        );
    }
    
    @Deserializer("origin:mirai:??")
    MiraiOrigin deserializeMiraiOrigin(@DeserializerValue String miraiCode) {
        return new MiraiOrigin(MiraiCode.deserializeMiraiCode(miraiCode));
    }
    
    @Summarizer(MiraiOrigin.class)
    String summaryMiraiOrigin(MiraiOrigin origin) {
        return origin.getMessageChain().contentToString();
    }
    
    @Convertor(MiraiOrigin.class)
    CompoundMessage toXiaoMing(MiraiOrigin origin,
                               ConvertContext context) {
        
        return Mirais.toXiaoMing(origin.getMessageChain(), context.getProperties());
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // resource
    ///////////////////////////////////////////////////////////////////////////
    
    @Serializer(MiraiImageResource.class)
    List<String> serializeMiraiImageResource(MiraiImageResource resource) {
        return Collections.asUnmodifiableList(
            "resource",
            "mirai",
            "image",
            resource.getImage().getImageId()
        );
    }
    
    @Deserializer("resource:mirai:image:?")
    MiraiImageResource deserializeMiraiImageResource(@DeserializerValue String imageId) {
        return new MiraiImageResource(net.mamoe.mirai.message.data.Image.fromId(imageId));
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // image
    ///////////////////////////////////////////////////////////////////////////
    
    @Convertor(value = MiraiImage.class, priority = Priority.HIGH)
    net.mamoe.mirai.message.data.Image toMirai(MiraiImage image) {
        return image.getImage();
    }
    
    @Convertor(ResourceImage.class)
    net.mamoe.mirai.message.data.Image toMirai(ResourceImage image, MiraiContact contact) throws IOException {
        try (InputStream inputStream = image.getResource().open()) {
            return net.mamoe.mirai.contact.Contact.uploadImage(contact.getMiraiContact(), inputStream);
        }
    }
    
    @Convertor(value = net.mamoe.mirai.message.data.Image.class, targets = {
        CustomFace.class,
        Image.class
    })
    Object toXiaoMing(net.mamoe.mirai.message.data.Image image) throws MalformedURLException {
        if (image.isEmoji()) {
            final String url = net.mamoe.mirai.message.data.Image.queryUrl(image);
    
            if (Strings.nonEmpty(url)) {
                return CustomFace.of(ResourceImage.of(new UrlResource(new URL(url))));
            } else {
                return CustomFace.of(new MiraiImage(image));
            }
        }
        return new MiraiImage(image);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // flash image
    ///////////////////////////////////////////////////////////////////////////
    
    @Convertor(FlashImage.class)
    net.mamoe.mirai.message.data.FlashImage toMirai(FlashImage image, ConvertContext context) {
        final net.mamoe.mirai.message.data.Image miraiImage =
            MessageModule.convert(image.getImage(), net.mamoe.mirai.message.data.Image.class, context.getProperties());
        
        return new net.mamoe.mirai.message.data.FlashImage(miraiImage);
    }
    
    @Convertor(net.mamoe.mirai.message.data.FlashImage.class)
    FlashImage toXiaoMing(net.mamoe.mirai.message.data.FlashImage image, ConvertContext context) {
        final Image newImage = MessageModule.convert(image.getImage(), Image.class, context.getProperties());
        return FlashImage.of(newImage);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // text
    ///////////////////////////////////////////////////////////////////////////
    
    @Convertor(PlainText.class)
    Text toXiaoMing(PlainText text) {
        return Text.of(text.getContent());
    }
    
    @Convertor(Text.class)
    PlainText toMirai(Text text) {
        return new PlainText(text.getText());
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // at
    ///////////////////////////////////////////////////////////////////////////
    
    @Convertor(At.class)
    SingletonAccountAt toXiaoMing(At at) {
        return (SingletonAccountAt) AccountAt.singleton(Code.ofLong(at.getTarget()));
    }
    
    @Convertor(SingletonAccountAt.class)
    At toMirai(SingletonAccountAt at) {
        return new At(at.getTargetCode().asLong());
    }
    
    @Convertor(AtAll.class)
    AllAccountAt toXiaoMing() {
        return AllAccountAt.getInstance();
    }
    
    @Convertor(AllAccountAt.class)
    AtAll toMirai() {
        return AtAll.INSTANCE;
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // face
    ///////////////////////////////////////////////////////////////////////////
    
    @Convertor(PrimitiveFace.class)
    net.mamoe.mirai.message.data.Face toMirai(PrimitiveFace face) {
        return new net.mamoe.mirai.message.data.Face(face.getCode());
    }
    
    @Convertor(net.mamoe.mirai.message.data.Face.class)
    PrimitiveFace toXiaoMing(net.mamoe.mirai.message.data.Face face) {
        return PrimitiveFace.of(face.getId());
    }
    
    @Convertor(CustomFace.class)
    net.mamoe.mirai.message.data.Image toMirai(CustomFace face, ConvertContext context) {
        return MessageModule.convert(face.getImage(), net.mamoe.mirai.message.data.Image.class, context.getProperties());
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // dice
    ///////////////////////////////////////////////////////////////////////////
    
    @Convertor(value = Dice.class, priority = Priority.HIGH)
    net.mamoe.mirai.message.data.Dice toMirai(Dice dice) {
        return new net.mamoe.mirai.message.data.Dice(dice.getValue());
    }
    
    @Convertor(value = net.mamoe.mirai.message.data.Dice.class, priority = Priority.HIGH)
    Dice toXiaoMing(net.mamoe.mirai.message.data.Dice dice) {
        return Dice.of(dice.getValue());
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // music share
    ///////////////////////////////////////////////////////////////////////////
    
    @Convertor(MusicShare.class)
    net.mamoe.mirai.message.data.MusicShare toMirai(MusicShare musicShare) {
        return new net.mamoe.mirai.message.data.MusicShare(
            Mirais.toMirai(musicShare.getSoftwareType()),
            musicShare.getTitle(),
            musicShare.getDescription(),
            musicShare.getJumpUrl(),
            musicShare.getCoverUrl(),
            musicShare.getMusicUrl()
        );
    }
    
    @Convertor(net.mamoe.mirai.message.data.MusicShare.class)
    MusicShare toXiaoMing(net.mamoe.mirai.message.data.MusicShare musicShare) {
        return MusicShare.newInstance(
            Mirais.toXiaoMing(musicShare.getKind()),
            musicShare.getTitle(),
            musicShare.getSummary(),
            musicShare.getBrief(),
            musicShare.getJumpUrl(),
            musicShare.getPictureUrl(),
            musicShare.getMusicUrl()
        );
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // forward
    ///////////////////////////////////////////////////////////////////////////
    
    @Convertor(Forward.class)
    ForwardMessage toMirai(Forward forward, ConvertContext context) {
    
        return new ForwardMessage(
            forward.getPreview(),
            forward.getTitle(),
            forward.getSummary(),
            forward.getSource(),
            forward.getDescription(),
            forward.getElements().stream()
                .map(x -> new ForwardMessage.Node(
                    x.getSenderCode().asLong(),
                    (int) TimeUnit.MILLISECONDS.toSeconds(x.getTimestamp()),
                    x.getSenderName(),
                    Mirais.toMirai(x.getMessage().asCompoundMessage(), context.getProperties())
                ))
                .collect(Collectors.toList())
        );
    }
    
    @Convertor(ForwardMessage.class)
    Forward toXiaoMing(ForwardMessage message, ConvertContext context) {
        
        return new ForwardImpl(
            message.getTitle(),
            message.getSummary(),
            message.getSource(),
            message.getBrief(),
            message.getPreview(),
            message.getNodeList()
                .stream()
                .map(x -> new ForwardElementImpl(
                    Code.ofLong(x.getSenderId()),
                    x.getSenderName(),
                    TimeUnit.SECONDS.toMillis(x.getTime()),
                    Mirais.toXiaoMing(x.getMessageChain(), context.getProperties())
                ))
                .collect(Collectors.toList())
        );
    }
    
    // TODO: 2022/4/18 audio, file, app, service
    
    ///////////////////////////////////////////////////////////////////////////
    // message source
    ///////////////////////////////////////////////////////////////////////////
    
    @Serializer(AbstractMiraiOnlineMessageSource.class)
    List<String> serializeOnlineMessageSource(AbstractMiraiOnlineMessageSource source) {
        final net.mamoe.mirai.message.data.OnlineMessageSource miraiMessageSource = source.getMiraiMessageSource();
    
        return Collections.asUnmodifiableList(
            "source",
            "mirai",
            source instanceof OutgoingOnlineMessageSource ? "outgoing" : "incoming",
            source.getMessageSourceType().toString().toLowerCase(),
            Long.toString(miraiMessageSource.getBotId()),
            Integer.toString(miraiMessageSource.getTime()),
            Arrays.toString(miraiMessageSource.getIds()),
            Arrays.toString(miraiMessageSource.getInternalIds()),
            Long.toString(miraiMessageSource.getFromId()),
            Long.toString(miraiMessageSource.getTargetId()),
            miraiMessageSource.getOriginalMessage().serializeToMiraiCode()
        );
    }
    
    @Serializer(MiraiOfflineMessageSource.class)
    List<String> serializeOfflineMessageSource(MiraiOfflineMessageSource source) {
        final net.mamoe.mirai.message.data.OfflineMessageSource miraiMessageSource = source.getMiraiMessageSource();
    
        return Collections.asUnmodifiableList(
            "source",
            "mirai",
            "offline",
            source.getMessageSourceType().toString().toLowerCase(),
            Long.toString(miraiMessageSource.getBotId()),
            Integer.toString(miraiMessageSource.getTime()),
            Arrays.toString(miraiMessageSource.getIds()),
            Arrays.toString(miraiMessageSource.getInternalIds()),
            Long.toString(miraiMessageSource.getFromId()),
            Long.toString(miraiMessageSource.getTargetId()),
            miraiMessageSource.getOriginalMessage().serializeToMiraiCode()
        );
    }
    
    @Deserializer("source:mirai:?:?:?:?:?:?:?:?:??")
    MiraiOfflineMessageSource deserializeOnlineMessageSource(@DeserializerValue String type,
                                                             @DeserializerValue String target,
                                                             @DeserializerValue String botCodeString,
                                                             @DeserializerValue String time,
                                                             @DeserializerValue String idsString,
                                                             @DeserializerValue String internalIdsString,
                                                             @DeserializerValue String fromIdString,
                                                             @DeserializerValue String toIdString,
                                                             @DeserializerValue String message,
                                                             DeserializeContext context) {
        
        final MessageSourceKind kind = MessageSourceKind.valueOf(target.toUpperCase());
        final Map<Property<?>, Object> properties = context.getProperties();
        final MessageSourceBuilder builder = new MessageSourceBuilder()
            .id(
                cn.chuanwise.common.util.Arrays.unbox(
                    MessageCodeTexts.deserializeComma(idsString).stream()
                        .map(Integer::parseInt)
                        .toArray(Integer[]::new)
                )
            )
            .id(
                cn.chuanwise.common.util.Arrays.unbox(
                    MessageCodeTexts.deserializeComma(internalIdsString).stream()
                        .map(Integer::parseInt)
                        .toArray(Integer[]::new)
                )
            )
            .messages(Mirais.toMirai(
                MessageCode.deserializeMessageCode(message, properties),
                properties
            ))
            .time(Integer.parseInt(time));
        
        builder.setFromId(Long.parseLong(fromIdString));
        builder.setTargetId(Long.parseLong(toIdString));
    
        return new MiraiOfflineMessageSource(
            builder.build(Long.parseLong(botCodeString), kind),
            properties
        );
    }
    
    @Convertor(AbstractMiraiOnlineMessageSource.class)
    MessageSource toMirai(AbstractMiraiOnlineMessageSource source,
                          ConvertContext context) {
        return Mirais.toMirai(source, context.getProperties());
    }
    
    @Convertor(MessageSource.class)
    cn.codethink.xiaoming.message.metadata.MessageSource toXiaoMing(MessageSource source,
                                                                    ConvertContext context) {
        return Mirais.toXiaoMing(source, context.getProperties());
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // vip face
    ///////////////////////////////////////////////////////////////////////////
    
    @Serializer(VipFace.class)
    List<String> serializeVipFace(VipFace face) {
        return Collections.asUnmodifiableList(
            "face",
            "vip",
            Integer.toString(face.getType().getCode()),
            Integer.toString(face.getCount())
        );
    }
    
    @Deserializer("face:vip:?:?")
    VipFace deserializeVipFace(@DeserializerValue String typeCodeString,
                               @DeserializerValue String countString) {
    
        final VipFaceType type = VipFaceType.of(Integer.parseInt(typeCodeString));
        final int count = Integer.parseInt(countString);
        return new VipFace(type, count);
    }
    
    @Summarizer(VipFace.class)
    String summaryVipFace(VipFace face) {
        return "[" + face.getType().getName() + "]";
    }
    
    @Convertor(VipFace.class)
    net.mamoe.mirai.message.data.VipFace toMirai(VipFace face) {
        return new net.mamoe.mirai.message.data.VipFace(
            Mirais.toMirai(face.getType()),
            face.getCount()
        );
    }
    
    @Convertor(net.mamoe.mirai.message.data.VipFace.class)
    VipFace toXiaoMing(net.mamoe.mirai.message.data.VipFace face) {
        return new VipFace(
            Mirais.toXiaoMing(face.getKind()),
            face.getCount()
        );
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // poke
    ///////////////////////////////////////////////////////////////////////////
    
    @Serializer(Poke.class)
    List<String> serializePoke(Poke poke) {
        return Collections.asUnmodifiableList(
            "poke",
            Integer.toString(poke.getType()),
            Integer.toString(poke.getCode())
        );
    }
    
    @Deserializer("poke:?:?")
    Poke deserializePoke(@DeserializerValue String typeString,
                         @DeserializerValue String codeString) {
        return Poke.of(
            Integer.parseInt(typeString),
            Integer.parseInt(codeString)
        );
    }
    
    @Summarizer(Poke.class)
    String summaryPoke() {
        return "[戳一戳消息]";
    }
    
    @Convertor(Poke.class)
    PokeMessage toMirai(Poke poke) {
        return new PokeMessage(poke.getName(), poke.getType(), poke.getCode());
    }
    
    @Convertor(PokeMessage.class)
    Poke toXiaoMing(PokeMessage pokeMessage) {
        return Poke.of(pokeMessage.getPokeType(), pokeMessage.getId());
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // market face
    ///////////////////////////////////////////////////////////////////////////
    
    @Serializer(MiraiMarketFace.class)
    List<String> serializeMiraiMarketFace(MiraiMarketFace face) {
        return Collections.asUnmodifiableList(
            "face",
            "market",
            "mirai",
            Integer.toString(face.getCode()),
            face.getName()
        );
    }
    
    @Deserializer("face:market:mirai:?:?")
    MiraiMarketFace deserializeMiraiMarketFace(@DeserializerValue String codeString,
                                               @DeserializerValue String name) {
        
        return new MiraiMarketFace(
            Integer.parseInt(codeString),
            name
        );
    }
    
    @Data
    public static class MiraiImplementedMarketFace
        implements MarketFace {
    
        private final int id;
        
        private final String name;
    }
    
    @Convertor(MiraiMarketFace.class)
    public MarketFace toMirai(MiraiMarketFace face) {
        return new MiraiImplementedMarketFace(
            face.getCode(),
            face.getName()
        );
    }
    
    @Convertor(MarketFace.class)
    public cn.codethink.xiaoming.message.basic.MarketFace toXiaoMing(MarketFace face) {
        return new MiraiMarketFace(
            face.getId(),
            face.getName()
        );
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // quote
    ///////////////////////////////////////////////////////////////////////////
    
    @Convertor(Quote.class)
    QuoteReply toMirai(Quote quote, ConvertContext context) {
        final MessageSource source = Mirais.toMirai(quote.getMessageSource(), context.getProperties());
        return new QuoteReply(source);
    }
    
    @Convertor(QuoteReply.class)
    Quote toXiaoMing(QuoteReply quoteReply, ConvertContext context) {
        final cn.codethink.xiaoming.message.metadata.MessageSource source = Mirais.toXiaoMing(quoteReply.getSource(), context.getProperties());
        return Quote.of(source);
    }
}