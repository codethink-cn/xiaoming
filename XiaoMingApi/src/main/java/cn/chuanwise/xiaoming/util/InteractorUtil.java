package cn.chuanwise.xiaoming.util;

import cn.chuanwise.util.StaticUtil;
import cn.chuanwise.util.StringUtil;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.XiaomingUser;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Chuanwise
 */
public class InteractorUtil extends StaticUtil {
    static final Pattern PAGE = Pattern.compile("\\d+");

    public static <T> void showCollection(XiaomingUser user, Collection<T> collection, Function<T, String> summarizer, String empty, int elementNumberPerPage) {
        if (collection.isEmpty()) {
            user.sendMessage(empty);
            return;
        }

        List<String> strings = new ArrayList<>(collection.size());
        collection.forEach(t -> strings.add(summarizer.apply(t)));
        if (collection.size() < elementNumberPerPage) {
            StringBuilder builder = new StringBuilder(1 + "、" + strings.get(0));
            for (int i = 1; i < collection.size(); i++) {
                builder.append("\n").append((i + 1) + "、" + strings.get(i));
            }
            user.sendMessage(builder.toString());
        } else {
            int pageNumber = 0;
            final int totalPageNumber = strings.size() / elementNumberPerPage + (strings.size() % elementNumberPerPage == 0 ? 0 : 1);

            // 生成每一页的信息
            List<String> pageInfo = new ArrayList<>(totalPageNumber);
            for (int i = 0; i < totalPageNumber; i++) {
                final StringBuilder builder = new StringBuilder();

                builder.append("第 ").append(i + 1).append(" 页，共 ").append(totalPageNumber).append(" 页");
                int pageFrontIndex = elementNumberPerPage * i;
                int pageEndIndex = Math.min(pageFrontIndex + elementNumberPerPage, strings.size());
                for (int j = pageFrontIndex; j < pageEndIndex; j++) {
                    builder.append("\n").append(j + 1).append("、").append(strings.get(j));
                }

                pageInfo.add(builder.toString());
            }

            boolean showPageInfo = true;
            boolean shouldBreak = false;
            while (!shouldBreak) {
                if (showPageInfo) {
                    user.sendMessage(pageInfo.get(pageNumber));
                }
                showPageInfo = true;

                // 选择翻页等操作
                final String nextInput = user.nextMessageOrExit().serialize();

                // 第 X 页
                final Matcher nextMatcher = PAGE.matcher(nextInput);
                if (nextMatcher.matches()) {
                    final int switchTo = Integer.parseInt(nextInput);
                    if (switchTo < 1 || switchTo > totalPageNumber) {
                        user.sendError("页码应该在 {} 到 {} 之间", 1, totalPageNumber);
                        showPageInfo = false;
                    } else if (switchTo + 1 == pageNumber) {
                        user.sendMessage("当前就正在这一页上哦");
                        showPageInfo = false;
                    } else {
                        pageNumber = switchTo - 1;
                    }
                    continue;
                }

                switch (nextInput) {
                    case "下一页":
                    case "下页":
                    case "next":
                        if (pageNumber + 1 == totalPageNumber) {
                            user.sendError("已经没有下一页了，重新选择一下吧");
                            showPageInfo = false;
                        } else {
                            pageNumber++;
                        }
                        break;
                    case "上一页":
                    case "上页":
                    case "front":
                    case "prev":
                        if (pageNumber == 0) {
                            user.sendError("已经没有上一页了，重新选择一下吧");
                            showPageInfo = false;
                        } else {
                            pageNumber--;
                        }
                        break;
                    case "退出":
                    case "exit":
                        shouldBreak = true;
                        break;
                    default:
                        user.sendMessage("应该告诉选择的序号、「第x页」「上一页」「下一页」或「退出」哦");
                        showPageInfo = false;
                }
            }
        }
    }

