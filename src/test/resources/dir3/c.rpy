label test1:
    call screen hallway_nav
screen hallway_nav():
    imagebutton auto "door_livingroom_%s":
        action Jump ("livingroom")
screen hallway_nav2():
        action Jump ("bathroom")
label livingroom:
    livingroom
    return
label bathroom:
    bathroom
label car:
    car