label bedroom:
    if day == 1 and time_of_day == "morning":
        jump ev_bedroom_day1_morning
    elif day == 2 and time_of_day == "night":
        jump ev_bedroom_day2_night
    else:
        "Nothing to do here right now."
        jump hub

label ev_bedroom_day1_morning:
    "Waking up on the first day."
    jump advance_time

label ev_bedroom_day2_night:
    "Going to sleep on day 2."
    jump advance_time
