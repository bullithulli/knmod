label E4_Start: #Figure out which branch we're on

    if mc_cancel_apurna_date == 1 or apurna_love == 0 or apurna_love == 2: #on April's branch
        $E3Branch = "April"
        jump E4_April

    else: #on Apurna's branch
        $E3Branch = "Apurna"
        jump E4_Apurna