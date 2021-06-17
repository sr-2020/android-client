package org.shadowrunrussia2020.android.billing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.fragment_billing.*
import org.shadowrunrussia2020.android.MainActivity
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.character.CharacterViewModel


class BillingFragment : Fragment() {

    private lateinit var mModel: BillingViewModel
    private val characterViewModel by lazy {
        ViewModelProviders.of(this).get(CharacterViewModel::class.java)
    }
    private var scoringScreenVisible = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_billing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mModel = ViewModelProviders.of(activity!!).get(BillingViewModel::class.java)

        tabLayout.setupWithViewPager(viewPager)

        viewPager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getCount(): Int = if (scoringScreenVisible) 5 else 4
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> BillingOverviewFragment()
                    1 -> BillingHistoryFragment()
                    2 -> BillingRentsFragment()
                    3 -> BillingPassportFragment()
                    4 -> BillingScoringFragment()
                    else -> throw Error();
                }
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return when (position) {
                    0 -> "Обзор"
                    1 -> "История"
                    2 -> "Ренты"
                    3 -> "Паспорт"
                    4 -> "Скоринг"
                    else -> throw Error();
                }
            }
        }

        characterViewModel.getCharacter().observe(this, Observer {
            scoringScreenVisible = it.screens.scoring
            (viewPager.adapter as FragmentPagerAdapter).notifyDataSetChanged()
        })

        // Hack to un-break SwipeRefreshLayout and ViewPager interaction. Without that SwipeRefreshLayout
        // will trigger even with tiny bit of vertical movement during horizontal swipe.
        // Based on https://stackoverflow.com/a/35825488/11167405
        viewPager.addOnPageChangeListener(object: ViewPager.SimpleOnPageChangeListener() {
            override fun onPageScrollStateChanged(state: Int) {
                val activity = activity as MainActivity?
                activity?.setPullToRefreshEnabled(state == ViewPager.SCROLL_STATE_IDLE)
            }
        })
    }
}
