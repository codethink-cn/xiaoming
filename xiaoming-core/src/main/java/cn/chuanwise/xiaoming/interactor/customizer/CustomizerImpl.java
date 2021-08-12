package cn.chuanwise.xiaoming.interactor.customizer;

import cn.chuanwise.toolkit.preservable.file.FilePreservableImpl;
import cn.chuanwise.xiaoming.interactor.customizer.Customizer;
import cn.chuanwise.xiaoming.interactor.information.InteractorMethodInformation;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class CustomizerImpl extends FilePreservableImpl implements Customizer {
    Map<String, InteractorMethodInformation> methodInformation = new HashMap<>();

    @Override
    public InteractorMethodInformation forName(String methodName) {
        return methodInformation.get(methodName);
    }

    @Override
    public void addInformation(InteractorMethodInformation information) {
        methodInformation.put(information.getName(), information);
    }

    @Override
    public void removeInformation(String methodName) {
        methodInformation.remove(methodName);
    }
}
