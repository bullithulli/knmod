default day = 1
default time_of_day = "morning"
default current_room = "bedroom"

label start:
    jump day_loop

label day_loop:
    if day > 2:
        jump ending
    jump expression current_room

label advance_time:
    if time_of_day == "morning":
        $ time_of_day = "afternoon"
    elif time_of_day == "afternoon":
        $ time_of_day = "evening"
    elif time_of_day == "evening":
        $ time_of_day = "night"
    else:
        $ day += 1
        $ time_of_day = "morning"
    jump day_loop
