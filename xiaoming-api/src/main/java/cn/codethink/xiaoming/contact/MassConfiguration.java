package cn.codethink.xiaoming.contact;

/**
 * 集体设置
 *
 * @author Chuanwise
 */
public interface MassConfiguration {
    
    /**
     * 询问集体名
     *
     * @return 群名
     */
    String getName();
    
    /**
     * 设置集体名
     *
     * @param name 群名
     */
    void setName(String name);
}
