label end_sandbox:
    if sb_choice:

        if time.is_night():

            $ sb_night = True
        else:


            $ increase_time()

        call play_nav_music (music_name=sb_end_params["music_name"])

        if sb_choice != "exit":

            $ seen_sb.add(sb_choice)



            $ check_leisure_time()

            $ check_yoga_master()
            $ check_massageur()





            if (sb_end_params["ap_granted"] and
                ((sb_end_params["ap_granted"] > 0 and sb_events[sb_choice.split(".")[0]].can_grant_ap(sb_choice))
                 or
                 sb_end_params["ap_granted"] < 0)):
                window hide
                $ sb_events[sb_choice.split(".")[0]].npc.add_stat("ap", sb_end_params["ap_granted"])

        scene black with fade


        if sb_end_params["map"]:
            $ go_to(L_map_main, previous=False)
        elif sb_end_params["goto_loc"] is None:
            jump expression current_room
        else:
            $ go_to(sb_end_params["goto_loc"])
    else:


        call play_nav_music (music_name=sb_end_params["music_name"])
        scene black with fade


        if sb_end_params["map"]:
            $ go_to(L_map_main, previous=False)
        elif sb_end_params["goto_loc"] is None:
            jump expression current_room
        else:
            $ go_to(sb_end_params["goto_loc"])