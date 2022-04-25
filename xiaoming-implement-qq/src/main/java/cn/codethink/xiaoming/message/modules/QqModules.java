package cn.codethink.xiaoming.message.modules;

import cn.chuanwise.common.util.Collections;
import cn.chuanwise.common.util.Strings;
import cn.codethink.xiaoming.Priority;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.QqContact;
import cn.codethink.xiaoming.message.basic.*;
import cn.codethink.xiaoming.message.basic.At;
import cn.codethink.xiaoming.message.basic.AtAll;
import cn.codethink.xiaoming.message.basic.Dice;
import cn.codethink.xiaoming.message.basic.FlashImage;
import cn.codethink.xiaoming.message.basic.Image;
import cn.codethink.xiaoming.message.basic.MusicShare;
import cn.codethink.xiaoming.message.basic.VipFace;
import cn.codethink.xiaoming.message.compound.CompoundMessage;
import cn.codethink.xiaoming.message.metadata.*;
import cn.codethink.xiaoming.message.metadata.MessageMetadata;
import cn.codethink.xiaoming.message.module.MessageModule;
import cn.codethink.xiaoming.message.module.convert.ConvertContext;
import cn.codethink.xiaoming.message.module.convert.Convertor;
import cn.codethink.xiaoming.message.module.deserialize.DeserializeContext;
import cn.codethink.xiaoming.message.module.deserialize.Deserializer;
import cn.codethink.xiaoming.message.module.deserialize.DeserializerValue;
import cn.codethink.xiaoming.message.module.serialize.Serializer;
import cn.codethink.xiaoming.message.module.summary.Summarizer;
import cn.codethink.xiaoming.message.resource.QqImageResource;
import cn.codethink.xiaoming.property.Property;
import cn.codethink.xiaoming.resource.UrlResource;
import cn.codethink.xiaoming.util.MessageCode;
import cn.codethink.xiaoming.util.MessageCodeTexts;
import cn.codethink.xiaoming.util.Qqs;
import lombok.Data;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.*;
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
public class QqModules {
    
    ///////////////////////////////////////////////////////////////////////////
    // origin
    ///////////////////////////////////////////////////////////////////////////
    
    @Serializer(QqOrigin.class)
    List<String> serializeQqOrigin(QqOrigin origin) {
        return Collections.asUnmodifiableList(
            "origin",
            "qq",
            origin.getMessageChain().serializeToMiraiCode()
        );
    }
    
    @Deserializer("origin:qq:??")
    QqOrigin deserializeQqOrigin(@DeserializerValue String miraiCode) {
        return new QqOrigin(MiraiCode.deserializeMiraiCode(miraiCode));
    }
    
    @Summarizer(QqOrigin.class)
    String summaryQqOrigin(QqOrigin origin) {
        return origin.getMessageChain().contentToString();
    }
    
