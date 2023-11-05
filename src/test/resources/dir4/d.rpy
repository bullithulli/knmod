python:
        kstm = renpy.input("What's your relationship with her? (If you leave it blank, she'll be your stepmom.)")
        kstm = kstm.strip()

        if not kstm:
            kstm = "Stepmom"

        stepson = renpy.input("What's her relationship with you? (If you leave it blank, you'll be her stepson.)")
        stepson = stepson.strip()
hello
World