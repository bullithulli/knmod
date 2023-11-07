package org.bullithulli.feature;

import org.bullithulli.modder2;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static junit.framework.TestCase.assertEquals;

public class TestKnmodTest {
    @Test
    public void test1() throws Exception {
        String solution = """
                define KN_MOD = Character("KN_MOD", color="#ff0000")
                # java -jar modder-2.jar null
                # ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
                # modded by modder2vNov2-2023 program. Created by BulliThulli
                python:
                        kstm = renpy.input("What's your relationship with her? (If you leave it blank, she'll be your stepmom.)")
                        kstm = kstm.strip()
                        if not kstm:
                            kstm = "Stepmom"
                        stepson = renpy.input("What's her relationship with you? (If you leave it blank, you'll be her stepson.)")
                        stepson = stepson.strip()
                hello
                World
                KN_MOD "ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"
                KN_MOD "modded by modder2 vNov2-2023 program. Created by BulliThulli"
                """;
        modder2 modder2 = new modder2();
        String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("dir4/d.rpy")).getPath();
        modder2.verifyAndExecuteKNModFeature(absolutePath, null, 0);
        String content = new String(Files.readAllBytes(Paths.get("/tmp/out")));
        assertEquals(solution, content);
    }

    @Test
    public void test2() throws Exception {
        String solution = """
                define KN_MOD = Character("KN_MOD", color="#ff0000")
                # java -jar modder-2.jar null
                # ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
                # modded by modder2vNov2-2023 program. Created by BulliThulli
                python:
                        kstm = renpy.input("What's your relationship with her? (If you leave it blank, she'll be your stepmom.)")
                kstm = kstm.strip()
                KN_MOD "if not kstm:"
                kstm = "Stepmom"
                stepson = renpy.input("What's her relationship with you? (If you leave it blank, you'll be her stepson.)")
                stepson = stepson.strip()
                hello
                World
                KN_MOD "ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"
                KN_MOD "modded by modder2 vNov2-2023 program. Created by BulliThulli"
                """;
        modder2 modder2 = new modder2();
        String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("dir4/d.rpy")).getPath();
        modder2.verifyAndExecuteKNModFeature(absolutePath, null, 2);
        String content = new String(Files.readAllBytes(Paths.get("/tmp/out")));
        assertEquals(solution, content);
    }

    @Test
    public void test3() throws Exception {
        String solution = """
                define KN_MOD = Character("KN_MOD", color="#ff0000")
                # java -jar modder-2.jar null
                # ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
                # modded by modder2vNov2-2023 program. Created by BulliThulli
                KN_MOD "label E4_Start: #Figure out which branch were on"
                KN_MOD "if mc_cancel_apurna_date == 1 or apurna_love == 0 or apurna_love == 2: #on Aprils branch"
                $E3Branch = "April"
                KN_MOD "jump E4_April"
                KN_MOD "else: #on Apurnas branch"
                $E3Branch = "Apurna"
                KN_MOD "jump E4_Apurna"
                KN_MOD "ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"
                KN_MOD "modded by modder2 vNov2-2023 program. Created by BulliThulli"
                """;
        modder2 modder2 = new modder2();
        String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("dir4/f.rpy")).getPath();
        modder2.verifyAndExecuteKNModFeature(absolutePath, null, 0);
        String content = new String(Files.readAllBytes(Paths.get("/tmp/out")));
        assertEquals(solution, content);
    }

    @Test
    public void test4() throws Exception {
        String solution = """
                define KN_MOD = Character("KN_MOD", color="#ff0000")
                # java -jar modder-2.jar null
                # ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
                # modded by modder2vNov2-2023 program. Created by BulliThulli
                KN_MOD "if v3s27_mc_baby_schedulewednesday == BabyDuty.ALONE or v3s27_mc_baby_schedulewednesday == BabyDuty.WITH_PARTNER: # IF MC has baby duties tonight, MC has baby duty Wednesday night"
                hello #somecomment
                KN_MOD "else if v3s27_mc_baby_schedulewednesday == BabyDuty.ALONE or v3s27_mc_baby_schedulewednesday == BabyDuty.WITH_PARTNER: # IF MC has baby duties tonight, MC has baby duty Wednesday night"
                world
                KN_MOD "ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"
                KN_MOD "modded by modder2 vNov2-2023 program. Created by BulliThulli"
                """;
        modder2 modder2 = new modder2();
        String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("dir4/g.rpy")).getPath();
        modder2.verifyAndExecuteKNModFeature(absolutePath, null, 0);
        String content = new String(Files.readAllBytes(Paths.get("/tmp/out")));
        assertEquals(solution, content);
    }

    @Test
    public void test6() throws Exception {
        String solution = """
                define KN_MOD = Character("KN_MOD", color="#ff0000")
                # java -jar modder-2.jar null
                # ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
                # modded by modder2vNov2-2023 program. Created by BulliThulli
                KN_MOD "label z:"
                KN_MOD "menu:"
                KN_MOD "What about the fraternities?:"
                KN_MOD "if mc.frat == Frat.WOLVES: # -if mc.frat, Frat.WOLVES"
                u "Do you think they're coming for the Wolves too?"
                KN_MOD "else: ### ERROR: else"
                u "Do you think they're reviewing the Apes?"
                KN_MOD "ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"
                KN_MOD "modded by modder2 vNov2-2023 program. Created by BulliThulli"
                """;
        modder2 modder2 = new modder2();
        String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("knmodtests/b.rpy")).getPath();
        modder2.verifyAndExecuteKNModFeature(absolutePath, null, 0);
        String content = new String(Files.readAllBytes(Paths.get("/tmp/out")));
        assertEquals(solution, content);
    }

    @Test
    public void test7() throws Exception {
        String solution = """
                define KN_MOD = Character("KN_MOD", color="#ff0000")
                # java -jar modder-2.jar null
                # ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
                # modded by modder2vNov2-2023 program. Created by BulliThulli
                KN_MOD "label z:"
                KN_MOD "menu dsa:"
                KN_MOD "Give me that:"
                KN_MOD "menufail_label=ep2s83_quiet: #TIMED CHOICE if time expires, jump to Say nothing"
                KN_MOD "Give me that:"
                KN_MOD "ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"
                KN_MOD "modded by modder2 vNov2-2023 program. Created by BulliThulli"
                """;
        modder2 modder2 = new modder2();
        String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("knmodtests/c.rpy")).getPath();
        modder2.verifyAndExecuteKNModFeature(absolutePath, null, 0);
        String content = new String(Files.readAllBytes(Paths.get("/tmp/out")));
        assertEquals(solution, content);
    }

    //@Test
    public void test5() throws Exception {
        String solution = """
                define KN_MOD = Character("KN_MOD", color="#ff0000")
                # java -jar modder-2.jar null
                # ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
                # modded by modder2vNov2-2023 program. Created by BulliThulli
                KN_MOD "if v3s27_mc_baby_schedulewednesday == BabyDuty.ALONE or v3s27_mc_baby_schedulewednesday == BabyDuty.WITH_PARTNER: # IF MC has baby duties tonight, MC has baby duty Wednesday night"
                hello #somecomment
                KN_MOD "else if v3s27_mc_baby_schedulewednesday == BabyDuty.ALONE or v3s27_mc_baby_schedulewednesday == BabyDuty.WITH_PARTNER: # IF MC has baby duties tonight, MC has baby duty Wednesday night"
                world
                KN_MOD "ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"
                KN_MOD "modded by modder2 vNov2-2023 program. Created by BulliThulli"
                """;
        modder2 modder2 = new modder2();
        String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("knmodtests/a.rpy")).getPath();
        modder2.verifyAndExecuteKNModFeature(absolutePath, null, 0);
        String content = new String(Files.readAllBytes(Paths.get("/tmp/out")));
        assertEquals(solution, content);
    }

    @Test
    public void test8() throws Exception {
        String solution = """
                define KN_MOD = Character("KN_MOD", color="#ff0000")
                # java -jar modder-2.jar null
                # ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
                # modded by modder2vNov2-2023 program. Created by BulliThulli
                KN_MOD "return"
                KN_MOD "return#dsaasd"
                KN_MOD "ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"
                KN_MOD "modded by modder2 vNov2-2023 program. Created by BulliThulli"
                """;
        modder2 modder2 = new modder2();
        String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("knmodtests/returntest.rpy")).getPath();
        modder2.verifyAndExecuteKNModFeature(absolutePath, null, 0);
        String content = new String(Files.readAllBytes(Paths.get("/tmp/out")));
        assertEquals(solution, content);
    }

// TODO: 11/4/23  
//    public void test3() throws Exception {
//        String solution = """
//                define KN_MOD = Character("KN_MOD", color="#ff0000")
//                # java null
//                # ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
//                # modded by modder2vNov2-2023 program. Created by BulliThulli
//                python:
//                        kstm = renpy.input("What's your relationship with her? (If you leave it blank, she'll be your stepmom.)")
//                kstm = kstm.strip()
//                KN_MOD "if not kstm:"
//                kstm = "Stepmom"
//                stepson = renpy.input("What's her relationship with you? (If you leave it blank, you'll be her stepson.)")
//                stepson = stepson.strip()
//                hello
//                World
//                KN_MOD "ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"
//                KN_MOD "modded by modder2 vNov2-2023 program. Created by BulliThulli"
//                """;
//        modder2 modder2 = new modder2();
//        String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("dir4/e.rpy")).getPath();
//        modder2.verifyAndExecuteKNModFeature(absolutePath, null, 0);
//        String content = new String(Files.readAllBytes(Paths.get("/tmp/out")));
//        assertEquals(solution, content);
//    }
}
