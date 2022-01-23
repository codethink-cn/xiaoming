package cn.chuanwise.xiaoming.object;

import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.object.XiaomingObject;
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