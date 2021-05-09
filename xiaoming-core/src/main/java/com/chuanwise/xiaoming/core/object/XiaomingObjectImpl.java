package com.chuanwise.xiaoming.core.object;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.object.XiaomingObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class XiaomingObjectImpl implements XiaomingObject {
    /**
     * 小明本体的引用
     */
    @Setter
    @Getter
    transient XiaomingBot xiaomingBot;
}