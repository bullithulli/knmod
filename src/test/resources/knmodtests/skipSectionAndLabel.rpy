
image banish_channel:
    parallel:
        "images/scenes/003_summoning_wood/fx/scn_003_fx_beam[banish_frame_pad].webp"
        function play_banish_channel
        pause frame_rate
        repeat
    parallel:
        easeout 1.0 alpha .75
        easein 1.0 alpha 1.0
        repeat


label scene_003_summoning_wood:
    play sound "audio/sfx/ambience/sfx_ambience_forest.ogg" loop fadein 1.0 volume 0.125
    show screen text_only("Earth, Present Day") as intro_text with Dissolve(3.0)
    with Pause(1.0)
    anwar "hehllo"