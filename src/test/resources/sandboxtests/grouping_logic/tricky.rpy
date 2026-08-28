label tricky_start:
    $ renpy.call("python_call_target")
    call from_clause_target from _call_something_1
    call screen tricky_screen
    return

label python_call_target:
    "Inside python call"
    return

label from_clause_target:
    "Inside from clause target"
    return

screen tricky_screen():
    textbutton "Click" action Call("screen_call_target")

label screen_call_target:
    "Inside screen call"
    return
