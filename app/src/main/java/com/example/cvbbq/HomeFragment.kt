package com.example.cvbbq

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.cvbbq.databinding.FragmentHomeBinding
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RoastViewModel by activityViewModels()
    private var cvUri: Uri? = null

    // File Picker Launcher
    private val pickCVLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            handleFileSelection(uri)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinners()
        setupClickListeners()

        // Observe Loading State (Optional: Disable button while loading)
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.roastButton.isEnabled = !isLoading
            binding.roastButton.text = if (isLoading) "Roasting..." else "Roast Me!"
        }
    }

    private fun setupSpinners() {
        // Note: Ensure you have a spinner_item.xml layout for white text, or use simple_spinner_dropdown_item
        val languages = listOf("English", "Malayalam", "Hindi", "Tamil")
        binding.languageSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, languages)

        val intensities = listOf("Mild", "Medium", "Nuclear")
        binding.intensitySpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, intensities)
    }

    private fun setupClickListeners() {
        // 1. Open File Picker
        binding.uploadCvButton.setOnClickListener {
            pickCVLauncher.launch("*/*") // Allows PDF and Doc
        }

        // 2. Remove Selected File (Switch UI back)
        binding.removeFileButton.setOnClickListener {
            cvUri = null
            binding.uploadCvButton.visibility = View.VISIBLE // Show dashed box
            binding.fileCard.visibility = View.GONE          // Hide card
            binding.cvPathText.visibility = View.VISIBLE     // Show helper text
            binding.cvPathText.text = "No file selected"
        }

        // 3. Roast Action
        binding.roastButton.setOnClickListener {
            if (cvUri == null) {
                Toast.makeText(context, "Please upload a CV first!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val textContent = extractTextFromUri(cvUri!!)
            if (textContent.length < 50) {
                Toast.makeText(context, "Could not read text. Is this a valid PDF/Doc?", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val language = binding.languageSpinner.selectedItem.toString()
            val intensity = binding.intensitySpinner.selectedItem.toString()

            // Trigger ViewModel
            viewModel.roastCV(textContent, language, intensity)

            // Navigate to Results
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RoastFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun handleFileSelection(uri: Uri) {
        cvUri = uri
        val fileName = getFileName(uri)

        // UI Toggle
        binding.uploadCvButton.visibility = View.GONE
        binding.cvPathText.visibility = View.GONE
        binding.fileCard.visibility = View.VISIBLE

        binding.selectedFileName.text = fileName
    }

    private fun getFileName(uri: Uri): String {
        var name = "Unknown File"
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (index != -1) name = it.getString(index)
            }
        }
        return name
    }

    private fun extractTextFromUri(uri: Uri): String {
        val contentResolver = requireContext().contentResolver
        val type = contentResolver.getType(uri)

        return try {
            if (type == "application/pdf") {
                // Extract PDF Text using iText7
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    val pdfReader = PdfReader(inputStream)
                    val pdfDocument = PdfDocument(pdfReader)
                    val sb = StringBuilder()
                    for (i in 1..pdfDocument.numberOfPages) {
                        sb.append(PdfTextExtractor.getTextFromPage(pdfDocument.getPage(i))).append("\n")
                    }
                    pdfDocument.close()
                    sb.toString()
                } ?: ""
            } else {
                // Fallback for plain text files
                contentResolver.openInputStream(uri)?.bufferedReader().use { it?.readText() ?: "" }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Error reading file: ${e.message}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}