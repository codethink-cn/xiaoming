package cn.codethink.xiaoming.message.module.modules;

import cn.chuanwise.common.util.Collections;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.Contact;
import cn.codethink.xiaoming.contact.Mass;
import cn.codethink.xiaoming.contact.Member;
import cn.codethink.xiaoming.message.basic.AllAccountAt;
import cn.codethink.xiaoming.message.basic.At;
import cn.codethink.xiaoming.message.basic.SingletonAccountAt;
import cn.codethink.xiaoming.message.module.deserialize.Deserializer;
import cn.codethink.xiaoming.message.module.deserialize.DeserializerValue;
import cn.codethink.xiaoming.message.module.serialize.Serializer;
import cn.codethink.xiaoming.message.module.summary.Summarizer;
import cn.codethink.xiaoming.message.module.summary.SummaryContext;

import java.util.List;
import java.util.Objects;

/**
 * @see At
 *
 * @author Chuanwise
 */
public class AtModules {
    
    ///////////////////////////////////////////////////////////////////////////
    // at singleton account
    ///////////////////////////////////////////////////////////////////////////
    
    @Serializer(SingletonAccountAt.class)
    List<String> serializeSingletonAccountAt(SingletonAccountAt at) {
        return Collections.asUnmodifiableList(
            "at",
            "account",
            "singleton",
            at.getTargetCode().toString()
        );
    }
    
    @Deserializer("at:account:singleton:??")
    SingletonAccountAt deserializeSingletonAccountAt(@DeserializerValue String code) {
        return SingletonAccountAt.newInstance(Code.parseCode(code));
    }
    
    @Summarizer(SingletonAccountAt.class)
    String summarySingletonAccountAt(SingletonAccountAt at,
                                          SummaryContext context) {
        final Contact contact = context.getContact();
    
        if (Objects.nonNull(contact)) {
            
            // if is in mass contact
            // display member nick
            if (contact instanceof Mass) {
                
                // if the member is in the contact
                // display his sender name
                final Member member = ((Mass) contact).getMember(at.getTargetCode());
                if (Objects.nonNull(member)) {
                    return "@" + member.getSenderName();
                }
            }
        }
        return "@" + at.getTargetCode().asString();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // at all
    ///////////////////////////////////////////////////////////////////////////
    
    final List<String> serializedAllAccountAt = Collections.asUnmodifiableList(
        "at",
        "account",
        "all"
    );
    
    final String summarizedAllAccountAt = "@全体成员";
    
    @Serializer(AllAccountAt.class)
    List<String> serializeAllAccountAt() {
        return serializedAllAccountAt;
    }

    @Deserializer("at:account:all")
    AllAccountAt parseAllAccountAt() {
        return AllAccountAt.getInstance();
    }
    
    @Summarizer(AllAccountAt.class)
    String summaryAllAccountAt() {
        return summarizedAllAccountAt;
    }
}
