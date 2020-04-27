package org.shadowrunrussia2020.android.magic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_spell_details.*
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.character.CharacterViewModel
import org.shadowrunrussia2020.android.common.models.Character
import org.shadowrunrussia2020.android.common.models.Spell

class SpellDetailsFragment : Fragment() {
    private val args: SpellDetailsFragmentArgs by navArgs()
    private val spell: Spell by lazy { args.spell }
    private val mCharacterModel by lazy {
        ViewModelProviders.of(requireActivity()).get(CharacterViewModel::class.java)
    }

    private val mModel by lazy {
        ViewModelProviders.of(requireActivity()).get(SpellDetailsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_spell_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textAbilityName.text = spell.humanReadableName
        textAbilityDescription.text = spell.description

        updateEnableness(true)

        castSpell.setOnClickListener {
            findNavController().navigate(
                SpellDetailsFragmentDirections.actionStartCast(spell, mModel.power)
            )
        }

        seekBarSpellPower.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateEnableness(progress > 0)
                mModel.power = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        mCharacterModel.getCharacter()
            .observe(this, Observer { data: Character? ->
                if (data != null) {
                    seekBarSpellPower.max = data.magic
                }
            })
    }

    private fun updateEnableness(enable: Boolean) {
        castSpell.isEnabled = enable
    }
}
