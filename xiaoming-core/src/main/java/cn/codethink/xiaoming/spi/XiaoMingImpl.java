package cn.codethink.xiaoming.spi;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.annotation.InternalAPI;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.message.AutoSummarizable;
import cn.codethink.xiaoming.message.Message;
import cn.codethink.xiaoming.message.basic.*;
import cn.codethink.xiaoming.message.compound.*;
import cn.codethink.xiaoming.message.metadata.MessageSource;
import cn.codethink.xiaoming.message.metadata.Quote;
import cn.codethink.xiaoming.message.metadata.QuoteImpl;
import cn.codethink.xiaoming.message.module.MessageModuleImpl;
import cn.codethink.xiaoming.message.module.convert.ConvertContext;
import cn.codethink.xiaoming.message.module.convert.ConvertContextImpl;
import cn.codethink.xiaoming.message.module.deserialize.DeserializeContext;
import cn.codethink.xiaoming.message.module.deserialize.DeserializeContextImpl;
import cn.codethink.xiaoming.message.module.modules.*;
import cn.codethink.xiaoming.property.CustomGetterProperty;
import cn.codethink.xiaoming.property.Property;
import cn.codethink.xiaoming.message.module.serialize.SerializeContext;
import cn.codethink.xiaoming.message.module.serialize.SerializeContextImpl;
import cn.codethink.xiaoming.message.module.summary.SummaryContext;
import cn.codethink.xiaoming.message.module.summary.SummaryContextImpl;
import cn.codethink.xiaoming.property.SimpleProperty;
import cn.codethink.xiaoming.resource.*;
import com.google.auto.service.AutoService;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * 核心服务
 *
 * @author Chuanwise
 */
