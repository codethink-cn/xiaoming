package cn.codethink.xiaoming.message.parser;

import cn.chuanwise.common.util.Numbers;
import cn.chuanwise.common.util.Preconditions;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.message.basic.*;

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
    
    @MessageParser({"text", "?"})
    public Text parseText(@ParserArgument String text) {
        return new Text(text);
    }
    
    @MessageParser({"face", "?"})
    public Face parseFace(@ParserArgument String codeString) {
        return Face.of(Numbers.parseInt(codeString));
    }
    
    @MessageParser({"image", "url", "?"})
    public UrlImage parseUrlImage(@ParserArgument String url) throws MalformedURLException {
        return new UrlImage(new URL(url));
    }
    
    @MessageParser({"at", "singleton", "?"})
    public AtSingleton parseAtSingleton(@ParserArgument String code) {
        return new AtSingleton(Code.parseCode(code));
    }
    
    @MessageParser({"at", "all"})
    public AtAll parseAtAll() {
        return AtAll.INSTANCE;
    }
    
    @MessageParser({"music", "share", "?", "?", "?", "?", "?", "?", "?"})
    public MusicShare parseMusicShare(@ParserArgument String softwareType,
                                      @ParserArgument String title,
                                      @ParserArgument String description,
                                      @ParserArgument String jumpUrl,
                                      @ParserArgument String coverUrl,
                                      @ParserArgument String musicUrl,
                                      @ParserArgument String summary) {
    
        final MusicSoftwareType musicSoftwareType = MusicSoftwareType.of(softwareType);
        Preconditions.elementNonNull(musicSoftwareType, "no such music software type: " + softwareType);
        
        return new MusicShare(
            musicSoftwareType,
            title,
            description,
            jumpUrl,
            coverUrl,
            musicUrl,
            summary
        );
    }
}
