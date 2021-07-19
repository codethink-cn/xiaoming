package cn.chuanwise.xiaoming.core.schedule.task;

public class OptimizeTask extends ScheduableTaskImpl<Void> {
    @Override
    public Void execute() {
        getXiaomingBot().optimize();
        return null;
    }
}
