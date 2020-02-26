package org.shadowrunrussia2020.android.magic.cast


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import org.shadowrunrussia2020.android.common.di.MainActivityScope
import org.shadowrunrussia2020.android.magic.R

class SpellCastFragment : Fragment() {
//    private val args: SpellDetailsFragmentArgs by navArgs()
//    private val spell: Spell by lazy { args.spell }
//    private val mCharacterModel by lazy {
//        ViewModelProviders.of(requireActivity()).get(CharacterViewModel::class.java)
//    }

    companion object{
        private const val EXTRA_SPELL_ID = "org.shadowrunrussia2020.android.magic.cast.spell"
        fun createBundle(spellId:String) = Bundle().apply {
            putString(EXTRA_SPELL_ID, spellId)
        }
    }
    private val router by lazy { (activity as MainActivityScope).router }
    private val spellId by lazy { arguments?.getString(EXTRA_SPELL_ID) ?: throw IllegalStateException("no spellId") }



    private val viewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(SpellCastViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_spell_cast, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}
