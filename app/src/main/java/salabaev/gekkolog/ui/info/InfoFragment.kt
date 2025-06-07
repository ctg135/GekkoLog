package salabaev.gekkolog.ui.info

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannedString
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import salabaev.gekkolog.R
import salabaev.gekkolog.databinding.FragmentInfoBinding

class InfoFragment : Fragment() {

    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = InfoFragment()
    }

    private val viewModel: InfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Раздел условия содержания
        binding.headingTerrarium.underlineText(3)
        binding.headingTerrarium.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_info_to_terrariumFragment)
        }
        binding.headingTools.underlineText(3)
        binding.headingTools.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_info_to_toolsFragment)
        }
        binding.headingTemp.underlineText(3)
        binding.headingTemp.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_info_to_tempertatureFragment)
        }
        // Раздел кормления
        binding.headingFeedObjects.underlineText(3)
        binding.headingFeedObjects.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_info_to_foodFragment)
        }
        binding.headingFeedTime.underlineText(3)
        binding.headingFeedTime.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_info_to_timeFragment)
        }
        binding.headingFeedAdditions.underlineText(3)
        binding.headingFeedAdditions.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_info_to_additionsFragment)
        }
        binding.headingFeedCancel.underlineText(3)
        binding.headingFeedCancel.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_info_to_cancelFragment)
        }
        // Раздел о здоровье
        binding.headingHealthTools.underlineText(3)
        binding.headingHealthTools.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_info_to_healthToolsFragment)
        }
        binding.headingHealthShed.underlineText(3)
        binding.headingHealthShed.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_info_to_shedFragment)
        }
        binding.headingHealthWeight.underlineText(3)
        binding.headingHealthWeight.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_info_to_weightFragment)
        }
        binding.headingHealthAdaptation.underlineText(3)
        binding.headingHealthAdaptation.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_info_to_adaptationFragment)
        }
    }

    fun TextView.underlineText(start: Int = 0, end: Int = this.text.length) {
        this.text = SpannableString(this.text).apply {
            setSpan(
                UnderlineSpan(),
                start,
                end,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
    }

}