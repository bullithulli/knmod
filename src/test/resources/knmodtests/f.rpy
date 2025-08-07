label xx:
    scene select (3)
    with dissolve
    menu:
        "She's your non-@unt":
            python:
                arelat = "non-@unt"
        "Other":
            $ persistent.Amrelation = renpy.input("What's she to you?")
            $ persistent.Amrelation = persistent.Amrelation.strip() or "non-@unt"
            #python:
    mc "She is my [arelat]."
    menu:
        "You are her student":
            python:
                arelatmc = "student"
        "Other":
            $ persistent.Amrelationmc = renpy.input("You are her:")
            $ persistent.Amrelationmc = persistent.Amrelationmc.strip() or "student"
    window hide
