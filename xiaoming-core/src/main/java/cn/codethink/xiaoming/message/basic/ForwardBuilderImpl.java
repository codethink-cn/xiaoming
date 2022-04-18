package cn.codethink.xiaoming.message.basic;

import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.message.Message;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Chuanwise
 *
 * @see cn.codethink.xiaoming.message.basic.ForwardBuilder
 */
public class ForwardBuilderImpl
    implements ForwardBuilder {
    
    public static final int MAX_PREVIEW_LENGTH = 3;
    
    private final List<ForwardElement> elements;
    
    private String title;
    
    private String description;
    
    private String source;
    
    private String summary;
    
    private List<String> preview;
    
    public ForwardBuilderImpl() {
        elements = new ArrayList<>();
    }
    
    @Override
    public ForwardBuilder plus(Code senderCode, String senderName, Message message) {
        Preconditions.objectNonNull(senderCode, "sender code");
        Preconditions.objectArgumentNonEmpty(senderName, "sender name");
        Preconditions.objectNonNull(message, "message");
    
        final ForwardElementImpl element;
        
        if (elements.isEmpty()) {
            element = new ForwardElementImpl(senderCode, senderName, System.currentTimeMillis(), message);
        } else {
            final ForwardElement lastElement = elements.get(elements.size() - 1);
            element = new ForwardElementImpl(senderCode, senderName,
                lastElement.getTimestamp() + TimeUnit.SECONDS.toMillis(1), message);
        }
        elements.add(element);
        
        return this;
    }
    
    @Override
    public ForwardBuilder plus(ForwardElement element) {
        Preconditions.objectNonNull(element, "forward element");
        
        elements.add(element);
        
        return this;
    }
    
    @Override
    public ForwardBuilder title(String title) {
        Preconditions.objectArgumentNonEmpty(title, "title");
    
        this.title = title;
    
        return this;
    }
    
    @Override
    public ForwardBuilder description(String description) {
        Preconditions.objectArgumentNonEmpty(description, "description");
    
        this.description = description;
    
        return this;
    }
    
    @Override
    public ForwardBuilder summary(String summary) {
        Preconditions.objectArgumentNonEmpty(summary, "summary");

        this.summary = summary;
        
        return this;
    }
    
    @Override
    public ForwardBuilder source(String source) {
        Preconditions.objectArgumentNonEmpty(source, "source");
        
        this.source = source;
        
        return this;
    }
    
    @Override
    public ForwardBuilder preview(List<String> preview) {
        Preconditions.objectArgumentNonEmpty(preview, "preview");
        
        this.preview = preview;
        
        return this;
    }
    
    @Override
    public Forward build() {
        Preconditions.state(!elements.isEmpty(), "elements is empty!");
        
        // sort based on timestamp
        elements.sort(Comparator.comparingLong(ForwardElement::getTimestamp));
        
        if (Objects.isNull(title)) {
            title = "群聊的消息记录";
        }
        if (Objects.isNull(description)) {
            description = "查看 " + elements.size() + " 条转发消息";
        }
        if (Objects.isNull(source)) {
            source = "聊天记录";
        }
        if (Objects.isNull(summary)) {
            summary = "[群聊的聊天记录]";
        }
        
        if (Objects.isNull(preview)) {
            preview = new ArrayList<>(MAX_PREVIEW_LENGTH);
    
            final int size = Math.min(MAX_PREVIEW_LENGTH, elements.size());
            for (int i = 0; i < size; i++) {
                final ForwardElement element = elements.get(i);
                
                preview.add(element.getSenderName() + "：" +
                    element.getMessage().asCompoundMessage().serializeToMessageSummary());
            }
        } else if (preview.size() > MAX_PREVIEW_LENGTH) {
            preview = preview.subList(0, MAX_PREVIEW_LENGTH);
        }
        
        return new ForwardImpl(title, description, source, summary, preview, elements);
    }
}
