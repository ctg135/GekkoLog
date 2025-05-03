package salabaev.gekkolog.ui.pets

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import salabaev.gekkolog.R
import salabaev.gekkolog.data.GeckosDatabase
import salabaev.gekkolog.data.gecko.GeckoRepository
import salabaev.gekkolog.databinding.FragmentPetsBinding

class PetsFragment : Fragment() {

    private var _binding: FragmentPetsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PetsViewModel
    private lateinit var adapter: PetListAdapter

    companion object {
        fun newInstance() = PetsFragment()
    }

//    private val viewModel: PetsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация ViewModel
        val database = GeckosDatabase.getInstance(requireContext())
        val repository = GeckoRepository(database.GeckoDao())
//        val viewModelFactory = GeckoListViewModelFactory(repository)
//        viewModel = ViewModelProvider(this, viewModelFactory).get(GeckoListViewModel::class.java)
        viewModel = PetsViewModel(repository)

        // Настройка RecyclerView
        adapter = PetListAdapter()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // Подписка на изменения данных
        viewModel.petList?.observe(viewLifecycleOwner) { geckos ->
            adapter.submitList(geckos)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}