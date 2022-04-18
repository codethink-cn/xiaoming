package cn.codethink.xiaoming.contact;

import cn.codethink.common.util.Preconditions;
import cn.codethink.common.util.StaticUtilities;
import net.mamoe.mirai.contact.MemberPermission;

import java.util.NoSuchElementException;

/**
 * 和 Mirai 的权限互转工具
 *
 * @author Chuanwise
 */
public class MiraiRole
    extends StaticUtilities {
    
    /**
     * 将 Mirai 群员权限转化为小明角色
     *
     * @param permission Mirai 群员权限
     * @return 小明角色
     */
    public static Role toXiaoMing(MemberPermission permission) {
        Preconditions.nonNull(permission, "permission");

        switch (permission) {
            case OWNER:
                return Role.OWNER;
            case MEMBER:
                return Role.MEMBER;
            case ADMINISTRATOR:
                return Role.ADMIN;
            default:
                throw new NoSuchElementException();
        }
    }
    
    /**
     * 将小明角色转化为 Mirai 群员权限
     *
     * @param role 小明角色
     * @return Mirai 群员权限
     */
    public static MemberPermission toMirai(Role role) {
        Preconditions.nonNull(role, "role");
    
        switch (role) {
            case ADMIN:
                return MemberPermission.ADMINISTRATOR;
            case OWNER:
                return MemberPermission.OWNER;
            case MEMBER:
                return MemberPermission.MEMBER;
            default:
                throw new NoSuchElementException();
        }
    }
}
