package com.example.cvbbq

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.cvbbq.databinding.FragmentRoastBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class RoastFragment : Fragment() {

    private var _binding: FragmentRoastBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RoastViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoastBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe text & metrics
        viewModel.roastText.observe(viewLifecycleOwner) { roast ->
            binding.roastText.text = roast
        }

        viewModel.sectionRoasts.observe(viewLifecycleOwner) { list ->
            val finalText = list.joinToString("\n\n=================\n\n")
            binding.roastText.text = finalText
        }

        viewModel.hrTrauma.observe(viewLifecycleOwner) { binding.hrTraumaBar.progress = it }
        viewModel.grammarDisaster.observe(viewLifecycleOwner) { binding.grammarBar.progress = it }
        viewModel.memePotential.observe(viewLifecycleOwner) { binding.memePotentialBar.progress = it }
        viewModel.gifUrl.observe(viewLifecycleOwner) { url ->
            Glide.with(this)
                .asGif()
                .load(url)
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.roastGif)
        }

        binding.shareButton.setOnClickListener {
            showShareSheet()
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showShareSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_share_sheet, null)

        val roastText = binding.roastText.text.toString()

        val shareAll = view.findViewById<LinearLayout>(R.id.shareAll)
        val shareWhatsapp = view.findViewById<LinearLayout>(R.id.shareWhatsapp)
        val shareInsta = view.findViewById<LinearLayout>(R.id.shareInsta)

        fun animateClick(v: View) {
            v.animate().scaleX(0.93f).scaleY(0.93f).setDuration(80).withEndAction {
                v.animate().scaleX(1f).scaleY(1f).duration = 80
            }
        }

        shareAll.setOnClickListener {
            animateClick(it)
            shareAllApps(roastText)
            dialog.dismiss()
        }

        shareWhatsapp.setOnClickListener {
            animateClick(it)
            shareToWhatsApp(roastText)
            dialog.dismiss()
        }

        shareInsta.setOnClickListener {
            animateClick(it)
            shareToInstagramStory(roastText)
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun shareAllApps(text: String) {
        if (text.isBlank()) {
            Toast.makeText(requireContext(), "Nothing to share!", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(intent, "Share Roast via…"))
    }

    private fun shareToWhatsApp(text: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                setPackage("com.whatsapp")
                putExtra(Intent.EXTRA_TEXT, text)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "WhatsApp not installed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareToInstagramStory(text: String) {
        try {
            val intent = Intent("com.instagram.share.ADD_TO_STORY").apply {
                type = "text/plain"
                setPackage("com.instagram.android")

                putExtra("interactive_asset_text", text)
                putExtra("top_background_color", "#181828")
                putExtra("bottom_background_color", "#181828")
            }
            startActivity(intent)

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Instagram not installed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}