package cn.chuanwise.xiaoming.interactor.filter;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public abstract class StringFilterMatcher extends FilterMatcher {
    String string;

    @Override
    public String toString() {
        return string;
    }
}
