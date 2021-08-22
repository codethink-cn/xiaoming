package cn.chuanwise.xiaoming.language.sentence;

import cn.chuanwise.utility.*;
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
        this.customValues = CollectionUtility.addTo(Arrays.asList(customValues), new ArrayList<>(customValues.length), value -> value);
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        this.defaultValueContextParameters = ArgumentUtility.getContextVariableNames(defaultValue);
    }

    public String getValue() {
        if (customValues.size() == 0) {
            return defaultValue;
        } else {
            return customValues.get(RandomUtility.nextInt(customValues.size()));
        }
    }

    public String render(int maxIterateTime, Object... arguments) {
        final String value = getValue();
        final List<String> argumentNames = ArgumentUtility.getContextVariableNames(value);

        CheckUtility.checkArgument(argumentNames.size() == arguments.length, "parameter number is not equals to argument number!" +
                "parameter: " + argumentNames + " (size: " + argumentNames.size() + "), " +
                "but argument: " + Arrays.toString(arguments) + " (size: " + arguments.length + ")");

        return ArgumentUtility.format(value, maxIterateTime, variable -> {
            final int index = argumentNames.indexOf(variable);
            if (IndexUtility.isLegal(index, arguments.length)) {
                return arguments[index];
            } else {
                return 0;
            }
        });
    }
}
