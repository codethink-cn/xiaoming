package cn.chuanwise.xiaoming.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionFormat {
    String prefix = "", suffix = "", content, splitter = "\n", nullObject = "null";

    public CollectionFormat(String content, String splitter) {
        this.content = content;
        this.splitter = splitter;
    }

    public CollectionFormat(String prefix, String content, String splitter) {
        this.prefix = prefix;
        this.content = content;
        this.splitter = splitter;
    }

    public CollectionFormat(String content) {
        this.content = content;
    }
}