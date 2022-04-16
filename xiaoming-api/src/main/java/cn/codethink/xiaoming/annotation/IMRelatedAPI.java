package cn.codethink.xiaoming.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 和指定的通讯软件相关的 API。
 *
 * 例如商城表情 {@link cn.codethink.xiaoming.message.element.MarketFace}，QQ 和微信都具备，但却无法通用。
 *
 * @author Chuanwise
 */
@Documented
@Retention(RetentionPolicy.CLASS)
public @interface IMRelatedAPI {
}
