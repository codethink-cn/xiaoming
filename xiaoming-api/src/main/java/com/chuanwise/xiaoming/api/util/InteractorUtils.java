package com.chuanwise.xiaoming.api.util;

import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.exception.InteractorTimeoutException;
import com.chuanwise.xiaoming.api.exception.ReceptCancelledException;
import com.chuanwise.xiaoming.api.user.XiaomingUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Chuanwise
 */
public class InteractorUtils extends StaticUtils {
    static final Pattern PAGE = Pattern.compile("(第|P|p)\\s*(?<page>\\d+)\\s*页");

    public static <T> void showCollection(XiaomingUser user, Collection<T> collection, Function<T, String> summarizer, String empty, int pageElemNumber) {
        if (collection.isEmpty()) {
            user.sendMessage(empty);
            return;
        }

        List<String> strings = new ArrayList<>(collection.size());
        collection.forEach(t -> strings.add(summarizer.apply(t)));
        if (collection.size() < pageElemNumber) {
            int index = 0;
            StringBuilder builder = new StringBuilder((++index) + "、" + strings.get(0));
            for (int i = 1; i < collection.size(); i++) {
                builder.append("\n").append((++index) + "、" + strings.get(0));
            }
            user.sendMessage(builder.toString());
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
                final String nextInput = user.nextInput().serialize();

                // 第 X 页
                final Matcher nextMatcher = PAGE.matcher(nextInput);
                if (nextMatcher.matches()) {
                    final int switchTo = Integer.parseInt(nextMatcher.group("page"));
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

                // 第 X 个
                if (nextInput.matches("\\d+")) {
                    final int index = Integer.parseInt(nextInput);
                    if (index < 1 || index > collection.size()) {
                        user.sendError("序号应该在 {} 到 {} 之间", 1, collection.size());
                        showPageInfo = false;
                    } else {
                        final int switchTo = index / pageElemNumber;
                        if (switchTo + 1 == pageNumber) {
                            user.sendMessage("当前这一页有第 {last} 个项目哦");
                            showPageInfo = false;
                        } else {
                            pageNumber = switchTo - 1;
                        }
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

    public static <T> T indexChooser(XiaomingUser user, Collection<T> collection, Function<T, String> summarizer, int pageElemNumber) {
        return indexChooser(user, collection, summarizer, "（无）", "\n", pageElemNumber);
    }

    public static <T> T indexChooser(XiaomingUser user, Collection<T> collection, Function<T, String> summarizer, String empty, String splitter, int pageElemNumber) {
        if (collection.isEmpty()) {
            user.sendMessage(empty);
            return null;
        }

        List<String> strings = new ArrayList<>(collection.size());
        collection.forEach(t -> strings.add(summarizer.apply(t)));

        if (collection.size() == 1) {
            return collection.iterator().next();
        } else {
            final Object[] objects = collection.toArray(new Object[0]);

            if (collection.size() < pageElemNumber) {
                int index = 0;
                StringBuilder builder = new StringBuilder((++index) + "、" + strings.get(0));
                for (int i = 1; i < collection.size(); i++) {
                    builder.append("\n").append((++index) + "、" + strings.get(i));
                }
                user.sendMessage(builder.toString());

                final String choose = waitNextLegalInput(user, string -> {
                    if (string.matches("\\d+")) {
                        final int i = Integer.parseInt(string);
                        return i >= 1 && i <= collection.size();
                    } else {
                        return Objects.equals(string, "退出");
                    }
                }, "应该告诉我你要选择的序号（在 1 到 " + collection.size() + " 之间），或「退出」哦").serialize();
                if (Objects.equals(choose, "退出")) {
                    return null;
                } else {
                    return (T) objects[Integer.parseInt(choose) - 1];
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
                    final String nextInput = user.nextInput().serialize();

                    // 第 X 页
                    final Matcher nextMatcher = PAGE.matcher(nextInput);
                    if (nextMatcher.matches()) {
                        final int switchTo = Integer.parseInt(nextMatcher.group("page"));
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

                    // 第 X 个
                    if (nextInput.matches("\\d+")) {
                        final int index = Integer.parseInt(nextInput);
                        if (index < 1 || index > collection.size()) {
                            user.sendError("序号应该在 {} 到 {} 之间哦", 1, collection.size());
                            showPageInfo = false;
                        } else {
                            return (T) objects[index - 1];
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

    public static <T> T waitLastElement(List<T> list, long timeout, Runnable onTimeout) {
        final long latestTime = System.currentTimeMillis() + timeout;
        final int sizeBeforeWait = list.size();
        try {
            synchronized (list) {
                list.wait(timeout);
            }
        } catch (InterruptedException exception) {
            throw new ReceptCancelledException();
        }
        if (System.currentTimeMillis() < latestTime) {
            if (sizeBeforeWait + 1 == list.size()) {
                final T result = list.get(sizeBeforeWait);
                return result;
            } else {
                throw new ReceptCancelledException();
            }
        } else {
            onTimeout.run();
            return null;
        }
    }

    public static <T> T waitLastElement(List<T> list, long timeout) {
        return waitLastElement(list, timeout, () -> {});
    }

    public static <T, C extends Collection<T>> C fillCollection(XiaomingUser user,
                                                                String beforeInput,
                                                                C collection, Predicate<String> isLegal, Function<String, T> translator, String illegalNotice,
                                                                String endWord,
                                                                boolean emptyable, String emptyNotice) {
        user.sendMessage(beforeInput + "，使用「" + endWord + "」结束");
        Message message = user.nextInput();
        while (true) {
            final String serializedMessage = message.serialize();
            if (Objects.equals(serializedMessage, endWord)) {
                if (!emptyable && collection.isEmpty()) {
                    user.sendMessage(emptyNotice);
                } else {
                    return collection;
                }
            } else {
                if (isLegal.test(serializedMessage)) {
                    collection.add(translator.apply(serializedMessage));
                } else {
                    user.reply(message, illegalNotice);
                }
            }
            message = user.nextInput();
        }
    }

    public static <T, C extends Collection<T>> C fillCollection(XiaomingUser user,
                                                                String questionWithoutAsk,
                                                                String what,
                                                                C collection, Predicate<String> isLegal, Function<String, T> translator,
                                                                boolean emptyable) {
        user.setProperty("question", questionWithoutAsk);
        user.setProperty("what", what);

        final String endWord = "结束";
        user.setProperty("endWord", endWord);

        return fillCollection(user,
                user.replaceLanguage("inputItOneByOne"),
                collection, isLegal, translator,
                user.replaceLanguage("illegalInput"), endWord,
                emptyable, user.replaceLanguage("unemptyableNotice"));
    }

    public static <C extends Collection<String>> C fillStringCollection(XiaomingUser user,
                                                                        String questionWithoutAsk,
                                                                        String what,
                                                                        C collection,
                                                                        boolean emptyable) {
        return fillCollection(user, questionWithoutAsk, what, collection, string -> true, String::toString, emptyable);
    }

    public static Message waitNextLegalInput(XiaomingUser user, Predicate<String> judger, String illegalInput) {
        while (true) {
            final Message message = user.nextInput();
            final String serializedMessage = message.serialize();

            if (judger.test(serializedMessage)) {
                return message;
            } else {
                user.sendError(illegalInput);
            }
        }
    }

    public static Message waitNextInputIn(XiaomingUser user, Collection<String> definations, String illegalInput) {
        return waitNextLegalInput(user, definations::contains, illegalInput);
    }
}