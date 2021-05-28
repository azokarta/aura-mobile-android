package kz.aura.merp.employee.util

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter (private val bundle: Bundle? = null, fa: FragmentActivity, private val fragments: ArrayList<Fragment>) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        bundle.let {
            fragments[position].arguments = bundle
        }
        return fragments[position]
    }
}