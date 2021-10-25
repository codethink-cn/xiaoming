package cn.chuanwise.xiaoming.permission;

import cn.chuanwise.toolkit.box.Box;
import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.util.ConditionUtil;
import cn.chuanwise.util.StringUtil;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Data
@JsonSerialize(using = PermissionSerializer.class)
@JsonDeserialize(using = PermissionDeserializer.class)
public final class Permission
    implements Comparable<Permission> {
    protected final List<Element> elements;
    protected final boolean granted;

    @Override
    public int compareTo(@NotNull Permission o) {
        return toString().compareTo(o.toString());
    }

    protected interface Element {
        @Override
        String toString();
    }

    @Getter
    protected static class GenericElement implements Element {
        public static final String STRING = "*";

        public static final GenericElement INSTANCE = new GenericElement();

        private GenericElement() {}

        @Override
        public String toString() {
            return STRING;
        }
    }

    @Getter
    protected static class PlainTextElement implements Element {
        protected final String text;
        protected final boolean brace;

        public PlainTextElement(String text, boolean brace) {
            this.text = text;
            this.brace = brace;
        }

        @Override
        public String toString() {
            return brace ? ('{' + text + '}') : text;
        }
    }

    @Getter
    protected static class PatternElement implements Element {
        protected final Pattern pattern;

        public PatternElement(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public String toString() {
            return '[' + pattern.pattern() + ']';
        }
    }

    private Permission(boolean granted, List<Element> elements) {
        this.granted = granted;
        this.elements = elements;
    }

    /**
     * compile a string to permission node
     * @param string permission string
     * @return permission
     */
    public static Permission compile(String string) {
        ConditionUtil.checkArgument(StringUtil.notEmpty(string), "permission node string is empty!");

        // 检查是否是否定节点
        final boolean granted;
        if (string.charAt(0) == '-') {
            granted = false;
            string = string.substring(1);
            ConditionUtil.checkArgument(StringUtil.notEmpty(string), "permission node string is empty!");
        } else {
            granted = true;
        }

        final List<Element> elements = new ArrayList<>();

        int state = 0;

        final StringBuffer buffer = new StringBuffer();
        final int length = string.length();

        boolean translated = false;
        boolean translatedUsed = false;
        boolean regexTranslated = false;
        int leftBracketDepth = 0;
        boolean assertDot = false;
        for (int i = 0; i < length; i++) {
            final char ch = string.charAt(i);

            // assert
            if (assertDot) {
                report(ch == '.', string, i, "assert failed", "character must be '.'!");
                assertDot = false;
                continue;
            }

            switch (state) {
                case 0:
                    switch (ch) {
                        case '[':
                            // 正则表达式开头
                            state = 2;
                            leftBracketDepth = 1;
                            break;
                        case ']':
                            report(string, i, "语法错误", "] 是用在正则表达式结尾的，但却错误地出现在了权限节点中间。如须作为普通节点开头，请转义该符号");
                            break;
                        case '.':
                            report(string, i, "语法错误", "权限节点不能有空的子节点！");
                            break;
                        default:
                            // 普通文字模式
                            i--;
                            state = 1;
                            break;
                    }
                    break;
                case 1:
                    if (translated) {
                        buffer.append(ch);
                        break;
                    }
                    switch (ch) {
                        case '/':
                        case '\\':
                            translated = true;
                            translatedUsed = true;
                            break;
                        case '.':
                            // 普通字符串结尾
                            // 检查是否是特殊字符串例如 * 和 ?
                            final String text = buffer.toString();
                            report(StringUtil.notEmpty(text), string, i, "语法错误", "节点内容为空！");
                            buffer.setLength(0);
                            state = 0;

                            if (Objects.equals(GenericElement.STRING, text)) {
                                report(string, i, "语法错误", "贪婪通配符只能在权限节点结尾处使用！");
                                throw new IllegalStateException();
                            }
                            translatedUsed = false;
                            elements.add(new PlainTextElement(text, translatedUsed));
                            break;
                        default:
                            buffer.append(ch);
                            break;
                    }
                    break;
                case 2:
                    switch (ch) {
                        case '[':
                            if (!regexTranslated) {
                                leftBracketDepth++;
                            } else {
                                regexTranslated = false;
                            }
                            buffer.append(ch);
                            break;
                        case ']':
                            if (regexTranslated) {
                                buffer.append(ch);
                                regexTranslated = false;
                                break;
                            } else {
                                leftBracketDepth--;
                            }

                            if (leftBracketDepth == 0) {
                                // 到达正则表达式结尾
                                // 获取字符串、判空后清理缓存
                                final String regex = buffer.toString();
                                report(StringUtil.notEmpty(regex), string, i, "语法错误", "正则表达式内容为空！");
                                buffer.setLength(0);
                                state = 0;

                                final Pattern pattern;
                                try {
                                    pattern = Pattern.compile(regex);
                                } catch (PatternSyntaxException exception) {
                                    report(string, i, "语法错误", "正则表达式语法错误：" + exception.getMessage());
                                    break;
                                }
                                elements.add(new PatternElement(pattern));
                                assertDot = true;
                            } else {
                                // 括号是正则表达式的一部分，记录入缓存
                                buffer.append(ch);
                            }
                            break;
                        default:
                            if (!regexTranslated && (ch == '\\' || ch == '/')) {
                                regexTranslated = true;
                            } else {
                                regexTranslated = false;
                            }
                            buffer.append(ch);
                            break;
                    }
                    break;
                default:
                    throw new IllegalStateException();
            }
        }

        switch (state) {
            case 0:
                break;
            case 1:
                // 普通字符串结尾
                // 检查是否退出了转义模式
                report(!translated, string, length,"语法错误", "结尾处的转义字符不完整");

                // 检查是否是特殊字符串例如 * 和 ?
                final String text = buffer.toString();
                report(StringUtil.notEmpty(text), string, length, "语法错误", "节点内容为空！如需使用通配节点，请使用 * 或 **！");

                final Element element;
                switch (text) {
                    case GenericElement.STRING:
                        element = GenericElement.INSTANCE;
                        break;
                    default:
                        element = new PlainTextElement(text, translatedUsed);
                        break;
                }
                elements.add(element);
                break;
            case 2:
                report(string, length, "语法错误", "正则表达式括号不匹配！");
                break;
            default:
                throw new IllegalStateException();
        }

        return new Permission(granted, Collections.unmodifiableList(elements));
    }

    /**
     * calculate if this permission can match given permission
     * @param permission required permission
     * @return permission accessible
     */
    public Accessible acceptable(Permission permission) {
        ConditionUtil.notNull(permission, "required permission");

        final List<Element> thatElements = permission.elements;
        final List<Element> thisElements = elements;

        final int thatElementSize = thatElements.size();
        final int thisElementSize = thisElements.size();

        int thatElementIndex = 0;
        int thisElementIndex = 0;

        final Box<Boolean> result = Box.empty();
        while (thatElementIndex < thatElementSize && thisElementIndex < thisElementSize) {
            final Element thatElement = thatElements.get(thatElementIndex);
            final Element thisElement = thisElements.get(thisElementIndex);

            if (thatElement instanceof PlainTextElement) {
                final PlainTextElement thatPlainTextElement = (PlainTextElement) thatElement;

                // 如果双方都是字符串，比对字符串内容即可
                if (thisElement instanceof PlainTextElement) {
                    final PlainTextElement thisPlainTextElement = (PlainTextElement) thisElement;

                    final boolean textEquals = Objects.equals(thatPlainTextElement.getText(), thisPlainTextElement.getText());
                    if (!textEquals) {
                        result.set(false);
                        break;
                    }
                    thisElementIndex++;
                    thatElementIndex++;
                    continue;
                }

                // 如果自己是通配符，则单个通配时匹配成功
                // 如果是多个匹配，则检查对方是否有后
                if (thisElement instanceof GenericElement) {
                    result.set(true);
                    break;
                }

                if (thisElement instanceof PatternElement) {
                    final PatternElement thisPatternElement = (PatternElement) thisElement;

                    final boolean testMatches = thisPatternElement.pattern.matcher(thatPlainTextElement.getText()).matches();
                    if (!testMatches) {
                        result.set(false);
                        break;
                    }
                    thisElementIndex++;
                    thatElementIndex++;
                    continue;
                }

                throw new IllegalStateException();
            }
            if (thatElement instanceof GenericElement) {
                result.set(true);
                break;
            }
            if (thatElement instanceof PatternElement) {
                final PatternElement thatPatternElement = (PatternElement) thatElement;

                // 如果是字符串，测试匹配即可
                if (thisElement instanceof PlainTextElement) {
                    final PlainTextElement thisPlainTextElement = (PlainTextElement) thisElement;

                    final boolean testMatches = thatPatternElement.pattern.matcher(thisPlainTextElement.getText()).matches();
                    if (!testMatches) {
                        result.set(false);
                        break;
                    }
                    thisElementIndex++;
                    thatElementIndex++;
                    continue;
                }

                // 如果自己是通配符，则单个通配时匹配成功
                // 如果是多个匹配，则检查对方是否有后
                if (thisElement instanceof GenericElement) {
                    result.set(true);
                    break;
                }

                if (thisElement instanceof PatternElement) {
                    result.set(false);
                    break;
                }

                throw new IllegalStateException();
            }

            throw new IllegalStateException();
        }

        if (result.isPresent()) {
            if (result.get()) {
                if (granted) {
                    return Accessible.ACCESSIBLE;
                } else {
                    return Accessible.UNACCESSIBLE;
                }
            } else {
                return Accessible.UNKNOWN;
            }
        } else {
            if (granted) {
                return Accessible.ACCESSIBLE;
            } else {
                return Accessible.UNACCESSIBLE;
            }
        }
    }

    public Accessible acceptable(String permision) {
        ConditionUtil.checkArgument(StringUtil.notEmpty(permision), "permission");
        return acceptable(compile(permision));
    }

    @Override
    public String toString() {
        return (granted ? "" : "-") + CollectionUtil.toString(elements, ".");
    }

    private static void report(String string, int index, String errorType, String message) {
        throw new IllegalArgumentException(errorType + "\n" + message + "\n" +
                string + " （位于第 " + index + " 个字符左右）\n" +
                StringUtil.repeat(" ", Math.max(index - 2, 0)) + "---");
    }

    private static void report(boolean legal, String string, int index, String errorType, String message) {
        if (!legal) {
            report(string, index, errorType, message);
        }
    }
}