@InternalAPI
@AutoService(XiaoMing.class)
public class XiaoMingImpl
    implements XiaoMing {
    
    static {
        
        // if no xiaoming present, set one
        if (!XiaoMingSpi.isPresent()) {
            XiaoMingSpi.setXiaoMing(new XiaoMingImpl());
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // property
    ///////////////////////////////////////////////////////////////////////////
    
    @Override
    public <T> Property<T> newProperty() {
        return new SimpleProperty<>();
    }
    
    @Override
    public <T> Property<T> newProperty(Function<Map<Property<?>, Object>, T> getter) {
        return new CustomGetterProperty<>(getter);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // forward
    ///////////////////////////////////////////////////////////////////////////
    
    @Override
    public ForwardElement newForwardElement(Code senderCode, String senderName, long timestamp, Message message) {
        return new ForwardElementImpl(senderCode, senderName, timestamp, message);
    }
    
    @Override
    public ForwardBuilder newForwardBuilder() {
        return new ForwardBuilderImpl();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // resource
    ///////////////////////////////////////////////////////////////////////////
    
    @Override
    public ResourceImage newResourceImage(Resource resource) {
        return new ResourceImageImpl(resource);
    }
    
    @Override
    public Resource newResource(URL url) {
        return new UrlResource(url);
    }
    
    @Override
    public Resource newResource(Class<?> clazz, String path) {
        return new ClassPathResource(clazz, path);
    }
    
    @Override
    public Resource newResource(File file) {
        return new FileResource(file);
    }
    
    @Override
    public Resource newResource(byte[] bytes) {
        return new ByteArrayResource(bytes);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // single message
    ///////////////////////////////////////////////////////////////////////////
    
    @Override
    public Text newText(String text) {
        return new TextImpl(text);
    }
    
    @Override
    public AllAccountMention getAllAccountMention() {
        return AllAccountMentionImpl.INSTANCE;
    }
    
    @Override
    public SingletonAccountMention newSingletonAccountMention(Code targetCode) {
        return new SingletonAccountMentionImpl(targetCode);
    }
    
    @Override
    public MusicShare newMusicShare(MusicSoftwareType softwareType, String title, String description, String summary, String jumpUrl, String coverUrl, String musicUrl) {
        return new MusicShareImpl(softwareType, title, description, summary, jumpUrl, coverUrl, musicUrl);
    }
    
    @Override
    public FlashImage newFlashImage(Image image) {
        return new FlashImageImpl(image);
    }
    
    @Override
    public CustomFace newCustomFace(Image image) {
        return new CustomFaceImpl(image);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // message metadata
    ///////////////////////////////////////////////////////////////////////////
    
    @Override
    public Quote newQuote(MessageSource source) {
        return new QuoteImpl(source);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // message builder
    ///////////////////////////////////////////////////////////////////////////
    
    @Override
    public CompoundMessage newCompoundMessage(BasicMessage basicMessage) {
        return new SingletonCompoundMessage(basicMessage);
    }
    
    @Override
    public CompoundMessageBuilder newCompoundMessageBuilder() {
        return new SimpleCompoundMessageBuilder();
    }
    
    @Override
    public CompoundMessageBuilder copyAsCompoundMessageBuilder(CompoundMessage compoundMessage) {
        return new SimpleCompoundMessageBuilder(compoundMessage);
    }
    
    @Override
    public CompoundMessageBuilder newCompoundMessageBuilder(int capacity) {
        return new SimpleCompoundMessageBuilder(capacity);
    }
    
    @Override
    public CompoundMessageBuilder newLazyCompoundMessageBuilder(CompoundMessage compoundMessage) {
        return new LazyCompoundMessageBuilder(compoundMessage);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // message module
    ///////////////////////////////////////////////////////////////////////////
    
    @Override
    public boolean unregisterMessageModule(Object module) {
        return MessageModuleImpl.unregisterModule(module);
    }
    
    @Override
    public boolean unregisterMessageModule(Class<?> moduleClass) {
        return MessageModuleImpl.unregisterModule(moduleClass);
    }
    
    @Override
    public void registerMessageModule(Object module) {
        MessageModuleImpl.registerModule(module);
    }
    
    @Override
    @SuppressWarnings("all")
    public <T> T convert(Object source, Class<T> targetClass, Map<Property<?>, Object> properties) {
        Preconditions.objectNonNull(source, "source");
        Preconditions.objectNonNull(properties, "properties");
    
        final ConvertContext context = new ConvertContextImpl(source, targetClass, properties);
        return (T) MessageModuleImpl.convert(context);
    }
    
    @Override
    public String summary(AutoSummarizable source, Map<Property<?>, Object> properties) {
        Preconditions.objectNonNull(source, "source");
        Preconditions.objectNonNull(properties, "properties");
    
        final SummaryContext context = new SummaryContextImpl(source, properties);
        return MessageModuleImpl.summary(context);
    }
    
    @Override
    public List<String> serialize(Object source, Map<Property<?>, Object> properties) {
        Preconditions.objectNonNull(source, "source");
        Preconditions.objectNonNull(properties, "properties");
    
        final SerializeContext context = new SerializeContextImpl(source, properties);
        return MessageModuleImpl.serialize(context);
    }
    
    @Override
    public Object deserialize(List<String> arguments, Map<Property<?>, Object> properties) {
        Preconditions.objectNonNull(arguments, "arguments");
        Preconditions.objectNonNull(properties, "properties");
    
        final DeserializeContext context = new DeserializeContextImpl(arguments, properties);
        return MessageModuleImpl.deserialize(context);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // image type
    ///////////////////////////////////////////////////////////////////////////
    
    @Override
    public ImageType newImageType(String extension) {
        return new ImageTypeImpl(extension);
    }
    
    @Override
    public ImageType getImageType(String extension) {
        return ImageTypeImpl.getImageType(extension);
    }
    
    @Override
    public Set<ImageType> getImageTypes() {
        return ImageTypeImpl.getInstances();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // call back
    ///////////////////////////////////////////////////////////////////////////
    
    @Override
    public void onRegister() {
        // register modules
        MessageModuleImpl.registerModule(new DiceModules());
        MessageModuleImpl.registerModule(new FaceModules());
        MessageModuleImpl.registerModule(new FlashImageModules());
        MessageModuleImpl.registerModule(new ForwardModules());
        MessageModuleImpl.registerModule(new ImageModules());
        MessageModuleImpl.registerModule(new MentionModules());
        MessageModuleImpl.registerModule(new MetadataModules());
        MessageModuleImpl.registerModule(new MusicShareModules());
        MessageModuleImpl.registerModule(new ResourceModules());
        MessageModuleImpl.registerModule(new TextModules());
    }
    
    @Override
    public void onDeregister() {
        // unregister modules
        MessageModuleImpl.unregisterModule(DiceModules.class);
        MessageModuleImpl.unregisterModule(FaceModules.class);
        MessageModuleImpl.unregisterModule(FlashImageModules.class);
        MessageModuleImpl.unregisterModule(ForwardModules.class);
        MessageModuleImpl.unregisterModule(ImageModules.class);
        MessageModuleImpl.unregisterModule(MentionModules.class);
        MessageModuleImpl.unregisterModule(MetadataModules.class);
        MessageModuleImpl.unregisterModule(MusicShareModules.class);
        MessageModuleImpl.unregisterModule(ResourceModules.class);
        MessageModuleImpl.unregisterModule(TextModules.class);
    }
}
