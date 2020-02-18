package org.shadowrunrussia2020.android.abilities


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_active_ability_details.*
import kotlinx.android.synthetic.main.fragment_spell_details.textAbilityDescription
import kotlinx.android.synthetic.main.fragment_spell_details.textAbilityName
import org.ocpsoft.prettytime.PrettyTime
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.common.models.PassiveAbility
import java.util.*

class PassiveAbilityDetailsFragment : Fragment() {
    private val args: PassiveAbilityDetailsFragmentArgs by navArgs()
    private val ability: PassiveAbility by lazy { args.ability }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_passive_ability_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textAbilityName.text = ability.name
        textAbilityDescription.text = ability.description
        val validUntil = ability.validUntil
        if (validUntil != null) {
            textValidUntil.text = "Закончится " + PrettyTime(Locale("ru")).format(Date(validUntil))
        }
    }
}
