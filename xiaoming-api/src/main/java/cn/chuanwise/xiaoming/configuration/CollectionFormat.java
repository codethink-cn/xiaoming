package cn.chuanwise.xiaoming.configuration;

public interface CollectionFormat {
    default String getPrefix() {
        return "";
    }

    default String getSuffix() {
        return "";
    }

    default String getNull() {
        return "null";
    }

    default String getIndex() {
        return "";
    }

    String getContent();

    String getSplitter();
}
