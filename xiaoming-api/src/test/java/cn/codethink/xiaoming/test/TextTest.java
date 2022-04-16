package cn.codethink.xiaoming.test;

import cn.codethink.xiaoming.util.Texts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TextTest {
    
    @Test
    void testSerialize() {
        Assertions.assertEquals("a b c d e", Texts.serializeText("a b c d e"));
        Assertions.assertEquals("", Texts.serializeText(""));
        Assertions.assertEquals("\\[", Texts.serializeText("["));
        Assertions.assertEquals("a b c \\[ e f \\:", Texts.serializeText("a b c [ e f :"));
    }
    
    @Test
    void testDeserialize() {
        Assertions.assertEquals("a b c d e", Texts.deserializeText("a b c d e"));
        Assertions.assertEquals("", Texts.deserializeText(""));
        Assertions.assertEquals("[", Texts.deserializeText("\\["));
        Assertions.assertEquals("a b c [ e f :", Texts.deserializeText("a b c \\[ e f \\:"));
    }
}
