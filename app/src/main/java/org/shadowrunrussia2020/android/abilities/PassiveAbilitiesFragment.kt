
package org.shadowrunrussia2020.android.abilities


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_passive_abilities.*
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.character.CharacterViewModel
import org.shadowrunrussia2020.android.common.models.Character
import org.shadowrunrussia2020.android.common.models.PassiveAbility
import org.shadowrunrussia2020.android.view.universal_list.PassiveAbilityListItem
import org.shadowrunrussia2020.android.view.universal_list.UniversalAdapter

class PassiveAbilitiesFragment : Fragment() {
    private val mModel by lazy {
        ViewModelProviders.of(activity!!).get(CharacterViewModel::class.java)
    }
    private val universalAdapter by lazy { UniversalAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_passive_abilities, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mModel.getCharacter().observe(this,
            Observer { character: Character? ->
                if (character != null) {
                    universalAdapter.clear()

                    val transform: (PassiveAbility) -> PassiveAbilityListItem = {
                        PassiveAbilityListItem(it) {
                            findNavController().navigate(
                                PassiveAbilitiesFragmentDirections.actionSelectPassiveAbility(it)
                            )
                        }
                    }

                    universalAdapter.appendList(
                        character.passiveAbilities.filter { it -> it.id.startsWith("meta-") }
                            .map(transform)
                    )

                    universalAdapter.appendList(
                        character.passiveAbilities.filter { it -> it.id.startsWith("arch-") }
                            .map(transform)
                    )

                    universalAdapter.appendList(
                        character.passiveAbilities.filter { it -> !it.id.startsWith("meta-") && !it.id.startsWith("arch-") }
                            .map(transform)
                    )

                    universalAdapter.apply()
                }
            })

        availableAbilitiesView.adapter = universalAdapter
        availableAbilitiesView.layoutManager = LinearLayoutManager(requireContext())
    }
}
