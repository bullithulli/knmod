image demons_far:
    function anim_reset
    time 0.01
    parallel:
        "images/scenes/001_demons/far/001-demons-far[frame_pad].webp"
        function next_frame
        function play_demons_far
        pause frame_rate
        repeat
    parallel:
        alpha 0.0
        pause 5.0
        linear 5.0 alpha 1.0