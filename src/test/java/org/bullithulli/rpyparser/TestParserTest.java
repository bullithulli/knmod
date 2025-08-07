package org.bullithulli.rpyparser;

import org.bullithulli.rpyparser.symImpl.blockSymbols.renpyLabel;
import org.bullithulli.rpyparser.symImpl.rootSymbol;
import org.bullithulli.utils.parserUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

public class TestParserTest {
    private static String readFileContents(String fileName) {
        InputStream inputStream = TestParserTest.class.getClassLoader().getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new IllegalArgumentException("File not found: " + fileName);
        }

        try (Scanner scanner = new Scanner(inputStream).useDelimiter("\\A")) {
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    @Test
    public void parseTest1() {
        String rpyCode = """
                label one:
                	label two:
                		label three:
                	label four:
                	label five:
                label six:
                	label seven:""";
        parser renpyParser = new parser();
        assertEquals(rpyCode, renpyParser.parseLine(rpyCode, true).getChainString(0, -1, true, true).trim());
    }

    @Test
    public void parseTest2() {
        String rpyCode = """
                label car:
                			label bike:
                	label z:""";

        String sol = """
                label car:
                	label bike:
                	label z:""";
        parser renpyParser = new parser();
        assertEquals(sol, renpyParser.parseLine(rpyCode, true).getChainString(0, -1, true, true).trim());
    }

    @Test
    public void parseTest4() {
        String rpyCode = """
                label one:
                	label two:
                		label three:
                	label four:
                	label five:
                label six:
                """;
        parser renpyParser = new parser();
        assertEquals(renpyParser.parseLine(rpyCode, true).getHIERARCHY_CHILD_SYMBOL().size(), 2);
    }

    @Test
    public void parseTest5() {
        String rpyCode = """
                screen tabletUI():
                    tag menu
                    add "tabletmenu/tabletbg.png"
                    imagebutton:
                        xalign 0.44
                        yalign 0.2
                        xoffset 0
                        yoffset 0
                        idle "tabletmenu/tbggalleryidle.png"
                        hover "tabletmenu/tbggalleryhover.png"
                        action ShowMenu("gallery")
                
                    imagebutton:
                        xalign 0.54
                        yalign 0.2
                        xoffset 0
                        yoffset 0
                        idle "tabletmenu/tbgstatsidle.png"
                        hover "tabletmenu/tbgstatshover.png"
                        action ShowMenu ("StatsUI")
                anwar "hello"
                    imagebutton: #<---this is wrong, parser should adjust to 0th hierarchy
                        xalign 0.54
                        yalign 0.2
                        xoffset 0
                        yoffset 0
                        idle "tabletmenu/tbgstatsidle.png"
                        hover "tabletmenu/tbgstatshover.png"
                        action ShowMenu ("StatsUI")
                        label z:
                            car
                """;
        String sol = """
                screen tabletUI():
                	tag menu
                	add "tabletmenu/tabletbg.png"
                	imagebutton:
                		xalign 0.44
                		yalign 0.2
                		xoffset 0
                		yoffset 0
                		idle "tabletmenu/tbggalleryidle.png"
                		hover "tabletmenu/tbggalleryhover.png"
                		action ShowMenu("gallery")
                	imagebutton:
                		xalign 0.54
                		yalign 0.2
                		xoffset 0
                		yoffset 0
                		idle "tabletmenu/tbgstatsidle.png"
                		hover "tabletmenu/tbgstatshover.png"
                		action ShowMenu ("StatsUI")
                anwar "hello"
                imagebutton: #<---this is wrong, parser should adjust to 0th hierarchy
                	xalign 0.54
                	yalign 0.2
                	xoffset 0
                	yoffset 0
                	idle "tabletmenu/tbgstatsidle.png"
                	hover "tabletmenu/tbgstatshover.png"
                	action ShowMenu ("StatsUI")
                	label z:
                		car
                """;
        parser renpyParser = new parser();
        assertEquals(sol.trim(), renpyParser.parseLine(rpyCode, false).getChainString(0, -1, true, true).trim());
    }

    @Test
    public void parseTest6() throws IOException {
        String content = readFileContents("parserTests/test1.rpy");
        String sol = """
                screen StatsUI:
                	add "tabletmenu/statsbg.png"
                	frame:
                		xalign 0.533
                		yalign 0.215
                		xpadding 10
                		ypadding 5
                		has hbox:
                			spacing 10
                		vbox:
                			text "[adamlove]" size 35
                	frame:
                		xalign 0.533
                		yalign 0.34
                		xpadding 10
                		ypadding 5
                		has hbox:
                			spacing 10
                		vbox:
                			text "[libbylove]" size 35
                	frame:
                		xalign 0.533
                		yalign 0.47
                		xpadding 10
                		ypadding 5
                		has hbox:
                			spacing 10
                		vbox:
                			text "[exhibitionistpoints]" size 35
                	frame:
                		xalign 0.533
                		yalign 0.60
                		xpadding 10
                		ypadding 5
                		has hbox:
                			spacing 10
                		vbox:
                			text "[corruptionpoints]" size 35
                	frame:
                		xalign 0.533
                		yalign 0.72
                		xpadding 10
                		ypadding 5
                		has hbox:
                			spacing 10
                		vbox:
                			text "[lesbianpoints]" size 35
                	imagebutton:
                		xalign 0.544
                		yalign 0.955
                		xoffset 52
                		yoffset 0
                		idle "tabletmenu/ebi.png"
                		hover "tabletmenu/ebh.png"
                		action Return()
                screen my_exit_screen(yes_action, no_action):
                	add "gui/exit.png"
                	hbox:
                		xalign 0.5
                		yalign 0.5
                		spacing 400
                		imagebutton:
                			idle "gui/exit_button_yes_idle.png"
                			hover "gui/exit_button_yes_hover.png"
                			action yes_action
                		imagebutton:
                			idle "gui/exit_button_no_idle.png"
                			hover "gui/exit_button_no_hover.png"
                			action no_action
                	hbox:
                		xalign 0.5 yalign 0.95
                		spacing 150
                		imagebutton:
                			idle "tabletmenu/tbgpatidle.png"
                			hover "tabletmenu/tbgpathover.png"
                			action OpenURL("https://www.patreon.com/OuterRealm3D")
                screen whositsinfront:
                	add "images/screens/wsif.png"
                	modal True
                	imagebutton:
                		xalign 0.2
                		yalign 0.6
                		xoffset 0
                		yoffset 0
                		idle "images/screens/adaminfrontidle.png"
                		hover "images/screens/adaminfronthover.png"
                		action Jump ("adamsitsinfront")
                	imagebutton:
                		xalign 0.5
                		yalign 0.6
                		xoffset 0
                		yoffset 0
                		idle "images/screens/libbyinfrontidle.png"
                		hover "images/screens/libbyinfronthover.png"
                		action Jump ("libbysitsinfront")
                	imagebutton:
                		xalign 0.8
                		yalign 0.6
                		xoffset 0
                		yoffset 0
                		idle "images/screens/nooneinfrontidle.png"
                		hover "images/screens/nooneinfronthover.png"
                		action Jump ("noonesitsinfront")
                screen swput1:
                	add "images/screens/csoswpu.png"
                	modal True
                	imagebutton:
                		xalign 0.3
                		yalign 0.5
                		xoffset 0
                		yoffset 0
                		idle "images/screens/philchoiceimageidle.png"
                		hover "images/screens/philchoiceimagehover.png"
                		action Jump ("philsdeparture")
                	imagebutton:
                		xalign 0.7
                		yalign 0.5
                		xoffset 0
                		yoffset 0
                		idle "images/screens/susanchoiceimageidle.png"
                		hover "images/screens/susanchoiceimagehover.png"
                		action Jump ("intropart2")
                screen phillormall:
                	add "images/screens/csoswpu.png"
                	modal True
                	imagebutton:
                		xalign 0.3
                		yalign 0.5
                		xoffset 0
                		yoffset 0
                		idle "images/screens/philchoiceimageidle.png"
                		hover "images/screens/philchoiceimagehover.png"
                		action Jump ("PhilArrivesInAlaska")
                	imagebutton:
                		xalign 0.7
                		yalign 0.5
                		xoffset 0
                		yoffset 0
                		idle "images/screens/susanchoiceimageidle.png"
                		hover "images/screens/susanchoiceimagehover.png"
                		action Jump ("mwomall")
                """;
        parser renpyParser = new parser();
        rootSymbol root = (rootSymbol) renpyParser.parseLine(content, false);
        assertEquals(sol.trim(), root.getChainString(0, -1, true, true).trim());
        parserUtils.writeChainString("/tmp/out", root, 0, -1, true, true);
        String out = new String(Files.readAllBytes(Paths.get("/tmp/out")));
        assertEquals(out.trim(), root.getChainString(0, -1, true, true).trim().replaceAll("\t", "    "));
    }

    @Test
    public void test1() {
        String rpyCode = """
                label x:
                    label y:
                        anwar "sda"
                    return
                call y
                jump y
                jump x
                """;
        parser renpyParser = new parser();
        renpyParser.parseLine(rpyCode, false);
        assertEquals(1, renpyParser.pathMatrix.get("x").size());
        assertEquals(2, renpyParser.pathMatrix.get("y").size());
        // TODO: 1/14/24 path matrix update after adding and deleting labels
    }

    @Test
    public void test2() throws Exception {
        String vanilla = """
                label A:
                	label B:
                		anwar "hello, landlord"
                	label C:
                		label D:
                			label E:
                		label F:
                			anwar "Bye, landlord"
                """;
        String ip = """
                label patch1:
                	anwar "Hello, mom"
                label patch2:
                	anwar "bye, mom"
                """;
        parser vanillaRenpyParser = new parser();
        rootSymbol vRoot = (rootSymbol) vanillaRenpyParser.parseLine(vanilla, true);
        parser iParser = new parser();
        rootSymbol iRoot = (rootSymbol) iParser.parseLine(ip, true);
        renpyLabel patch1 = iRoot.getInnerLabelByName("patch1");
        renpyLabel B = vRoot.getInnerLabelByNameSearchRecursivly("B");
        renpyLabel A = (renpyLabel) B.getHIERARCHY_PARENT_SYMBOL();

        parserUtils.deleteInnerLabel(B, vanillaRenpyParser);
        parserUtils.addLabelAfter(A, vanillaRenpyParser, patch1, RENPY_SYMBOL_POSITION.CHAIN_RIGHT, 0);

        assertEquals("""
                label A:
                	label patch1:
                		anwar "Hello, mom"
                	label C:
                		label D:
                			label E:
                		label F:
                			anwar "Bye, landlord"
                """.trim(), vRoot.getChainString(0, -1, true, true).trim());
        assertEquals("""
                label patch2:
                	anwar "bye, mom"
                """.trim(), iRoot.getChainString(0, -1, true, true).trim());

        renpyLabel F = vRoot.getInnerLabelByNameSearchRecursivly("F");
        renpyLabel E = (renpyLabel) F.getCHAIN_PARENT_SYMBOL();
        renpyLabel patch2 = iRoot.getInnerLabelByNameSearchRecursivly("patch2");
        parserUtils.deleteInnerLabel(F, vanillaRenpyParser);
        parserUtils.addLabelAfter(E, vanillaRenpyParser, patch2, RENPY_SYMBOL_POSITION.CHAIN_RIGHT, 0);
        assertEquals("""
                label A:
                	label patch1:
                		anwar "Hello, mom"
                	label C:
                		label D:
                			label E:
                				label patch2:
                					anwar "bye, mom"
                """.trim(), vRoot.getChainString(0, -1, true, true).trim());
        // TODO: 1/14/24 Wrong. need to fix
        assertEquals("", iRoot.getChainString(0, -1, true, true).trim());
    }

    @Test
    public void createTranslationTableTest() throws URISyntaxException, FileNotFoundException {
        URL fileURL = getClass().getClassLoader().getResource("parserTests/translate.rpy");
        assert fileURL != null;
        File file = new File(fileURL.toURI());
        parser renpyparser = new parser();
        HashMap<String, String> dict = renpyparser.createTranslationTable(file);
        assertEquals(dict.size(), 6);
    }

    @Test
    public void parserTest1() {
        String content = readFileContents("dir4/d.rpy");
        parser renpyParser = new parser();
        String solution = """
                python:
                	kstm = renpy.input("What's your relationship with her? (If you leave it blank, she'll be your stepmom.)")
                	kstm = kstm.strip()
                	if not kstm:
                		kstm = "Stepmom"
                	stepson = renpy.input("What's her relationship with you? (If you leave it blank, you'll be her stepson.)")
                	stepson = stepson.strip()
                hello
                World""";
        assertEquals(solution, renpyParser.parseLine(content, false).getChainString(0, -1, true, true).trim());
    }
}
