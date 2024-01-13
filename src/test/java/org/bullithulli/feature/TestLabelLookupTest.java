package org.bullithulli.feature;

import org.bullithulli.modder2;
import org.junit.Test;

import java.io.IOException;
import java.util.Objects;

import static junit.framework.TestCase.assertEquals;
import static org.bullithulli.utils.TestSysUtils.*;

public class TestLabelLookupTest {
    String absolutePathDir1 = Objects.requireNonNull(getClass().getClassLoader().getResource("dir1/")).getPath();
    String absolutePathDir3 = Objects.requireNonNull(getClass().getClassLoader().getResource("dir3/")).getPath();

    @Test
    public void returnTest1() throws IOException {
        String solution = """
                label five:
                    five
                    return
                """;

        modder2 modder2 = new modder2();
        disableSysOuts();
        String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("dir1/")).getPath();
        modder2.verifyAndExecuteLabelLookupFeature("five", absolutePath, false, true, false, true, true, false);
        enableSysOuts();
        assertEquals(solution, getStringFromSysOuts());
    }

    /*
     * Default Behaviour
     *
     * @throws IOException
     */
    @Test
    public void returnTest2() throws IOException {
        String solution = """
                label one:
                    one
                label two:
                    two
                label three:
                    three
                    return
                BULLITULLI-MODDER2: -----------------------------------      INNER_LABELS_FOUND  [label two:]   -----------------------------------
                BULLITULLI-MODDER2: -----------------------------------      INNER_LABELS_FOUND  [label three:]   -----------------------------------
                """;

        modder2 modder2 = new modder2();
        disableSysOuts();

        modder2.verifyAndExecuteLabelLookupFeature("one", absolutePathDir1, false, false, true, true, true, false);
        enableSysOuts();
        assertEquals(solution, getStringFromSysOuts());
    }


    @Test
    public void returnTest3() throws IOException {
        String solution = """
                label one:
                    one
                """;
        modder2 modder2 = new modder2();
        disableSysOuts();
        String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("dir1/")).getPath();
        modder2.verifyAndExecuteLabelLookupFeature("one", absolutePath, false, true, false, true, true, false);
        enableSysOuts();
        assertEquals(solution, getStringFromSysOuts());
    }

    @Test
    public void returnTest4() throws IOException {
        String solution = """
                label one:
                    one
                label two:
                    two
                label three:
                    three
                    return
                BULLITULLI-MODDER2: -----------------------------------      INNER_LABELS_FOUND  [label two:]   -----------------------------------
                BULLITULLI-MODDER2: -----------------------------------      INNER_LABELS_FOUND  [label three:]   -----------------------------------
                """;

        modder2 modder2 = new modder2();
        disableSysOuts();
        modder2.verifyAndExecuteLabelLookupFeature("one", absolutePathDir1, false, false, false, true, true, false);
        enableSysOuts();
        assertEquals(solution, getStringFromSysOuts());
    }

    @Test
    public void innerJumpTest1() throws IOException {
        String solution = """
                label ten:
                    ten
                    jump twelve
                BULLITULLI-MODDER2: -----------------------------------      JUMPS_FOUND  [jump twelve]   -----------------------------------
                label twelve:
                    jump one
                BULLITULLI-MODDER2: -----------------------------------      JUMPS_FOUND  [jump one]   -----------------------------------
                label one:
                    one
                label two:
                    two
                label three:
                    three
                    return
                BULLITULLI-MODDER2: -----------------------------------      INNER_LABELS_FOUND  [label two:]   -----------------------------------
                BULLITULLI-MODDER2: -----------------------------------      INNER_LABELS_FOUND  [label three:]   -----------------------------------
                """;

        modder2 modder2 = new modder2();
        disableSysOuts();
        modder2.verifyAndExecuteLabelLookupFeature("ten", absolutePathDir1, false, false, false, true, true, false);
        enableSysOuts();
        assertEquals(solution, getStringFromSysOuts());
    }

    @Test
    public void innerJumpTest2() throws IOException {
        String solution = """
                label ten:
                    ten
                    jump twelve
                BULLITULLI-MODDER2: -----------------------------------      JUMPS_FOUND  [jump twelve]   -----------------------------------
                label twelve:
                    jump one
                BULLITULLI-MODDER2: -----------------------------------      JUMPS_FOUND  [jump one]   -----------------------------------
                label one:
                    one
                """;

        modder2 modder2 = new modder2();
        disableSysOuts();
        modder2.verifyAndExecuteLabelLookupFeature("ten", absolutePathDir1, false, true, false, true, true, false);
        enableSysOuts();
        assertEquals(solution, getStringFromSysOuts());
    }

    @Test
    public void innerJumpTest3() throws IOException {
        String solution = """
                label ten:
                    ten
                    jump twelve
                BULLITULLI-MODDER2: -----------------------------------      JUMPS_FOUND  [jump twelve]   -----------------------------------
                """;

        modder2 modder2 = new modder2();
        disableSysOuts();
        modder2.verifyAndExecuteLabelLookupFeature("ten", absolutePathDir1, false, true, false, false, true, false);
        enableSysOuts();
        assertEquals(solution, getStringFromSysOuts());
    }

    @Test
    public void innerJumpTest4() throws IOException {
        String solution = """
                label ten:
                    ten
                    jump twelve
                BULLITULLI-MODDER2: -----------------------------------      JUMPS_FOUND  [jump twelve]   -----------------------------------
                label twelve:
                    jump one
                BULLITULLI-MODDER2: -----------------------------------      JUMPS_FOUND  [jump one]   -----------------------------------
                label one:
                    one
                label two:
                    two
                label three:
                    three
                    return
                BULLITULLI-MODDER2: -----------------------------------      INNER_LABELS_FOUND  [label two:]   -----------------------------------
                BULLITULLI-MODDER2: -----------------------------------      INNER_LABELS_FOUND  [label three:]   -----------------------------------
                """;

        modder2 modder2 = new modder2();
        disableSysOuts();
        modder2.verifyAndExecuteLabelLookupFeature("ten", absolutePathDir1, false, false, true, true, true, false);
        enableSysOuts();
        assertEquals(solution, getStringFromSysOuts());
    }

    @Test
    public void innerJumpTest5() throws IOException {
        String solution = """
                label seventeen:
                    seventeen
                    if a<10
                        jump one
                    else
                        jump eight
                return
                BULLITULLI-MODDER2: -----------------------------------      JUMPS_FOUND  [jump one]   -----------------------------------
                BULLITULLI-MODDER2: -----------------------------------      JUMPS_FOUND  [jump eight]   -----------------------------------
                label one:
                    one
                label two:
                    two
                label three:
                    three
                    return
                BULLITULLI-MODDER2: -----------------------------------      INNER_LABELS_FOUND  [label two:]   -----------------------------------
                BULLITULLI-MODDER2: -----------------------------------      INNER_LABELS_FOUND  [label three:]   -----------------------------------
                label eight:
                    call ten
                label nine:
                    nine
                    return
                BULLITULLI-MODDER2: -----------------------------------      INNER_LABELS_FOUND  [label nine:]   -----------------------------------
                BULLITULLI-MODDER2: -----------------------------------      INNER_CALLS_FOUND  [call ten]   -----------------------------------
                label ten:
                    ten
                    jump twelve
                BULLITULLI-MODDER2: -----------------------------------      JUMPS_FOUND  [jump twelve]   -----------------------------------
                label twelve:
                    jump one
                BULLITULLI-MODDER2: -----------------------------------      JUMPS_FOUND  [jump one]   -----------------------------------
                """;

        modder2 modder2 = new modder2();
        disableSysOuts();
        modder2.verifyAndExecuteLabelLookupFeature("seventeen", absolutePathDir1, false, false, true, true, true, false);
        enableSysOuts();
        assertEquals(solution, getStringFromSysOuts());
    }

    @Test
    public void innerJumpTest6() throws IOException {
        String solution = """
                label seventeen:
                    seventeen
                    if a<10
                        jump one
                    else
                        jump eight
                return
                BULLITULLI-MODDER2: -----------------------------------      JUMPS_FOUND  [jump one]   -----------------------------------
                BULLITULLI-MODDER2: -----------------------------------      JUMPS_FOUND  [jump eight]   -----------------------------------
                label one:
                    one
                label two:
                    two
                label three:
                    three
                    return
                BULLITULLI-MODDER2: -----------------------------------      INNER_LABELS_FOUND  [label two:]   -----------------------------------
                BULLITULLI-MODDER2: -----------------------------------      INNER_LABELS_FOUND  [label three:]   -----------------------------------
                label eight:
                    call ten
                label nine:
                    nine
                    return
                BULLITULLI-MODDER2: -----------------------------------      INNER_LABELS_FOUND  [label nine:]   -----------------------------------
                BULLITULLI-MODDER2: -----------------------------------      INNER_CALLS_FOUND  [call ten]   -----------------------------------
                """;

        modder2 modder2 = new modder2();
        disableSysOuts();
        modder2.verifyAndExecuteLabelLookupFeature("seventeen", absolutePathDir1, false, false, true, true, false, false);
        enableSysOuts();
        assertEquals(solution, getStringFromSysOuts());
    }

    @Test
    public void innerJumpTest7() throws IOException {
        String solution = """
                label seventeen:
                    seventeen
                    if a<10
                        jump one
                    else
                        jump eight
                return
                BULLITULLI-MODDER2: -----------------------------------      JUMPS_FOUND  [jump one]   -----------------------------------
                BULLITULLI-MODDER2: -----------------------------------      JUMPS_FOUND  [jump eight]   -----------------------------------
                """;

        modder2 modder2 = new modder2();
        disableSysOuts();
        modder2.verifyAndExecuteLabelLookupFeature("seventeen", absolutePathDir1, false, false, true, false, false, false);
        enableSysOuts();
        assertEquals(solution, getStringFromSysOuts());
    }

    @Test
    public void innerCallTest1() throws IOException {
        String solution = """
                label a1:
                    a1
                    call one
                    call two
                    call ten
                    return
                BULLITULLI-MODDER2: -----------------------------------      INNER_CALLS_FOUND  [call one]   -----------------------------------
                BULLITULLI-MODDER2: -----------------------------------      INNER_CALLS_FOUND  [call two]   -----------------------------------
                BULLITULLI-MODDER2: -----------------------------------      INNER_CALLS_FOUND  [call ten]   -----------------------------------
                label one:
                    one
                label two:
                    two
                label three:
                    three
                    return
                BULLITULLI-MODDER2: -----------------------------------      INNER_LABELS_FOUND  [label two:]   -----------------------------------
                BULLITULLI-MODDER2: -----------------------------------      INNER_LABELS_FOUND  [label three:]   -----------------------------------
                label ten:
                    ten
                    jump twelve
                BULLITULLI-MODDER2: -----------------------------------      JUMPS_FOUND  [jump twelve]   -----------------------------------
                """;

        modder2 modder2 = new modder2();
        disableSysOuts();
        modder2.verifyAndExecuteLabelLookupFeature("a1", absolutePathDir1, false, false, true, false, true, false);
        enableSysOuts();
        assertEquals(solution, getStringFromSysOuts());
    }

    @Test
    public void innerCallTest2() throws IOException {
        String solution = """
                label a1:
                    a1
                    call one
                    call two
                    call ten
                    return
                BULLITULLI-MODDER2: -----------------------------------      INNER_CALLS_FOUND  [call one]   -----------------------------------
                BULLITULLI-MODDER2: -----------------------------------      INNER_CALLS_FOUND  [call two]   -----------------------------------
                BULLITULLI-MODDER2: -----------------------------------      INNER_CALLS_FOUND  [call ten]   -----------------------------------
                label one:
                    one
                label two:
                    two
                label three:
                    three
                    return
                BULLITULLI-MODDER2: -----------------------------------      INNER_LABELS_FOUND  [label two:]   -----------------------------------
                BULLITULLI-MODDER2: -----------------------------------      INNER_LABELS_FOUND  [label three:]   -----------------------------------
                label ten:
                    ten
                    jump twelve
                BULLITULLI-MODDER2: -----------------------------------      JUMPS_FOUND  [jump twelve]   -----------------------------------
                label twelve:
                    jump one
                BULLITULLI-MODDER2: -----------------------------------      JUMPS_FOUND  [jump one]   -----------------------------------
                """;

        modder2 modder2 = new modder2();
        disableSysOuts();
        modder2.verifyAndExecuteLabelLookupFeature("a1", absolutePathDir1, false, false, true, true, true, false);
        enableSysOuts();
        assertEquals(solution, getStringFromSysOuts());
    }

    @Test
    public void screenCallTest1() throws IOException {
        String solution = """
                label test1:
                    call screen hallway_nav
                screen hallway_nav():
                    imagebutton auto "door_livingroom_%s":
                        action Jump ("livingroom")
                screen hallway_nav2():
                        action Jump ("bathroom")
                label livingroom:
                    livingroom
                    return
                BULLITULLI-MODDER2: -----------------------------------      JUMPS_FOUND  [action Jump ("livingroom")]   -----------------------------------
                BULLITULLI-MODDER2: -----------------------------------      JUMPS_FOUND  [action Jump ("bathroom")]   -----------------------------------
                BULLITULLI-MODDER2: -----------------------------------      INNER_LABELS_FOUND  [screen hallway_nav():]   -----------------------------------
                BULLITULLI-MODDER2: -----------------------------------      INNER_LABELS_FOUND  [screen hallway_nav2():]   -----------------------------------
                BULLITULLI-MODDER2: -----------------------------------      INNER_LABELS_FOUND  [label livingroom:]   -----------------------------------
                BULLITULLI-MODDER2: -----------------------------------      INNER_CALLS_FOUND  [call screen hallway_nav]   -----------------------------------
                label bathroom:
                    bathroom
                label car:
                    car
                BULLITULLI-MODDER2: -----------------------------------      INNER_LABELS_FOUND  [label car:]   -----------------------------------
                """;

        modder2 modder2 = new modder2();
        disableSysOuts();
        modder2.verifyAndExecuteLabelLookupFeature("test1", absolutePathDir3, false, false, true, true, true, true);
        enableSysOuts();
        assertEquals(solution, getStringFromSysOuts());
    }
}
