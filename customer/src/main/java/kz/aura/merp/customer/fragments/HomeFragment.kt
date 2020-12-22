package kz.aura.merp.customer.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.customer.activities.MainActivity
import kz.aura.merp.customer.models.HomeProduct
import kz.aura.merp.customer.models.Slider
import kz.aura.merp.customer.presenters.HomePresenter
import kz.aura.merp.customer.presenters.IHomePresenter
import kz.aura.merp.customer.utils.Constants
import kz.aura.merp.customer.views.IHomeView
import com.example.aura.R
import kz.aura.merp.customer.adapterItems.HomeProductItem
import kz.aura.merp.customer.adapterItems.SliderItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), IHomeView {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var homePresenter: IHomePresenter
    private lateinit var messageText: TextView

    private val image = "https://i.ytimg.com/vi/0t9shY1PpRs/maxresdefault.jpg"
    private val sliderAdapter = GroupAdapter<GroupieViewHolder>()
        .apply {
            add(SliderItem(Slider(image, "Акция")))
            add(SliderItem(Slider(image, "Бонус")))
            add(SliderItem(Slider(image, "Акция")))
            add(SliderItem(Slider(image, "Бонус")))
            add(SliderItem(Slider(image, "Акция")))
        }
    private val homeProductAdapter = GroupAdapter<GroupieViewHolder>()
        .apply {
            add(HomeProductItem(HomeProduct(image, "Roboclean S-Plus")))
            add(HomeProductItem(HomeProduct(image, "Roboclean S-Plus")))
            add(HomeProductItem(HomeProduct(image, "Roboclean S-Plus")))
            add(HomeProductItem(HomeProduct(image, "Roboclean S-Plus")))
            add(HomeProductItem(HomeProduct(image, "Roboclean S-Plus")))
            add(HomeProductItem(HomeProduct(image, "Roboclean S-Plus")))
            add(HomeProductItem(HomeProduct(image, "Roboclean S-Plus")))
            add(HomeProductItem(HomeProduct(image, "Roboclean S-Plus")))
            add(HomeProductItem(HomeProduct(image, "Roboclean S-Plus")))
            add(HomeProductItem(HomeProduct(image, "Roboclean S-Plus")))
            add(HomeProductItem(HomeProduct(image, "Roboclean S-Plus")))
            add(HomeProductItem(HomeProduct(image, "Roboclean S-Plus")))

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as MainActivity).supportActionBar?.title = "Aura"
        fragment_home_slider_recyclerview.layoutManager = LinearLayoutManager(
            this.context,
            RecyclerView.HORIZONTAL,
            false
        )
        fragment_home_slider_recyclerview.adapter = sliderAdapter
        fragment_home_grid_recyclerview.layoutManager = GridLayoutManager(
            this.context,
            2,
            GridLayoutManager.VERTICAL,
            false
        )
        fragment_home_grid_recyclerview.adapter = homeProductAdapter

        onClickListener(MessagingTestFragment(), products_btn, R.id.nav_products)
        onClickListener(FeedbackFragment(), comments_btn, R.id.nav_comments)
        onClickListener(PaymentsFragment(), payment_btn, R.id.nav_payments)
        onClickListener(MessageFragment(), messages_btn, R.id.nav_message)
        onClickListener(BonusesFragment(), bonuses_btn, R.id.nav_bonuses)
        onClickListener(SettingsFragment(), settings_btn, R.id.nav_settings)
//        onClickListener(DefrayalFragment(), defrayal_btn, R.id.nav_defrayal)
        onClickListener(ServicesFragment(), service_btn, R.id.nav_services)

        homePresenter = HomePresenter(this, this.requireContext())
        homePresenter.getUnreadMessagesCount(Constants.CUSTOMER_ID)

        messageText = MenuItemCompat.getActionView(
            (activity as MainActivity).navigation_view.menu.findItem(R.id.nav_message)
        ) as TextView
    }

    private fun initializeCountDrawer(count: Int) {
        messageText.gravity = Gravity.CENTER_VERTICAL
        messageText.setTypeface(null, Typeface.BOLD)
        messageText.setTextColor(resources.getColor(R.color.colorAccent))
        messageText.text = "$count"
    }

    private fun onClickListener(fragment: Fragment, btn: ImageButton, id: Int) {
        btn.setOnClickListener {
            // set background color for drawer item ]<-
            (activity as MainActivity).navigation_view.setCheckedItem(id)

            parentFragmentManager
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onSuccessUnreadMessagesCount(count: Int) {
        if (count != 0) {
            unread_messages_count.text = "$count"
            initializeCountDrawer(count)
        } else {
            unread_messages_count.setBackgroundResource(0)
        }
    }
}