    @Convertor(QqOrigin.class)
    CompoundMessage toXiaoMing(QqOrigin origin,
                               ConvertContext context) {
        
        return Qqs.toXiaoMing(origin.getMessageChain(), context.getProperties());
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // resource
    ///////////////////////////////////////////////////////////////////////////
    
    @Serializer(QqImageResource.class)
    List<String> serializeQqImageResource(QqImageResource resource) {
        return Collections.asUnmodifiableList(
            "resource",
            "qq",
            "image",
            resource.getImage().getImageId()
        );
    }
    
    @Deserializer("resource:qq:image:?")
    QqImageResource deserializeQqImageResource(@DeserializerValue String imageId) {
        return new QqImageResource(net.mamoe.mirai.message.data.Image.fromId(imageId));
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // image
    ///////////////////////////////////////////////////////////////////////////
    
    @Convertor(value = QqImage.class, priority = Priority.HIGH)
    net.mamoe.mirai.message.data.Image toQq(QqImage image) {
        return image.getImage();
    }
    
    @Convertor(ResourceImage.class)
    net.mamoe.mirai.message.data.Image toQq(ResourceImage image, QqContact contact) throws IOException {
        try (InputStream inputStream = image.getResource().open()) {
            return net.mamoe.mirai.contact.Contact.uploadImage(contact.getQqContact(), inputStream);
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
                return CustomFace.of(new QqImage(image));
            }
        }
        return new QqImage(image);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // flash image
    ///////////////////////////////////////////////////////////////////////////
    
    @Convertor(FlashImage.class)
    net.mamoe.mirai.message.data.FlashImage toQq(FlashImage image, ConvertContext context) {
        final net.mamoe.mirai.message.data.Image qqImage =
            MessageModule.convert(image.getImage(), net.mamoe.mirai.message.data.Image.class, context.getProperties());
        
        return new net.mamoe.mirai.message.data.FlashImage(qqImage);
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
    PlainText toQq(Text text) {
        return new PlainText(text.getText());
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // at
    ///////////////////////////////////////////////////////////////////////////
    
    @Convertor(net.mamoe.mirai.message.data.At.class)
    SingletonAt toXiaoMing(net.mamoe.mirai.message.data.At at) {
        return (SingletonAt) At.of(Code.ofLong(at.getTarget()));
    }
    
    @Convertor(SingletonAt.class)
    net.mamoe.mirai.message.data.At toQq(SingletonAt at) {
        return new net.mamoe.mirai.message.data.At(at.getTargetCode().asLong());
    }
    
    @Convertor(net.mamoe.mirai.message.data.AtAll.class)
    AtAll toXiaoMing() {
        return AtAll.getInstance();
    }
    
    @Convertor(AtAll.class)
    net.mamoe.mirai.message.data.AtAll toQq() {
        return net.mamoe.mirai.message.data.AtAll.INSTANCE;
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // face
    ///////////////////////////////////////////////////////////////////////////
    
    @Convertor(PrimitiveFace.class)
    net.mamoe.mirai.message.data.Face toQq(PrimitiveFace face) {
        return new net.mamoe.mirai.message.data.Face(face.getCode());
    }
    
    @Convertor(net.mamoe.mirai.message.data.Face.class)
    PrimitiveFace toXiaoMing(net.mamoe.mirai.message.data.Face face) {
        return PrimitiveFace.of(face.getId());
    }
    
    @Convertor(CustomFace.class)
    net.mamoe.mirai.message.data.Image toQq(CustomFace face, ConvertContext context) {
        return MessageModule.convert(face.getImage(), net.mamoe.mirai.message.data.Image.class, context.getProperties());
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // dice
    ///////////////////////////////////////////////////////////////////////////
    
    @Convertor(value = Dice.class, priority = Priority.HIGH)
    net.mamoe.mirai.message.data.Dice toQq(Dice dice) {
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
    net.mamoe.mirai.message.data.MusicShare toQq(MusicShare musicShare) {
        return new net.mamoe.mirai.message.data.MusicShare(
            Qqs.toQq(musicShare.getSoftwareType()),
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
            Qqs.toXiaoMing(musicShare.getKind()),
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
    ForwardMessage toQq(Forward forward, ConvertContext context) {
    
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
                    Qqs.toQq(x.getMessage().asCompoundMessage(), context.getProperties())
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
                    Qqs.toXiaoMing(x.getMessageChain(), context.getProperties())
                ))
                .collect(Collectors.toList())
        );
    }
    
    // TODO: 2022/4/18 audio, file, app, service
    
    ///////////////////////////////////////////////////////////////////////////
    // message source
    ///////////////////////////////////////////////////////////////////////////
    
    @Serializer(AbstractQqOnlineMessageSource.class)
    List<String> serializeOnlineMessageSource(AbstractQqOnlineMessageSource source) {
        final net.mamoe.mirai.message.data.OnlineMessageSource qqMessageSource = source.getQqMessageSource();
    
        return Collections.asUnmodifiableList(
            "source",
            "qq",
            source instanceof OutgoingOnlineMessageSource ? "outgoing" : "incoming",
            source.getMessageSourceType().toString().toLowerCase(),
            Long.toString(qqMessageSource.getBotId()),
            Integer.toString(qqMessageSource.getTime()),
            Arrays.toString(qqMessageSource.getIds()),
            Arrays.toString(qqMessageSource.getInternalIds()),
            Long.toString(qqMessageSource.getFromId()),
            Long.toString(qqMessageSource.getTargetId()),
            qqMessageSource.getOriginalMessage().serializeToMiraiCode()
        );
    }
    
    @Serializer(QqOfflineMessageSource.class)
    List<String> serializeOfflineMessageSource(QqOfflineMessageSource source) {
        final net.mamoe.mirai.message.data.OfflineMessageSource qqMessageSource = source.getQqMessageSource();
    
        return Collections.asUnmodifiableList(
            "source",
            "qq",
            "offline",
            source.getMessageSourceType().toString().toLowerCase(),
            Long.toString(qqMessageSource.getBotId()),
            Integer.toString(qqMessageSource.getTime()),
            Arrays.toString(qqMessageSource.getIds()),
            Arrays.toString(qqMessageSource.getInternalIds()),
            Long.toString(qqMessageSource.getFromId()),
            Long.toString(qqMessageSource.getTargetId()),
            qqMessageSource.getOriginalMessage().serializeToMiraiCode()
        );
    }
    
    @Deserializer("source:qq:?:?:?:?:?:?:?:?:??")
    QqOfflineMessageSource deserializeOnlineMessageSource(@DeserializerValue String type,
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
            .messages(Qqs.toQq(
                MessageCode.deserializeMessageCode(message, properties),
                properties
            ))
            .time(Integer.parseInt(time));
        
        builder.setFromId(Long.parseLong(fromIdString));
        builder.setTargetId(Long.parseLong(toIdString));
    
        return new QqOfflineMessageSource(
            builder.build(Long.parseLong(botCodeString), kind),
            properties
        );
    }
    
    @Convertor(AbstractQqOnlineMessageSource.class)
    MessageSource toQq(AbstractQqOnlineMessageSource source,
                          ConvertContext context) {
        return Qqs.toQq(source, context.getProperties());
    }
    
    @Convertor(MessageSource.class)
    cn.codethink.xiaoming.message.metadata.MessageSource toXiaoMing(MessageSource source,
                                                                    ConvertContext context) {
        return Qqs.toXiaoMing(source, context.getProperties());
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
    net.mamoe.mirai.message.data.VipFace toQq(VipFace face) {
        return new net.mamoe.mirai.message.data.VipFace(
            Qqs.toQq(face.getType()),
            face.getCount()
        );
    }
    
    @Convertor(net.mamoe.mirai.message.data.VipFace.class)
    VipFace toXiaoMing(net.mamoe.mirai.message.data.VipFace face) {
        return new VipFace(
            Qqs.toXiaoMing(face.getKind()),
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
    PokeMessage toQq(Poke poke) {
        return new PokeMessage(poke.getName(), poke.getType(), poke.getCode());
    }
    
    @Convertor(PokeMessage.class)
    Poke toXiaoMing(PokeMessage pokeMessage) {
        return Poke.of(pokeMessage.getPokeType(), pokeMessage.getId());
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // market face
    ///////////////////////////////////////////////////////////////////////////
    
    @Serializer(QqMarketFace.class)
    List<String> serializeQqMarketFace(QqMarketFace face) {
        return Collections.asUnmodifiableList(
            "face",
            "market",
            "qq",
            Integer.toString(face.getCode()),
            face.getName()
        );
    }
    
    @Deserializer("face:market:qq:?:?")
    QqMarketFace deserializeQqMarketFace(@DeserializerValue String codeString,
                                            @DeserializerValue String name) {
        
        return new QqMarketFace(
            Integer.parseInt(codeString),
            name
        );
    }
    
    @Data
    public static class QqImplementedMarketFace
        implements MarketFace {
    
        private final int id;
        
        private final String name;
    }
    
    @Convertor(QqMarketFace.class)
    public MarketFace toQq(QqMarketFace face) {
        return new QqImplementedMarketFace(
            face.getCode(),
            face.getName()
        );
    }
    
    @Convertor(MarketFace.class)
    public cn.codethink.xiaoming.message.basic.MarketFace toXiaoMing(MarketFace face) {
        return new QqMarketFace(
            face.getId(),
            face.getName()
        );
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // quote
    ///////////////////////////////////////////////////////////////////////////
    
    @Convertor(Quote.class)
    QuoteReply toQq(Quote quote, ConvertContext context) {
        final MessageSource source = Qqs.toQq(quote.getMessageSource(), context.getProperties());
        return new QuoteReply(source);
    }
    
    @Convertor(QuoteReply.class)
    Quote toXiaoMing(QuoteReply quoteReply, ConvertContext context) {
        final cn.codethink.xiaoming.message.metadata.MessageSource source = Qqs.toXiaoMing(quoteReply.getSource(), context.getProperties());
        return Quote.of(source);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // qq origin
    ///////////////////////////////////////////////////////////////////////////
    
    @Convertor(value = MessageOrigin.class, targets = {
        MessageMetadata.class,
        BasicMessage.class
    })
    Object toXiaoMing(MessageOrigin origin, ConvertContext context) {
        final MessageChain singletonMessage = new MessageChainBuilder(1)
            .append(origin.getOrigin())
            .asMessageChain();
    
        final CompoundMessage compoundMessage = Qqs.toXiaoMing(singletonMessage, context.getProperties());
        if (compoundMessage.isEmpty()) {
            return compoundMessage.getMetadata().values().iterator().next();
        } else {
            return compoundMessage.get(0);
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // service
    ///////////////////////////////////////////////////////////////////////////
    
    @Serializer(SimpleQqService.class)
    List<String> serializeQqService(SimpleQqService service) {
        return Collections.asUnmodifiableList(
            "service",
            "qq",
            Integer.toString(service.getCode()),
            service.getContent()
        );
    }
    
    @Deserializer("service:json:?:?")
    SimpleQqService deserializeQqService(@DeserializerValue String codeString,
                                         @DeserializerValue String json) {
        
        return new SimpleQqService(Integer.parseInt(codeString), json);
    }
    
    @Summarizer(SimpleQqService.class)
    String summarySimpleQqService() {
        return "[服务消息]";
    }
    
    @Convertor(SimpleQqService.class)
    ServiceMessage toQq(SimpleQqService service) {
        return new SimpleServiceMessage(service.getCode(), service.getContent());
    }
    
    @Convertor(value = ServiceMessage.class)
    SimpleQqService toXiaoMing(ServiceMessage message) {
        return new SimpleQqService(message.getServiceId(), message.getContent());
    }
}