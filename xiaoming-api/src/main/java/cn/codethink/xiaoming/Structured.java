package cn.codethink.xiaoming;

/**
 * 带有某种结构的东西
 *
 * @author Chuanwise
 */
public interface Structured<P, N> {
    
    /**
     * 父节点
     *
     * @return 为 null 时表示没有父节点
     */
    P previous();
}
