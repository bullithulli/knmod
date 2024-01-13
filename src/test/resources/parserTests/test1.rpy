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
