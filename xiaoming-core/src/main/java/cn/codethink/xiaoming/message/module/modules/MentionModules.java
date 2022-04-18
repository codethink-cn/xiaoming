package cn.codethink.xiaoming.message.module.modules;

import cn.chuanwise.common.util.Collections;
import cn.codethink.xiaoming.code.Code;
import cn.codethink.xiaoming.contact.Contact;
import cn.codethink.xiaoming.contact.Mass;
import cn.codethink.xiaoming.contact.Member;
import cn.codethink.xiaoming.message.basic.AllAccountMention;
import cn.codethink.xiaoming.message.basic.SingletonAccountMention;
import cn.codethink.xiaoming.message.module.deserialize.Deserializer;
import cn.codethink.xiaoming.message.module.deserialize.DeserializerValue;
import cn.codethink.xiaoming.message.module.serialize.Serializer;
import cn.codethink.xiaoming.message.module.summary.Summarizer;
import cn.codethink.xiaoming.message.module.summary.SummaryContext;

import java.util.List;
import java.util.Objects;

/**
 * @see cn.codethink.xiaoming.message.basic.Mention
 *
 * @author Chuanwise
 */
public class MentionModules {
    
    ///////////////////////////////////////////////////////////////////////////
    // mention singleton account
    ///////////////////////////////////////////////////////////////////////////
    
    @Serializer(SingletonAccountMention.class)
    List<String> serializeSingletonAccountMention(SingletonAccountMention mention) {
        return Collections.asUnmodifiableList(
            "mention",
            "account",
            "singleton",
            mention.getTargetCode().toString()
        );
    }
    
    @Deserializer("mention:account:singleton:??")
    SingletonAccountMention deserializeSingletonAccountMention(@DeserializerValue String code) {
        return SingletonAccountMention.newInstance(Code.parseCode(code));
    }
    
    @Summarizer(SingletonAccountMention.class)
    String summarySingletonAccountMention(SingletonAccountMention mention,
                                          SummaryContext context) {
        final Contact contact = context.getContact();
    
        if (Objects.nonNull(contact)) {
            
            // if is in mass contact
            // display member nick
            if (contact instanceof Mass) {
                
                // if the member is in the contact
                // display his sender name
                final Member member = ((Mass) contact).getMember(mention.getTargetCode());
                if (Objects.nonNull(member)) {
                    return "@" + member.getSenderName();
                }
            }
        }
        return "@" + mention.getTargetCode().asString();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // mention all
    ///////////////////////////////////////////////////////////////////////////
    
    final List<String> serializedAllAccountMention = Collections.asUnmodifiableList(
        "mention",
        "account",
        "all"
    );
    
    final String summarizedAllAccountMention = "@全体成员";
    
    @Serializer(AllAccountMention.class)
    List<String> serializeAllAccountMention() {
        return serializedAllAccountMention;
    }

    @Deserializer("mention:account:all")
    AllAccountMention parseAllAccountMention() {
        return AllAccountMention.getInstance();
    }
    
    @Summarizer(AllAccountMention.class)
    String summaryAllAccountMention() {
        return summarizedAllAccountMention;
    }
}
