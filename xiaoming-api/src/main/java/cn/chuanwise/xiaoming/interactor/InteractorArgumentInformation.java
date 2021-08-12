package cn.chuanwise.xiaoming.interactor;

import cn.chuanwise.xiaoming.interactor.information.InteractorMethodInformation;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class InteractorArgumentInformation<T> {
    Class<T> clazz;
    String parameterName;
    String currentValue;
    String defaultValue;
    InteractorMethodInformation methodInformation;
}
