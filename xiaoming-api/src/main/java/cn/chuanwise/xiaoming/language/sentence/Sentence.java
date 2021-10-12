package cn.chuanwise.xiaoming.language.sentence;

import cn.chuanwise.util.*;
import lombok.*;

import java.util.*;

@Data
@NoArgsConstructor
public class Sentence {
    String defaultValue = "empty";

    transient List<String> defaultValueContextParameters;

    List<String> customValues = new ArrayList<>(0);

    public Sentence(String defaultValue, String... customValues) {
        this.defaultValue = defaultValue;
        this.customValues = CollectionUtil.addTo(Arrays.asList(customValues), new ArrayList<>(customValues.length), value -> value);
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        this.defaultValueContextParameters = ArgumentUtil.getContextVariableNames(defaultValue);
    }

    public String getValue() {
        if (customValues.size() == 0) {
            return defaultValue;
        } else {
            return customValues.get(RandomUtil.nextInt(customValues.size()));
        }
    }

    public String render(int maxIterateTime, Object... arguments) {
        final String value = getValue();
        final List<String> argumentNames = ArgumentUtil.getContextVariableNames(value);

        ConditionUtil.checkArgument(argumentNames.size() == arguments.length, "parameter number is not equals to argument number!" +
                "parameter: " + argumentNames + " (size: " + argumentNames.size() + "), " +
                "but argument: " + Arrays.toString(arguments) + " (size: " + arguments.length + ")");

        return ArgumentUtil.format(value, maxIterateTime, variable -> {
            final int index = argumentNames.indexOf(variable);
            if (IndexUtil.isLegal(index, arguments.length)) {
                return arguments[index];
            } else {
                return 0;
            }
        });
    }
}
