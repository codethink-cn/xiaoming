package cn.chuanwise.xiaoming.interactor.customizer;

import cn.chuanwise.xiaoming.interactor.information.InteractorMethodInformation;

import java.util.Map;

public interface Customizer {
    InteractorMethodInformation forName(String methodName);

    void addInformation(InteractorMethodInformation information);

    void removeInformation(String methodName);

    Map<String, InteractorMethodInformation> getMethodInformation();

    void setMethodInformation(Map<String, InteractorMethodInformation> methodInformation);
}
