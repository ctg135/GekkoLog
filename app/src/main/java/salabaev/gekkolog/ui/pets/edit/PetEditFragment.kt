package salabaev.gekkolog.ui.pets.edit

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import salabaev.gekkolog.data.GeckosDatabase
import salabaev.gekkolog.databinding.FragmentPetEditBinding
import java.io.File
import salabaev.gekkolog.data.gecko.Gecko
import salabaev.gekkolog.data.gecko.GeckoRepository
import java.io.FileOutputStream
import androidx.core.os.bundleOf
import salabaev.gekkolog.ui.utils.DatePickerHelper.showDatePickerDialog
import androidx.navigation.fragment.findNavController
import salabaev.gekkolog.R

class PetEditFragment : Fragment() {

    companion object {
        private const val REQUEST_IMAGE_PICK = 1001


        fun newInstance(geckoId: Int = 0): PetEditFragment {
            return PetEditFragment().apply {
                val gecko = bundleOf("geckoId" to geckoId)
                arguments = gecko
            }
        }
    }

    private var _binding: FragmentPetEditBinding? = null
    private val binding get() = _binding!!
//    private val viewModel: PetEditViewModel by viewModels()
    private lateinit var viewModel: PetEditViewModel
    private var currentPhotoUri: Uri? = null
//    private val viewModel: PetEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPetEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = GeckosDatabase.getInstance(requireContext()).GeckoDao()
        viewModel = PetEditViewModel(GeckoRepository(dao))

        setupViews()
        observeViewModel()
        loadGeckoData()
    }

    private fun setupViews() {
        binding.geckoImage.setOnClickListener { selectImageFromGallery() }
        binding.saveButton.setOnClickListener { saveGecko() }
        binding.ageEdit.setOnClickListener {
            showDatePickerDialog(requireContext()) { ageText ->
                binding.ageEdit.setText(ageText)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.gecko.observe(viewLifecycleOwner) { gecko ->
            gecko?.let { updateUI(it) }
        }
    }

    private fun loadGeckoData() {
        arguments?.getInt("geckoId")?.let { geckoId ->
            if (geckoId != 0) {
                viewModel.loadGecko(geckoId)
            }
        }
    }

    private fun updateUI(gecko: Gecko) {
        binding.nameEdit.setText(gecko.name)
        binding.morphEdit.setText(gecko.morph)
        binding.genderEdit.setText(gecko.gender)
        binding.feedPeriodEdit.setText(gecko.feedPeriod?.toString())

        // Загрузка изображения
        gecko.photoPath?.let { path: String ->
            Glide.with(this)
                .load(File(path))
                .into(binding.geckoImage)
        }
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                currentPhotoUri = uri
                Glide.with(this)
                    .load(uri)
                    .into(binding.geckoImage)
            }
        }
    }

//    private fun showDatePickerDialog(requireContext: Context, param: (Any) -> Unit) {
//        val calendar = Calendar.getInstance()
//        DatePickerDialog(
//            requireContext(),
//            { _, year, month, day ->
//                val selectedDate = Calendar.getInstance().apply {
//                    set(year, month, day)
//                }
//                val ageText = calculateAge(selectedDate)
//                binding.ageEdit.setText(ageText)
//            },
//            calendar.get(Calendar.YEAR),
//            calendar.get(Calendar.MONTH),
//            calendar.get(Calendar.DAY_OF_MONTH)
//                .show()
//    }

    private fun saveGecko() {
        val gecko = Gecko().apply {
            id = arguments?.getInt("geckoId") ?: 0
            name = binding.nameEdit.text.toString()
            morph = binding.morphEdit.text.toString()
            gender = binding.genderEdit.text.toString()
            feedPeriod = binding.feedPeriodEdit.text.toString().toIntOrNull()
            currentPhotoUri?.let { uri ->
                photoPath = saveImageToInternalStorage(uri).absolutePath
            }
        }

        viewModel.saveGecko(gecko)
        findNavController().popBackStack()
    }

    private fun saveImageToInternalStorage(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().filesDir, "gecko_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { output ->
            inputStream?.copyTo(output)
        }
        return file
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}