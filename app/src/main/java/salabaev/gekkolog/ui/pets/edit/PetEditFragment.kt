package salabaev.gekkolog.ui.pets.edit

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import com.google.android.material.textfield.MaterialAutoCompleteTextView

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
    private lateinit var viewModel: PetEditViewModel
    private var currentPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        binding.deleteButton.setOnClickListener { alertDeletePet() }
        binding.ageEdit.setOnClickListener {
            showDatePickerDialog(requireContext()) { ageText ->
                binding.ageEdit.setText(ageText)
            }
        }
        val genders = listOf(
            Pair("M", getString(salabaev.gekkolog.R.string.male)),  // "M" - значение для базы, "Самец" - отображаемый текст
            Pair("F", getString(salabaev.gekkolog.R.string.female))) // "F" - значение для базы, "Самка" - отображаемый текст

        val adapter = ArrayAdapter(
            requireContext(),
            salabaev.gekkolog.R.layout.item_dropdown_menu_1line,
            genders.map { it.second }
        )

        val genderDropdown = binding.genderEdit
        genderDropdown.setAdapter(adapter)

        genderDropdown.setOnItemClickListener { _, _, position, _ ->
            // Сохраняем выбранное значение ("M" или "F") в тег
            genderDropdown.tag = genders[position].first
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
        binding.feedPeriodEdit.setText(gecko.feedPeriod?.toString())

        // Загрузка изображения
        gecko.photoPath?.let { path: String ->
            Glide.with(this)
                .load(File(path))
                .into(binding.geckoImage)
        }

        val genderDropdown = binding.genderEdit as MaterialAutoCompleteTextView
        when (gecko.gender) {
            "M" -> {
                genderDropdown.setText(getString(salabaev.gekkolog.R.string.male), false)
                genderDropdown.tag = "M"
            }
            "F" -> {
                genderDropdown.setText(getString(salabaev.gekkolog.R.string.female), false)
                genderDropdown.tag = "F"
            }
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

    private fun saveGecko() {
        val gecko = Gecko().apply {
            id = arguments?.getInt("geckoId") ?: 0
            name = binding.nameEdit.text.toString()
            morph = binding.morphEdit.text.toString()
            gender = binding.genderEdit.tag?.toString() ?: ""
            feedPeriod = binding.feedPeriodEdit.text.toString().toIntOrNull()
            viewModel.gecko.observe(viewLifecycleOwner) { geckoViewModel ->
                if (currentPhotoUri != null) {
                    // Если выбрано новое фото
                    photoPath = saveImageToInternalStorage(currentPhotoUri!!).absolutePath
                } else if (geckoViewModel?.photoPath != null) {
                    // Если фото не изменялось
                    photoPath = geckoViewModel.photoPath
                }
                else {
                    // Если фото не было выбрано
                    photoPath = null
                }
            }
            if (id == 0 && currentPhotoUri != null) {
                photoPath = saveImageToInternalStorage(currentPhotoUri!!).absolutePath
            }
        }
        viewModel.saveGecko(gecko)
        findNavController().popBackStack()
    }

    private fun alertDeletePet() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setMessage("Вы уверены?")
            .setTitle("Удаление питомца")
            .setCancelable(true)
            .setPositiveButton("Да"){ _, _ ->
                deletePet()
            }
            .setNegativeButton("Нет"){ dialog, _ ->
                dialog.cancel()
            }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun deletePet() {
        val geckoId = arguments?.getInt("geckoId") ?: 0
        if (geckoId != 0) {
            viewModel.deleteGecko(geckoId)
            viewModel.gecko.value?.photoPath?.let { path ->
                File(path).takeIf { it.exists() }?.delete()
            }
        }
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