    public static <T> void showCollection(XiaomingUser user, Collection<T> collection, Function<T, String> summarizer, int elementNumberPerPage) {
        showCollection(user, collection, summarizer, "（空）", elementNumberPerPage);
    }

    public static <T> T indexChooser(XiaomingUser user, List<T> collection, int pageElemNumber) {
        return indexChooser(user, collection, Objects::toString, "\n", pageElemNumber);
    }

    public static <T> T indexChooser(XiaomingUser user, List<T> collection, Function<T, String> summarizer, int pageElemNumber) {
        return indexChooser(user, collection, summarizer, "\n", pageElemNumber);
    }

    public static <T> T indexChooser(XiaomingUser user, List<T> list, Function<T, String> summarizer, String splitter, int pageElemNumber) {
        if (list.isEmpty()) {
            return null;
        }

        List<String> strings = new ArrayList<>(list.size());
        list.forEach(t -> strings.add(summarizer.apply(t)));

        if (list.size() == 1) {
            return list.iterator().next();
        } else {
            if (list.size() < pageElemNumber) {
                StringBuilder builder = new StringBuilder(1 + "、" + strings.get(0));
                for (int i = 1; i < list.size(); i++) {
                    builder.append("\n").append((i + 1) + "、" + strings.get(i));
                }
                user.sendMessage(builder.toString());

                final String choose = waitNextLegalInput(user, string -> {
                    if (string.matches("\\d+")) {
                        final int i = Integer.parseInt(string);
                        return i >= 1 && i <= list.size();
                    } else {
                        return Objects.equals(string, "退出");
                    }
                }, "应该告诉我你要选择的序号（在 1 到 " + list.size() + " 之间），或「退出」哦").serialize();
                if (Objects.equals(choose, "退出")) {
                    return null;
                } else {
                    return list.get(Integer.parseInt(choose) - 1);
                }
            } else {
                int pageNumber = 0;
                final int totalPageNumber = strings.size() / pageElemNumber + (strings.size() % pageElemNumber == 0 ? 0 : 1);

                // 生成每一页的信息
                List<String> pageInfo = new ArrayList<>(totalPageNumber);
                for (int i = 0; i < totalPageNumber; i++) {
                    final StringBuilder builder = new StringBuilder();

                    builder.append("第 ").append(i + 1).append(" 页，共 ").append(totalPageNumber).append(" 页");
                    int pageFrontIndex = pageElemNumber * i;
                    int pageEndIndex = Math.min(pageFrontIndex + pageElemNumber, strings.size());
                    for (int j = pageFrontIndex; j < pageEndIndex; j++) {
                        builder.append(splitter).append(j + 1).append("、").append(strings.get(j));
                    }

                    pageInfo.add(builder.toString());
                }

                boolean showPageInfo = true;
                boolean shouldBreak = false;
                while (!shouldBreak) {
                    if (showPageInfo) {
                        user.sendMessage(pageInfo.get(pageNumber));
                    }
                    showPageInfo = true;

                    // 选择翻页等操作
                    final String nextInput = user.nextMessageOrExit().serialize();

                    // 第 X 页
                    final Matcher nextMatcher = PAGE.matcher(nextInput);
                    if (nextMatcher.matches()) {
                        final int switchTo = Integer.parseInt(nextInput);
                        if (switchTo < 1 || switchTo > totalPageNumber) {
                            user.sendError("页码应该在 {context.min} 到 {context.max} 之间", 1, totalPageNumber);
                            showPageInfo = false;
                        } else if (switchTo + 1 == pageNumber) {
                            user.sendMessage("当前就正在这一页上哦");
                            showPageInfo = false;
                        } else {
                            pageNumber = switchTo - 1;
                        }
                        continue;
                    }

                    switch (nextInput) {
                        case "下一页":
                        case "下页":
                        case "next":
                            if (pageNumber + 1 == totalPageNumber) {
                                user.sendError("已经没有下一页了，重新选择一下吧");
                                showPageInfo = false;
                            } else {
                                pageNumber++;
                            }
                            break;
                        case "上一页":
                        case "上页":
                        case "front":
                        case "prev":
                            if (pageNumber == 0) {
                                user.sendError("已经没有上一页了，重新选择一下吧");
                                showPageInfo = false;
                            } else {
                                pageNumber--;
                            }
                            break;
                        case "退出":
                        case "exit":
                            shouldBreak = true;
                            break;
                        default:
                            user.sendMessage("应该告诉选择的序号、「第x页」「上一页」「下一页」或「退出」哦");
                            showPageInfo = false;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 将用户的输入填充到集合中
     * @param user 输入值
     * @param collection 集合
     * @param translator 翻译器，将消息翻译为集合中的元素。如果返回 null，则该元素不会被加入集合中。
     *                   本方法没有非法输入警告功能，这个功能需要自己在翻译器里实现
     * @param stopPredicate 判断当前输入是否是结尾，如果返回 true 则会停止收集
     * @param onEmptyStop 如果非空，则在集合为空前执行并且不停止收集。否则直接返回
     * @param <T> 集合中的元素类型
     * @param <C> 集合类型
     * @return 收集后的集合
     */
    public static <T, C extends Collection<T>> C fillCollection(XiaomingUser user,
                                                                C collection,
                                                                Function<Message, T> translator,
                                                                Predicate<Message> stopPredicate,
                                                                BiConsumer<XiaomingUser, Message> onEmptyStop) {
        Message message = null;
        while (true) {
            message = user.nextMessageOrExit();

            final boolean shouldStop = stopPredicate.test(message);
            if (shouldStop) {
                if (Objects.isNull(onEmptyStop) || !collection.isEmpty()) {
                    return collection;
                } else {
                    onEmptyStop.accept(user, message);
                }
            } else {
                // 用翻译器获得集合元素，随后判空。如果是空则不加入集合，否则继续加入
                final T element = translator.apply(message);
                if (Objects.nonNull(element)) {
                    collection.add(element);
                }
            }
        }
    }

    /**
     * 填充简单的字符串集合
     * 如果 onEmptyStop 为 null，表示集合可空，否则会执行该方法
     */
    public static <T, C extends Collection<String>> C fillStringCollection(XiaomingUser user,
                                                                           C collection,
                                                                           String stopSign,
                                                                           String emptyNotice) {
        final BiConsumer<XiaomingUser, Message> onEmptyStop;
        if (StringUtil.notEmpty(emptyNotice)) {
            onEmptyStop = (u, m) -> u.replyError(m, emptyNotice);
        } else {
            onEmptyStop = null;
        }
        return fillCollection(user, collection, Message::serialize, message -> Objects.equals(message.serialize(), stopSign), onEmptyStop);
    }

    public static <T, C extends Collection<String>> C fillStringCollection(XiaomingUser user,
                                                                           C collection,
                                                                           String what) {
        final String emptyNotice;
        if (Objects.isNull(what)) {
            emptyNotice = null;
        } else {
            emptyNotice = user.format("「" + what + "」不能为空，继续输入吧");
        }
        return fillStringCollection(user, collection, "结束", emptyNotice);
    }

    public static Message waitNextLegalInput(XiaomingUser user, Predicate<Message> judger, Consumer<Message> onIllegalInput) {
        while (true) {
            final Message message = user.nextMessageOrExit();

            if (judger.test(message)) {
                return message;
            } else {
                onIllegalInput.accept(message);
            }
        }
    }

    public static Message waitNextLegalInput(XiaomingUser user, Predicate<String> judger, String illegalInput) {
        return waitNextLegalInput(user, message -> judger.test(message.serialize()), message -> user.replyError(message, illegalInput));
    }

    public static Message waitNextInputIn(XiaomingUser user, Collection<String> defintions, String illegalInput) {
        return waitNextLegalInput(user, defintions::contains, illegalInput);
    }
}