package org.example.words;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class WordUtilTest {

    @Test
    public void testRandomizerWithinBounds() {
        int max = 10;
        for (int i = 0; i < 100; i++) {
            int rand = WordUtil.Randomizer(max);
            assertTrue(rand >= 0 && rand < max, "Randomizer returned value out of bounds");
        }
    }

    @Test
    public void testTypeCheckReturnsNullForNoBrackets() {
        assertNull(WordUtil.TypeCheck("hello"));
        assertNull(WordUtil.TypeCheck("world"));
    }

    @Test
    public void testTypeCheckDetectsType() {
        assertEquals("[noun]", WordUtil.TypeCheck("foo[noun]bar"));
        assertEquals("[verb]", WordUtil.TypeCheck("[verb]"));
    }

    @Test
    public void testTypeSubstituteReplacesPlaceholder() {
        String original = "I am a [noun].";
        String replaced = WordUtil.TypeSubstitute(original, "[noun]", "developer");
        assertEquals("I am a developer.", replaced);
    }
}