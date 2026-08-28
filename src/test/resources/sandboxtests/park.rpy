label park:
    scene park_bg
    "You arrive at the park."
    call screen park_navigation

screen park_navigation():
    textbutton "Visit the fountain" action Jump("fountain")
    textbutton "Sit on the bench" action Jump("bench")
    textbutton "Go home" action Jump("hub")

label fountain:
    "You walk to the fountain."
    "The water sparkles in the sunlight."
    if has_coin:
        "You toss a coin and make a wish."
    else:
        "You have no coins to toss."
    jump hub

label bench:
    "You sit on the bench and relax."
    while True:
        menu:
            "Keep sitting":
                "You enjoy the peaceful moment."
            "Get up":
                jump hub
