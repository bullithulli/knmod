package org.bullithulli.feature.sandbox;

import org.junit.Test;
import java.util.regex.Matcher;
import static org.junit.Assert.*;

public class SandboxRegexValidationTest {

    @Test
    public void testLabelPattern() {
        String[] validLabels = {
            "label my_label:",
            "label  my_label :",
            "label my_label(arg1, arg2=\"foo\"):",
            "label rötärnØ():",
            "label test_unicode_漢字:"
        };
        for (String label : validLabels) {
            Matcher m = SandboxScanner.LABEL_PATTERN.matcher(label);
            assertTrue("Should match label: " + label, m.find());
        }

        Matcher m = SandboxScanner.LABEL_PATTERN.matcher("label my_label:");
        m.find();
        assertEquals("my_label", m.group(1));

        m = SandboxScanner.LABEL_PATTERN.matcher("label rötärnØ():");
        m.find();
        assertEquals("rötärnØ", m.group(1));
    }

    @Test
    public void testConditionPattern() {
        String[] validConditions = {
            "week == 1",
            "corruption > 5",
            "time_of_day == \"morning\"",
            "location == 'Main Hall'",
            "is_day != False"
        };
        for (String cond : validConditions) {
            Matcher m = SandboxScanner.CONDITION_PATTERN.matcher(cond);
            assertTrue("Should match condition: " + cond, m.find());
        }

        Matcher m = SandboxScanner.CONDITION_PATTERN.matcher("if location == \"Main Hall\":");
        assertTrue(m.find());
        assertEquals("location", m.group(1));
        assertEquals("==", m.group(2));
        assertEquals("\"Main Hall\"", m.group(3)); // Including spaces in quotes!
    }

    @Test
    public void testAssignmentPattern() {
        String[] validAssignments = {
            "$ day += 1",
            "$  corruption  -=  5",
            "$\tmy_var=  \"string\"",
            "$is_ready=True"
        };
        for (String assign : validAssignments) {
            Matcher m = SandboxScanner.ASSIGNMENT_PATTERN.matcher(assign);
            assertTrue("Should match assignment: " + assign, m.find());
        }
    }

    @Test
    public void testJumpAndCallPattern() {
        Matcher m1 = SandboxFlattener.JUMP_PATTERN.matcher("jump my_label");
        assertTrue(m1.find());
        assertEquals("my_label", m1.group(1));

        Matcher m2 = SandboxFlattener.JUMP_PATTERN.matcher("jump my_label  # goes here");
        assertTrue(m2.find());
        assertEquals("my_label", m2.group(1));

        Matcher m3 = SandboxFlattener.CALL_PATTERN.matcher("call my_scene from _call_1");
        assertTrue(m3.find());
        assertEquals("my_scene", m3.group(1));
    }

    @Test
    public void testRenpyCallPattern() {
        Matcher m = SandboxFlattener.RENPY_CALL_PATTERN.matcher("$ renpy.call(\"hidden_python_scene\")");
        assertTrue(m.find());
        assertEquals("hidden_python_scene", m.group(1));
        
        Matcher m2 = SandboxFlattener.RENPY_CALL_PATTERN.matcher("renpy.call('single_quotes')");
        assertTrue(m2.find());
        assertEquals("single_quotes", m2.group(1));
    }
}
