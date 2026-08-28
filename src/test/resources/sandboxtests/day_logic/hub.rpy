label hub:
    menu:
        "Go to Bedroom":
            $ current_room = "bedroom"
            jump bedroom
        "Go to Park":
            $ current_room = "park"
            jump park
        "Wait":
            jump advance_time
