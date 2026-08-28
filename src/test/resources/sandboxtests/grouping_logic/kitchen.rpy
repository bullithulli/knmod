label kitchen_corrupt:
    if corruption >= 10:
        "You feel evil in the kitchen."
        $ corruption += 2
        return

label kitchen_normal:
    "Nothing special here."
    menu:
        "Cook meal" if week < 3:
            "You cook."
        "Clean" if corruption < 5:
            "You clean."
    return
