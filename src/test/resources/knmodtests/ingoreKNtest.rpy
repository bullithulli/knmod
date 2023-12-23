label later02:
    scene b028 with dissolve

    call phone_start from _call_phone_start
    call message_start("SABRINA", "hey") from _call_message_start
    call message("SABRINA", "Why didnt you text me when you got to the hotel?") from _call_message
    call reply_message("Didnt know I was supposed to.") from _call_reply_message
call reply_message("Bye") from _call_reply_message
anwar "hello"
