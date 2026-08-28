label kitchen:
    scene kitchen_bg
    "You enter the kitchen."
    if character_alive:
        "Alice is cooking breakfast."
        alice "Good morning! Want some eggs?"
        menu:
            "Yes please":
                alice "Here you go!"
                "You eat the eggs."
            "No thanks":
                alice "Suit yourself."
    else:
        "The kitchen is empty. Alice is gone."
        "You feel a pang of sadness."
    call make_coffee
    jump hub

label make_coffee:
    "You make yourself a cup of coffee."
    "The aroma fills the room."
    return
