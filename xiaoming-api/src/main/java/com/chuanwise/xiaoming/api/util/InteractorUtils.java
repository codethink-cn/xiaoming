package com.chuanwise.xiaoming.api.util;

import com.chuanwise.xiaoming.api.exception.ReceptCancelledException;
import com.chuanwise.xiaoming.api.user.XiaomingUser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Chuanwise
 */
public class InteractorUtils {
    static final Pattern PAGE = Pattern.compile("(第|p|P)?\\s*?(?<page>\\d+)\\s*?页?");
    static final Pattern INDEX = Pattern.compile("(第|no|No)\\s*?(?<index>\\d+)\\s*?(个?)");

    public static <T> void showList(XiaomingUser user, List<T> list, Function<T, String> consumer, String empty, int pageElemNumber) {
        if (list.isEmpty()) {
            user.sendMessage(empty);
            return;
        }

        List<String> strings = new ArrayList<>(list.size());
        list.forEach(t -> strings.add(consumer.apply(t)));
        if (list.size() < pageElemNumber) {
            int index = 0;
            StringBuilder builder = new StringBuilder((++index) + "、" + strings.get(0));
            for (int i = 1; i < list.size(); i++) {
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
                        user.sendError("页码应该介于 [{}, {}) 哦", 1, totalPageNumber);
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
                final Matcher indexMatcher = INDEX.matcher(nextInput);
                if (nextMatcher.matches()) {
                    final int index = Integer.parseInt(indexMatcher.group("index"));
                    if (index < 1 || index > list.size()) {
                        user.sendError("序号应该介于 [{}, {}) 哦", 1, list.size());
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
                        user.sendMessage("已退出阅览表格");
                        shouldBreak = true;
                        break;
                    default:
                        user.sendMessage("小明不知道你的意思，就先退出啦");
                        shouldBreak = true;
                }
            }
        }
    }

    public static <T> T indexChooser(XiaomingUser user, List<T> elements, Function<T, String> summaryer, String prefix, String spliter, int pageElemNumber) {
        /*
        int frontIndex = 0;
        int elementNumber = elements.size();
        int totalPageNumber = elementNumber / frontIndex + (elementNumber % frontIndex != 0 ? 1 : 0);
        int curPageNumber = 1;

        int index = 0;
        int finalChoose = 0;
        StringBuilder builder = new StringBuilder();
        while (true) {
            builder.setLength(0);
            builder.append("第 ").append(curPageNumber).append(" 页，共 ").append(totalPageNumber).append(" 页").append("\n");

            if (elementNumber - frontIndex <= pageElemNumber) {
                int thisPageElemNumber = Math.min(elementNumber, frontIndex + pageElemNumber) - frontIndex;
                builder.append(prefix);

                builder.append(frontIndex).append("、").append(summaryer.apply(elements.get(frontIndex++)));
                for (int i = frontIndex; i < thisPageElemNumber; i++) {
                    builder.append(i).append("、").append(spliter).append(summaryer.apply(elements.get(i)));
                }
            }

            String choose = user.nextInput();
            while (true) {

            }
            if (Objects.equals(choose, "上一页")) {
                if (curPageNumber == 1) {
                    user.sendError("已经没有上一页啦");
                }
            }
        }

         */
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
                return list.get(sizeBeforeWait);
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
}
