package com.example.cvbbq

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.cvbbq.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RoastViewModel by activityViewModels()
    private var cvUri: Uri? = null

    private val pickCVLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            cvUri = uri
            val fileName = getFileName(uri)
            binding.uploadCvButton.visibility = View.GONE
            binding.fileCard.visibility = View.VISIBLE

            binding.selectedFileName.text = fileName
            binding.cvPathText.text = fileName
        } else {
            binding.cvPathText.text = "No file selected"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val languages = listOf("English", "Malayalam", "Hindi", "Tamil")
        binding.languageSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            languages
        )

        val intensities = listOf("Mild", "Medium", "Nuclear")
        binding.intensitySpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            intensities
        )

        binding.uploadCvButton.setOnClickListener {
            pickCVLauncher.launch("*/*")
        }

        binding.removeFileButton.setOnClickListener {
            cvUri = null
            binding.uploadCvButton.visibility = View.VISIBLE
            binding.fileCard.visibility = View.GONE
            binding.cvPathText.text = "No file selected"
        }

        binding.roastButton.setOnClickListener {
            if (cvUri == null) {
                binding.cvPathText.text = "Please upload a CV first!"
                return@setOnClickListener
            }

            val cvText = readTextFromUri(cvUri!!)
            val language = binding.languageSpinner.selectedItem.toString()
            val intensity = binding.intensitySpinner.selectedItem.toString()

            viewModel.roastCV(cvText, language, intensity)

            println(cvText)

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RoastFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun readTextFromUri(uri: Uri): String {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            inputStream?.bufferedReader().use { it?.readText() ?: "" }
        } catch (e: Exception) {
            e.printStackTrace()
            "Unable to read file"
        }
    }

    private fun getFileName(uri: Uri): String {
        var name = "Unknown File"

        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = it.getString(nameIndex)
                }
            }
        }
        return name
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}