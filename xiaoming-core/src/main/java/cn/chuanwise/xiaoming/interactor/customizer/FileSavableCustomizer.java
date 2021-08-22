package cn.chuanwise.xiaoming.interactor.customizer;

import cn.chuanwise.toolkit.preservable.file.FilePreservableImpl;
import cn.chuanwise.xiaoming.interactor.handler.InteractorHandler;
import lombok.Data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Data
public class FileSavableCustomizer extends FilePreservableImpl implements Customizer {
    Map<String, InteractorHandler> interactorHandlers = new HashMap<>();

    public Map<String, InteractorHandler> getInteractorHandlers() {
        return Collections.unmodifiableMap(interactorHandlers);
    }

    @Override
    public InteractorHandler forName(String interactorName) {
        return interactorHandlers.get(interactorName);
    }

    public void addInteractorHandler(InteractorHandler handler) {
        interactorHandlers.put(handler.getName(), handler);
    }

    public void removeInteractorHandler(String methodName) {
        interactorHandlers.remove(methodName);
    }

    public void clearInteractorHandler() {
        interactorHandlers.clear();
    }
}
