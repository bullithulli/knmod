label park:
    if day == 1 and time_of_day == "afternoon":
        jump ev_park_day1_afternoon
    else:
        "The park is empty."
        jump hub

label ev_park_day1_afternoon:
    "A nice walk in the park."
    jump advance_time
