label start:
    jump main_story

label main_story:
    "This is the massive 4.5 million line story."
    jump ending

label ending:
    "Game over."
    return

label orphan_click:
    "I am a random UI button that was not naturally reached."
    jump main_story
