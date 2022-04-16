package cn.codethink.xiaoming.test;

import cn.codethink.xiaoming.util.Texts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TextTest {
    
    @Test
    void testSerialize() {
        Assertions.assertEquals("a b c d e", Texts.escape("a b c d e"));
        Assertions.assertEquals("", Texts.escape(""));
        Assertions.assertEquals("\\[", Texts.escape("["));
        Assertions.assertEquals("a b c \\[ e f \\:", Texts.escape("a b c [ e f :"));
    }
    
    @Test
    void testDeserialize() {
        Assertions.assertEquals("a b c d e", Texts.unescape("a b c d e"));
        Assertions.assertEquals("", Texts.unescape(""));
        Assertions.assertEquals("[", Texts.unescape("\\["));
        Assertions.assertEquals("a b c [ e f :", Texts.unescape("a b c \\[ e f \\:"));
    }
}
