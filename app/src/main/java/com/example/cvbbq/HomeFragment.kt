package com.example.cvbbq

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.cvbbq.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RoastViewModel by activityViewModels()
    private var cvUri: Uri? = null

    private val PICK_FILE_REQUEST = 100

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
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(Intent.createChooser(intent, "Select CV"), PICK_FILE_REQUEST)
        }

        binding.roastButton.setOnClickListener {
            if (cvUri == null) {
                binding.cvPathText.text = "Please upload a CV first!"
                return@setOnClickListener
            }

            val cvText = cvUri.toString()
            val language = binding.languageSpinner.selectedItem.toString()
            val intensity = binding.intensitySpinner.selectedItem.toString()

            viewModel.roastCV(cvText, language, intensity)

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RoastFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK) {
            cvUri = data?.data
            binding.cvPathText.text = cvUri?.lastPathSegment ?: "File selected"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}