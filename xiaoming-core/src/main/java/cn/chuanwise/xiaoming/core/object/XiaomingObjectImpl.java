package cn.chuanwise.xiaoming.core.object;

import cn.chuanwise.xiaoming.api.bot.XiaomingBot;
import cn.chuanwise.xiaoming.api.object.XiaomingObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class XiaomingObjectImpl implements XiaomingObject {
    protected transient XiaomingBot xiaomingBot;
}