label bathroom_week1:
    if week == 1 and time_of_day == "morning":
        "Bathroom morning scene week 1."
        jump bathroom_week1_part2

label bathroom_week1_part2:
    "This is a sequential jump. It must NOT be separated."
    return

label bathroom_week2:
    if week == 2:
        "Bathroom week 2."
        return
