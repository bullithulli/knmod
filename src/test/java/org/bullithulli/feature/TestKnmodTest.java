package org.bullithulli.feature;

import org.bullithulli.Modder2;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static junit.framework.TestCase.assertEquals;
import static org.bullithulli.Modder2.version;

public class TestKnmodTest {
	@Test
	public void test1() throws Exception {
		String solution = String.format("""
				define KN_MOD = Character("KN_MOD", color="#ff0000")
				# java -jar modder-2.jar null
				# ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
				# modded by modder2%s program. Created by BulliThulli
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
				KN_MOD "modded by modder2 %s program. Created by BulliThulli"
				python:
				    renpy.input("Ignore this box. It is just added by me to verify if you reached the end of the game", length=32)
				""", version, version);
		Modder2 modder2 = new Modder2();
		String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("dir4/d.rpy")).getPath();
		modder2.verifyAndExecuteKNModFeature(absolutePath, null, null);
		String content = new String(Files.readAllBytes(Paths.get("/tmp/out")));
		assertEquals(solution, content);
	}

	@Test
	public void test2() throws Exception {
		String solution = String.format("""
				define KN_MOD = Character("KN_MOD", color="#ff0000")
				# java -jar modder-2.jar null
				# ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
				# modded by modder2%s program. Created by BulliThulli
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
				KN_MOD "modded by modder2 %s program. Created by BulliThulli"
				python:
				    renpy.input("Ignore this box. It is just added by me to verify if you reached the end of the game", length=32)
				""", version, version);
		Modder2 modder2 = new Modder2();
		String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("dir4/d.rpy")).getPath();
		modder2.verifyAndExecuteKNModFeature(absolutePath, null, null);
		String content = new String(Files.readAllBytes(Paths.get("/tmp/out")));
		assertEquals(solution, content);
	}

	@Test
	public void test3() throws Exception {
		String solution = String.format("""
				define KN_MOD = Character("KN_MOD", color="#ff0000")
				# java -jar modder-2.jar null
				# ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
				# modded by modder2%s program. Created by BulliThulli
				KN_MOD "label E4_Start: #Figure out which branch were on"
				KN_MOD "if mc_cancel_apurna_date == 1 or apurna_love == 0 or apurna_love == 2: #on Aprils branch"
				$E3Branch = "April"
				KN_MOD "jump E4_April"
				KN_MOD "else: #on Apurnas branch"
				$E3Branch = "Apurna"
				KN_MOD "jump E4_Apurna"
				KN_MOD "ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"
				KN_MOD "modded by modder2 %s program. Created by BulliThulli"
				python:
				    renpy.input("Ignore this box. It is just added by me to verify if you reached the end of the game", length=32)
				""", version, version);
		Modder2 modder2 = new Modder2();
		String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("dir4/f.rpy")).getPath();
		modder2.verifyAndExecuteKNModFeature(absolutePath, null, null);
		String content = new String(Files.readAllBytes(Paths.get("/tmp/out")));
		assertEquals(solution, content);
	}

	@Test
	public void test4() throws Exception {
		String solution = String.format("""
				define KN_MOD = Character("KN_MOD", color="#ff0000")
				# java -jar modder-2.jar null
				# ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
				# modded by modder2%s program. Created by BulliThulli
				KN_MOD "if v3s27_mc_baby_schedulewednesday == BabyDuty.ALONE or v3s27_mc_baby_schedulewednesday == BabyDuty.WITH_PARTNER: # IF MC has baby duties tonight, MC has baby duty Wednesday night"
				hello #somecomment
				KN_MOD "else if v3s27_mc_baby_schedulewednesday == BabyDuty.ALONE or v3s27_mc_baby_schedulewednesday == BabyDuty.WITH_PARTNER: # IF MC has baby duties tonight, MC has baby duty Wednesday night"
				world
				KN_MOD "ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"
				KN_MOD "modded by modder2 %s program. Created by BulliThulli"
				python:
				    renpy.input("Ignore this box. It is just added by me to verify if you reached the end of the game", length=32)
				""", version, version);
		Modder2 modder2 = new Modder2();
		String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("dir4/g.rpy")).getPath();
		modder2.verifyAndExecuteKNModFeature(absolutePath, null, null);
		String content = new String(Files.readAllBytes(Paths.get("/tmp/out")));
		assertEquals(solution, content);
	}

	@Test
	public void test6() throws Exception {
		String solution = String.format("""
				define KN_MOD = Character("KN_MOD", color="#ff0000")
				# java -jar modder-2.jar null
				# ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
				# modded by modder2%s program. Created by BulliThulli
				KN_MOD "label z:"
				KN_MOD "menu:"
				KN_MOD "What about the fraternities?:"
				KN_MOD "if mc.frat == Frat.WOLVES: # -if mc.frat, Frat.WOLVES"
				u "Do you think they're coming for the Wolves too?"
				KN_MOD "else: ### ERROR: else"
				u "Do you think they're reviewing the Apes?"
				KN_MOD "ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"
				KN_MOD "modded by modder2 %s program. Created by BulliThulli"
				python:
				    renpy.input("Ignore this box. It is just added by me to verify if you reached the end of the game", length=32)
				""", version, version);
		Modder2 modder2 = new Modder2();
		String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("knmodtests/b.rpy")).getPath();
		modder2.verifyAndExecuteKNModFeature(absolutePath, null, null);
		String content = new String(Files.readAllBytes(Paths.get("/tmp/out")));
		assertEquals(solution, content);
	}

	@Test
	public void test7() throws Exception {
		String solution = String.format("""
				define KN_MOD = Character("KN_MOD", color="#ff0000")
				# java -jar modder-2.jar null
				# ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
				# modded by modder2%s program. Created by BulliThulli
				KN_MOD "label z:"
				KN_MOD "menu dsa:"
				KN_MOD "Give me that:"
				KN_MOD "menufail_label=ep2s83_quiet: #TIMED CHOICE if time expires, jump to Say nothing"
				KN_MOD "Give me that:"
				KN_MOD "ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"
				KN_MOD "modded by modder2 %s program. Created by BulliThulli"
				python:
				    renpy.input("Ignore this box. It is just added by me to verify if you reached the end of the game", length=32)
				""", version, version);
		Modder2 modder2 = new Modder2();
		String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("knmodtests/c.rpy")).getPath();
		modder2.verifyAndExecuteKNModFeature(absolutePath, null, null);
		String content = new String(Files.readAllBytes(Paths.get("/tmp/out")));
		assertEquals(solution, content);
	}

	//@Test
	public void test5() throws Exception {
		// TODO: 1/12/24 no idea why i commented the test.
		String solution = String.format("""
				define KN_MOD = Character("KN_MOD", color="#ff0000")
				# java -jar modder-2.jar null
				# ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
				# modded by modder2%s program. Created by BulliThulli
				KN_MOD "if v3s27_mc_baby_schedulewednesday == BabyDuty.ALONE or v3s27_mc_baby_schedulewednesday == BabyDuty.WITH_PARTNER: # IF MC has baby duties tonight, MC has baby duty Wednesday night"
				hello #somecomment
				KN_MOD "else if v3s27_mc_baby_schedulewednesday == BabyDuty.ALONE or v3s27_mc_baby_schedulewednesday == BabyDuty.WITH_PARTNER: # IF MC has baby duties tonight, MC has baby duty Wednesday night"
				world
				KN_MOD "ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"
				KN_MOD "modded by modder2 %s program. Created by BulliThulli"
				""", version, version);
		Modder2 modder2 = new Modder2();
		String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("knmodtests/a.rpy")).getPath();
		modder2.verifyAndExecuteKNModFeature(absolutePath, null, null);
		String content = new String(Files.readAllBytes(Paths.get("/tmp/out")));
		assertEquals(solution, content);
	}

	@Test
	public void test8() throws Exception {
		String solution = String.format("""
				define KN_MOD = Character("KN_MOD", color="#ff0000")
				# java -jar modder-2.jar null
				# ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
				# modded by modder2%s program. Created by BulliThulli
				KN_MOD "return"
				KN_MOD "return#dsaasd"
				KN_MOD "ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"
				KN_MOD "modded by modder2 %s program. Created by BulliThulli"
				python:
				    renpy.input("Ignore this box. It is just added by me to verify if you reached the end of the game", length=32)
				""", version, version);
		Modder2 modder2 = new Modder2();
		String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("knmodtests/returntest.rpy")).getPath();
		modder2.verifyAndExecuteKNModFeature(absolutePath, null, null);
		String content = new String(Files.readAllBytes(Paths.get("/tmp/out")));
		assertEquals(solution, content);
	}

	@Test
	public void test9() throws Exception {
		String solution = String.format("""
				define KN_MOD = Character("KN_MOD", color="#ff0000")
				# java -jar modder-2.jar null
				# ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
				# modded by modder2%s program. Created by BulliThulli
				image demons_far:
				    function anim_reset
				    time 0.01
				    parallel:
				        "images/scenes/001_demons/far/001-demons-far[frame_pad].webp"
				        function next_frame
				        function play_demons_far
				        pause frame_rate
				        repeat
				    parallel:
				        alpha 0.0
				        pause 5.0
				        linear 5.0 alpha 1.0
				KN_MOD "ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"
				KN_MOD "modded by modder2 %s program. Created by BulliThulli"
				python:
				    renpy.input("Ignore this box. It is just added by me to verify if you reached the end of the game", length=32)
				""", version, version);
		Modder2 modder2 = new Modder2();
		String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("knmodtests/skipImageTest.rpy")).getPath();
		modder2.verifyAndExecuteKNModFeature(absolutePath, null, null);
		String content = new String(Files.readAllBytes(Paths.get("/tmp/out")));
		assertEquals(solution, content);
	}

	@Test
	public void test10() throws Exception {
		String solution = String.format("""
				define KN_MOD = Character("KN_MOD", color="#ff0000")
				# java -jar modder-2.jar null
				# ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
				# modded by modder2%s program. Created by BulliThulli
				image banish_channel:
				    parallel:
				        "images/scenes/003_summoning_wood/fx/scn_003_fx_beam[banish_frame_pad].webp"
				        function play_banish_channel
				        pause frame_rate
				        repeat
				    parallel:
				        easeout 1.0 alpha .75
				        easein 1.0 alpha 1.0
				        repeat
				KN_MOD "label scene_003_summoning_wood:"
				play sound "audio/sfx/ambience/sfx_ambience_forest.ogg" loop fadein 1.0 volume 0.125
				KN_MOD "show screen text_onlyEarth, Present Day as intro_text with Dissolve3.0"
				with Pause(1.0)
				anwar "hehllo"
				KN_MOD "ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"
				KN_MOD "modded by modder2 %s program. Created by BulliThulli"
				python:
				    renpy.input("Ignore this box. It is just added by me to verify if you reached the end of the game", length=32)
				""", version, version);
		Modder2 modder2 = new Modder2();
		String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("knmodtests/skipSectionAndLabel.rpy")).getPath();
		modder2.verifyAndExecuteKNModFeature(absolutePath, null, null);
		String content = new String(Files.readAllBytes(Paths.get("/tmp/out")));
		assertEquals(solution, content);
	}

	@Test
	public void silenceKNMOD_forTest() throws Exception {
		String solution = String.format("""
				define KN_MOD = Character("KN_MOD", color="#ff0000")
				# java -jar modder-2.jar null
				# ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
				# modded by modder2%s program. Created by BulliThulli
				KN_MOD "label later02:"
				scene b028 with dissolve
				call phone_start from _call_phone_start
				call message_start("SABRINA", "hey") from _call_message_start
				call message("SABRINA", "Why didnt you text me when you got to the hotel?") from _call_message
				call reply_message("Didnt know I was supposed to.") from _call_reply_message
				call reply_message("Bye") from _call_reply_message
				anwar "hello"
				KN_MOD "ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"
				KN_MOD "modded by modder2 %s program. Created by BulliThulli"
				python:
				    renpy.input("Ignore this box. It is just added by me to verify if you reached the end of the game", length=32)
				""", version, version);
		Modder2 modder2 = new Modder2();
		modder2.KNmod.forceDontKNModForStartsWith.add("call");

		String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("knmodtests/ingoreKNtest.rpy")).getPath();
		modder2.verifyAndExecuteKNModFeature(absolutePath, null, null);
		String content = new String(Files.readAllBytes(Paths.get("/tmp/out")));
		assertEquals(solution, content);
	}

	@Test
	public void test11() throws Exception {
		String solution = String.format("""
				define KN_MOD = Character("KN_MOD", color="#ff0000")
				# java -jar modder-2.jar null
				# ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
				# modded by modder2%s program. Created by BulliThulli
				scene sc13-nighrestlore-10:
					ypos 0
					xpos 0
					linear 40 xpos -640
					repeat
				show sc13-nighrestlore-10_5:
					linear 1 ypos -5
					linear 1 ypos 5
					repeat
				KN_MOD "label car:"
				hello anwar
				hello kai
				scene sc13-nighrestlore-10:
					ypos 0
					xpos 0
					linear 40 xpos -640
					repeat
				show sc13-nighrestlore-10_5:
					linear 1 ypos -5
					linear 1 ypos 5
					repeat
				bye anwar
				bye all
				KN_MOD "ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"
				KN_MOD "modded by modder2 %s program. Created by BulliThulli"
				python:
				\trenpy.input("Ignore this box. It is just added by me to verify if you reached the end of the game", length=32)
				""", version, version);
		Modder2 modder2 = new Modder2();
		String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("dir4/a.rpy")).getPath();
		modder2.verifyAndExecuteKNModFeature(absolutePath, null, null);
		String content = new String(Files.readAllBytes(Paths.get("/tmp/out"))).replaceAll("    ", "\t");
		assertEquals(solution, content);
	}

	@Test
	public void test12() throws Exception {
		String solution = String.format("""
				define KN_MOD = Character("KN_MOD", color="#ff0000")
				# java -jar modder-2.jar null
				# ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
				# modded by modder2%s program. Created by BulliThulli
				KN_MOD "label zoom:"
				tina "Wha~"
				scene sc07-home-15 with dissolve
				heather "Right on time! The breakfast is almost ready, take a seat."
				scene sc07-home-15_5 with dissolve:
					xalign 0.22
					zoom 0.85
					pause 30
				y "{i}{color=#b4a999}Heather, the kindest person I've ever known in my life.{/i}{/color}"
				y "{i}{color=#b4a999}She's not my real mother. She found me when I was 6 years old alone in the middle of nowhere without parents, home or anything.{/i}{/color}"
				KN_MOD "ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"
				KN_MOD "modded by modder2 %s program. Created by BulliThulli"
				python:
				\trenpy.input("Ignore this box. It is just added by me to verify if you reached the end of the game", length=32)
				""", version, version);
		Modder2 modder2 = new Modder2();
		String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("dir4/b.rpy")).getPath();
		modder2.verifyAndExecuteKNModFeature(absolutePath, null, null);
		String content = new String(Files.readAllBytes(Paths.get("/tmp/out"))).replaceAll("    ", "\t");
		assertEquals(solution, content);
	}

	@Test
	public void test13() throws Exception {
		String solution = String.format("""
				define KN_MOD = Character("KN_MOD", color="#ff0000")
				# java -jar modder-2.jar null
				# ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
				# modded by modder2%s program. Created by BulliThulli
				KN_MOD "show screen asd"
				show block:
					x = 1
					y = 2
				show image
				KN_MOD "show screen asd"
				show image
				KN_MOD "show screen asd"
				KN_MOD "show screen asd"
				hello
				KN_MOD "ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"
				KN_MOD "modded by modder2 %s program. Created by BulliThulli"
				python:
				\trenpy.input("Ignore this box. It is just added by me to verify if you reached the end of the game", length=32)
				""", version, version);
		Modder2 modder2 = new Modder2();
		String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("dir4/c.rpy")).getPath();
		modder2.verifyAndExecuteKNModFeature(absolutePath, null, null);
		String content = new String(Files.readAllBytes(Paths.get("/tmp/out"))).replaceAll("    ", "\t");
		assertEquals(solution, content);
	}

	@Test
	public void test14() throws Exception {
		String solution = String.format("""
				define KN_MOD = Character("KN_MOD", color="#ff0000")
				# java -jar modder-2.jar null
				# ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/
				# modded by modder2%s program. Created by BulliThulli
				KN_MOD "label end_sandbox:"
				KN_MOD "if sb_choice:"
				KN_MOD "if time.is_night:"
				$ sb_night = True
				KN_MOD "else:"
				$ increase_time()
				KN_MOD "call play_nav_music music_name=sb_end_paramsmusic_name"
				KN_MOD "if sb_choice != exit:"
				$ seen_sb.add(sb_choice)
				$ check_leisure_time()
				$ check_yoga_master()
				$ check_massageur()
				KN_MOD "if sb_end_paramsap_granted and"
				KN_MOD "sb_end_paramsap_granted > 0 and sb_eventssb_choice.split.0.can_grant_apsb_choice"
				KN_MOD "or"
				KN_MOD "sb_end_paramsap_granted < 0:"
				window hide
				$ sb_events[sb_choice.split(".")[0]].npc.add_stat("ap", sb_end_params["ap_granted"])
				scene black with fade
				KN_MOD "if sb_end_paramsmap:"
				$ go_to(L_map_main, previous=False)
				KN_MOD "elif sb_end_paramsgoto_loc is None:"
				KN_MOD "jump expression current_room"
				KN_MOD "else:"
				$ go_to(sb_end_params["goto_loc"])
				KN_MOD "else:"
				KN_MOD "call play_nav_music music_name=sb_end_paramsmusic_name"
				scene black with fade
				KN_MOD "if sb_end_paramsmap:"
				$ go_to(L_map_main, previous=False)
				KN_MOD "elif sb_end_paramsgoto_loc is None:"
				KN_MOD "jump expression current_room"
				KN_MOD "else:"
				$ go_to(sb_end_params["goto_loc"])
				KN_MOD "ModWork created and maintained at https://f95zone.to/threads/renpy-visualnovel-to-kinetic-novel-convertor.172769/"
				KN_MOD "modded by modder2 %s program. Created by BulliThulli"
				python:
				\trenpy.input("Ignore this box. It is just added by me to verify if you reached the end of the game", length=32)
				""", version, version);
		Modder2 modder2 = new Modder2();
		String absolutePath = Objects.requireNonNull(getClass().getClassLoader().getResource("dir4/h.rpy")).getPath();
		modder2.verifyAndExecuteKNModFeature(absolutePath, null, null);
		String content = new String(Files.readAllBytes(Paths.get("/tmp/out"))).replaceAll("    ", "\t");
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
