package cn.codethink.xiaoming.message.parser;

import cn.chuanwise.common.util.Numbers;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.message.element.*;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 默认基础消息解析器
 *
 * @author Chuanwise
 */
public class DefaultBasicMessageParser {
    
    private static final DefaultBasicMessageParser INSTANCE = new DefaultBasicMessageParser();
    
    private DefaultBasicMessageParser() {
    }
    
    public static DefaultBasicMessageParser getInstance() {
        return INSTANCE;
    }
    
    @BasicMessageParser({"text", "?"})
    public Text parseText(@BasicMessageArgument String text) {
        return new Text(text);
    }
    
    @BasicMessageParser({"face", "?"})
    public Face parseFace(@BasicMessageArgument String codeString) {
        return Face.of(Numbers.parseInt(codeString));
    }
    
    @BasicMessageParser({"image", "url", "?"})
    public UrlImage parseUrlImage(@BasicMessageArgument String url) throws MalformedURLException {
        return new UrlImage(new URL(url));
    }
    
    @BasicMessageParser({"at", "singleton", "?"})
    public AtSingleton parseAtSingleton(@BasicMessageArgument String code) {
        return new AtSingleton(Code.parseCode(code));
    }
    
    @BasicMessageParser({"at", "all"})
    public AtAll parseAtAll() {
        return AtAll.INSTANCE;
    }
}
