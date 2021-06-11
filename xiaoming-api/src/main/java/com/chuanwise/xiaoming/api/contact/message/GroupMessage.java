package com.chuanwise.xiaoming.api.contact.message;

import com.chuanwise.xiaoming.api.schedule.async.AsyncResult;
import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageSource;
import net.mamoe.mirai.message.data.QuoteReply;

public interface GroupMessage extends Message {
    @Override
    GroupXiaomingUser getSender();

    @Override
    GroupContact getContact();

    @Override
    GroupMessage clone() throws CloneNotSupportedException;
}
