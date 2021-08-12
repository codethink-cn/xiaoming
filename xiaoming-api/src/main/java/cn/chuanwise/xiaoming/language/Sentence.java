package cn.chuanwise.xiaoming.language;

import cn.chuanwise.utility.*;
import lombok.*;

import java.beans.Transient;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@NoArgsConstructor
public class Sentence {
    String defaultValue = "empty";

    transient List<String> defaultValueContextParameters;

    @Transient
    public List<String> getDefaultParameterNames() {
        return defaultValueContextParameters;
    }

    List<String> customValues = new ArrayList<>(0);

    public Sentence(String defaultValue, String... customValues) {
        this.defaultValue = defaultValue;
        this.customValues = CollectionUtility.addTo(Arrays.asList(customValues), new ArrayList<>(customValues.length), value -> value);
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        defaultValueContextParameters = ArgumentUtility.getContextVariableNames(defaultValue);
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

        return ArgumentUtility.render(value, maxIterateTime, variable -> {
            final int index = argumentNames.indexOf(variable);
            if (IndexUtility.isLegal(index, arguments.length)) {
                return arguments[index];
            } else {
                return 0;
            }
        });
    }
}
