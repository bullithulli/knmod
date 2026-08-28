label bedroom:
    scene bedroom_bg
    "You enter the bedroom."
    menu:
        "Check the drawer":
            "You find a mysterious letter."
            jump read_letter
        "Look under the bed":
            "You find dust bunnies. Nothing else."
        "Go back":
            jump hub

label read_letter:
    "The letter reads: Meet me at the park tonight."
    "This must be important."
    jump hub
