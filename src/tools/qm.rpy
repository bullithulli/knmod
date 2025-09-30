
define persistent.dialogueBoxOpacity = 1.0


init python:

    style.bullithullistyle = Style(style.button_text)



    style.bullithullistyle.color = "#FF0000"
    style.bullithullistyle.hover_color = "#0000FF"
    style.bullithullistyle.selected_color = "#00FF00"
    def toggle_text_color():
        persistent.what_text_color = "#FFFFFF" if persistent.what_text_color == "#000000" else "#000000"

screen quick_menu():
    $ quick_menu = True
    layer "overlay"
    zorder 200
    if quick_menu:

        hbox:
            style_group "quick"

            xalign 0.5
            yalign 1.0

            textbutton _("Back") action Rollback() text_style "bullithullistyle" text_size 30
            textbutton _("Save") action ShowMenu('save') text_style "bullithullistyle" text_size 30
            textbutton _("Q.Save") action QuickSave() text_style "bullithullistyle" text_size 30
            textbutton _("Q.Load") action QuickLoad() text_style "bullithullistyle" text_size 30
            textbutton _("Skip") action Skip() text_style "bullithullistyle" text_size 30
            textbutton _("F.Skip") action Skip(fast=True, confirm=True) text_style "bullithullistyle" text_size 30
            textbutton _("Auto") action Preference("auto-forward", "toggle") text_style "bullithullistyle" text_size 30
            textbutton _("Prefs") action ShowMenu('preferences') text_style "bullithullistyle" text_size 30
            textbutton _("console") action Call("_console") text_style "bullithullistyle" text_size 30
            textbutton _("Hide") action HideInterface() text_style "bullithullistyle" text_size 30

            textbutton _("Toggle Color") action ToggleVariable("persistent.what_text_color", "#000000", "#FFFFFF") text_style "bullithullistyle" text_size 30

init -1 screen quick_menu():
    $ quick_menu = True
    variant "touch"

    zorder 100

    if quick_menu:

        hbox:
            style_prefix "quick"

            xalign 0.5
            yalign 1.0

            textbutton _("Back") action Rollback() text_style "bullithullistyle" text_size 30
            textbutton _("Save") action ShowMenu('save') text_style "bullithullistyle" text_size 30
            textbutton _("Skip") action Skip() text_style "bullithullistyle" text_size 30
            textbutton _("F.Skip") action Skip(fast=True, confirm=True) text_style "bullithullistyle" text_size 30
            textbutton _("Auto") action Preference("auto-forward", "toggle") text_style "bullithullistyle" text_size 30
            textbutton _("Prefs") action ShowMenu('preferences') text_style "bullithullistyle" text_size 30
            textbutton _("console") action Call("_console") text_style "bullithullistyle" text_size 30
            textbutton _("Hide") action HideInterface() text_style "bullithullistyle" text_size 30
            bar value VariableValue("persistent.dialogueBoxOpacity", range=1.0) style "slider" xsize 100
            textbutton _("Toggle Color") action ToggleVariable("persistent.what_text_color", "#000000", "#FFFFFF") text_style "bullithullistyle" text_size 30
# Decompiled by unrpyc: https://github.com/CensoredUsername/unrpyc
