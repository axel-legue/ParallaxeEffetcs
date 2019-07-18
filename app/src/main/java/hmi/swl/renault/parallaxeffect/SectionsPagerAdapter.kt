package hmi.swl.renault.parallaxeffect

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class SectionsPagerAdapter(
    private val activity: FragmentActivity
) : FragmentStateAdapter(activity) {


    companion object {
        private val TAB_TITLES = arrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }

    override fun createFragment(position: Int): Fragment = ParallaxFragment.newInstance()

    fun getPageTitle(position: Int): CharSequence = activity.resources.getString(TAB_TITLES[position])

    override fun getItemCount(): Int = TAB_TITLES.size
}