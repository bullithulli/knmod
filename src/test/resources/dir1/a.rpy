label one:
    one
label two:
    two
label three:
    three
    return
label four:
    four
    jump eight
label five:
    five
    return
label six:
    six
label seven:
    seven
label eight:
    call ten
label nine:
    nine
    return
label ten:
    ten
    jump twelve
label eleven:
    jump one
    call eight
label twelve:
    jump one
    return
label thirteen:
    jump two
    return
label fourteen:
    fourteen
jump eight
call nine
return
label fifteen:
    label sixteen:
        sixteen
    fifteen
return
label seventeen:
    seventeen
    if a<10
        jump one
    else
        jump eight
return