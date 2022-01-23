package cn.chuanwise.xiaoming.interactor.customizer;

import cn.chuanwise.toolkit.preservable.AbstractPreservable;
import cn.chuanwise.xiaoming.interactor.handler.Interactor;
import lombok.Data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Data
public class FileSavableCustomizer extends AbstractPreservable implements Customizer {
    Map<String, Interactor> interactors = new HashMap<>();

    public Map<String, Interactor> getInteractors() {
        return Collections.unmodifiableMap(interactors);
    }

    @Override
    public Interactor forName(String interactorName) {
        return interactors.get(interactorName);
    }

    public void addInteractorHandler(Interactor handler) {
        interactors.put(handler.getName(), handler);
    }

    public void removeInteractorHandler(String methodName) {
        interactors.remove(methodName);
    }

    public void clearInteractorHandler() {
        interactors.clear();
    }
}
