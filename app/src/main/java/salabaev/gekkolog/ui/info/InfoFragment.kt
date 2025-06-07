package salabaev.gekkolog.ui.info

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        binding.headingTerrarium.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_info_to_terrariumFragment)
        }
        binding.headingTools.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_info_to_toolsFragment)
        }
        binding.headingTemp.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_info_to_tempertatureFragment)
        }
        // Раздел кормления
        binding.headingFeedObjects.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_info_to_foodFragment)
        }
        binding.headingFeedTime.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_info_to_timeFragment)
        }
        binding.headingFeedAdditions.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_info_to_additionsFragment)
        }
        binding.headingFeedCancel.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_info_to_cancelFragment)
        }
        // Раздел о здоровье
        binding.headingHealthTools.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_info_to_healthToolsFragment)
        }
        binding.headingHealthShed.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_info_to_shedFragment)
        }
        binding.headingHealthWeight.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_info_to_weightFragment)
        }
    }
}