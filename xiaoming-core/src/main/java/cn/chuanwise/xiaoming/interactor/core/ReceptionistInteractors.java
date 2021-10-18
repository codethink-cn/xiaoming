package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Permission;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.recept.Receptionist;
import cn.chuanwise.xiaoming.recept.ReceptionistManager;
import cn.chuanwise.xiaoming.user.GroupXiaomingUser;
import cn.chuanwise.xiaoming.user.MemberXiaomingUser;
import cn.chuanwise.xiaoming.user.PrivateXiaomingUser;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.util.CommandWords;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class ReceptionistInteractors
        extends SimpleInteractors {
    ReceptionistManager receptionistManager;

    @Override
    public void onRegister() {
        receptionistManager = xiaomingBot.getReceptionistManager();
    }

    @Filter(CommandWords.RECEPTIONIST)
    @Permission("core.receptionist.list")
    public void listReceptionists(XiaomingUser user) {
        final Map<Long, Receptionist> receptionists = receptionistManager.getReceptionists();
        if (receptionists.isEmpty()) {
            user.sendWarning("目前没有任何接待员");
        } else {
            user.sendMessage("目前的接待员有：\n" +
                    CollectionUtil.toIndexString(receptionists.keySet(), xiaomingBot.getAccountManager()::getAliasAndCode));
        }
    }

    @Filter(CommandWords.RECEPTIONIST + " {qq}")
    @Permission("core.receptionist.look")
    public void lookReceptionist(XiaomingUser user, @FilterParameter("qq") long qq) {
        final Receptionist receptionist = receptionistManager.getReceptionist(qq);
        final Map<Long, GroupXiaomingUser> groupXiaomingUsers = receptionist.getGroupXiaomingUsers();
        final PrivateXiaomingUser privateXiaomingUser = receptionist.getPrivateXiaomingUser();
        final Map<Long, MemberXiaomingUser> memberXiaomingUsers = receptionist.getMemberXiaomingUsers();

        final Function<XiaomingUser, String> statusFunction = xiaomingUser -> (Objects.isNull(xiaomingUser.getInteractorContext()) ? "空闲" : "阻塞");

        user.sendMessage("「接待员详情」\n" +
                "目标：" + xiaomingBot.getAccountManager().getAliasAndCode(qq) + "\n" +
                "私聊：" + statusFunction.apply(privateXiaomingUser) + "\n" +
                "群聊：" + Optional.ofNullable(CollectionUtil.toIndexString(groupXiaomingUsers.values(), statusFunction::apply))
                                .map(x -> "共 " + groupXiaomingUsers.size() + " 个实体：\n" + x)
                                .orElse("（无）") + "\n" +
                "临时：" + Optional.ofNullable(CollectionUtil.toIndexString(memberXiaomingUsers.values(), statusFunction::apply))
                                .map(x -> "共 " + groupXiaomingUsers.size() + " 个实体：\n" + x)
                                .orElse("（无）"));
    }

    @Filter(CommandWords.DISABLE + CommandWords.RECEPTIONIST + " {qq}")
    @Filter(CommandWords.FORCE + CommandWords.DISABLE + CommandWords.RECEPTIONIST + " {qq}")
    @Permission("core.receptionist.disable")
    public void disableReceptionist(XiaomingUser user, @FilterParameter("qq") long qq) {
        receptionistManager.removeReceptionist(qq)
                .ifPresentOrElse(receptionist -> {
                    user.sendMessage("成功强制关闭" + xiaomingBot.getAccountManager().getAliasAndCode(qq) + "的接待员，" +
                            "这可能导致出现多重接待任务问题，但不会影响小明的正常运行");
                }, () -> {
                    user.sendError("当前并没有" + xiaomingBot.getAccountManager().getAliasAndCode(qq) + "的接待员");
                });
    }
}