label a1:
    a1
    call one
    call two
    call ten
    return
label a2:
    a2
    call a1
    call a3
    return
label a3:
    a3
    return
label a4:
    a4
    jump a1
    jump a3
    return