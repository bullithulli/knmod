label z:
    menu:
        "What about the fraternities?":
            if mc.frat == Frat.WOLVES: # -if mc.frat, Frat.WOLVES
                u "Do you think they're coming for the Wolves too?"
            else: ### ERROR: else
                u "Do you think they're reviewing the Apes?"
