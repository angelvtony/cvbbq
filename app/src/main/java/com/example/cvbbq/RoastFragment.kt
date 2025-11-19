package com.example.cvbbq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.cvbbq.databinding.FragmentRoastBinding

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

        viewModel.roastText.observe(viewLifecycleOwner) { roast ->
            binding.roastText.text = roast
        }

        viewModel.sectionRoasts.observe(viewLifecycleOwner) { list ->
            val finalText = list.joinToString("\n\n=================\n\n")
            binding.roastText.text = finalText
        }

        viewModel.hrTrauma.observe(viewLifecycleOwner) { binding.hrTraumaBar.progress = it }
        viewModel.grammarDisaster.observe(viewLifecycleOwner) { binding.grammarBar.progress = it }
//        viewModel.overconfidence.observe(viewLifecycleOwner) { binding.overconfidenceBar.progress = it }
        viewModel.memePotential.observe(viewLifecycleOwner) { binding.memePotentialBar.progress = it }
        viewModel.gifUrl.observe(viewLifecycleOwner) { url ->
            Glide.with(this)
                .asGif()
                .load(url)
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.roastGif)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}