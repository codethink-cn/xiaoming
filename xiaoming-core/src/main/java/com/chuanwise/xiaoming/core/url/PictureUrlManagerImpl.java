package com.chuanwise.xiaoming.core.url;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.url.PictureUrlManager;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;

@Data
public class PictureUrlManagerImpl extends UrlManagerImpl implements PictureUrlManager {
    transient XiaomingBot xiaomingBot;
    transient Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public String requireRecordedCatCode(String miraiCode) {
        requireRecordedUrl(requireUrl(miraiCode));
        return miraiCode;
    }

    @Override
    public String requireUrl(String miraiCode) {
        Matcher matcher = PICTURE_URL_PATTERN.matcher(miraiCode);
        if (matcher.matches()) {
            return matcher.group("url");
        } else {
            throw new IllegalArgumentException("syntax error: cat code string: " + miraiCode);
        }
    }

    /**
     * 保存网络图片到本地并替换其值
     * @param message
     * @return 如果保存图片出现问题为 null，否则为替换 url 后的图片
     */
    @Override
    public String requireRecordedMessage(String message) {
        for (String pictureCatCode : listCatCodes(message)) {
            requireRecordedCatCode(pictureCatCode);
        }
        return message;
    }

    /**
     * 获取一段文字中的所有图片
     * @param string
     * @return
     */
    @Override
    public List<String> listCatCodes(String string) {
        List<String> result = new ArrayList<>();
        int left = string.indexOf("[CAT:image");
        int right;

        while (left != -1) {
            right = string.indexOf("]", left);
            if (left < right) {
                String miraiCodeString = string.substring(left, right + 1);
                result.add(miraiCodeString);
            }
            left = string.indexOf("[CAT:image", right);
        }
        return result;
    }
}