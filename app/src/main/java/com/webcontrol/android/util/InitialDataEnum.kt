package com.webcontrol.android.util

import androidx.fragment.app.Fragment
import com.webcontrol.android.R
import com.webcontrol.android.ui.covid.initialdata.*

enum class InitialDataEnum(val fragment: Fragment) {
        PERSONAL(PersonalesFragment()),
        CONTACT(ContactoFragment()),
        RESIDENCE(ResidenciaFragment()),
        LABORAL(LaboralFragment()),
        HEALTHHISTORY(AntecedentesFragment())
}