package com.example.cvbbq

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RoastViewModel : ViewModel() {

    // FULL combined roast (if needed)
    private val _roastText = MutableLiveData<String>()
    val roastText: LiveData<String> get() = _roastText

    // Section-wise roast list
    private val _sectionRoasts = MutableLiveData<List<String>>()
    val sectionRoasts: LiveData<List<String>> get() = _sectionRoasts

    // Metrics
    private val _hrTrauma = MutableLiveData<Int>()
    val hrTrauma: LiveData<Int> get() = _hrTrauma

    private val _grammarDisaster = MutableLiveData<Int>()
    val grammarDisaster: LiveData<Int> get() = _grammarDisaster

    private val _overconfidence = MutableLiveData<Int>()
    val overconfidence: LiveData<Int> get() = _overconfidence

    private val _memePotential = MutableLiveData<Int>()
    val memePotential: LiveData<Int> get() = _memePotential

    // GIF URL
    private val _gifUrl = MutableLiveData<String>()
    val gifUrl: LiveData<String> get() = _gifUrl

    // Selected language & intensity
    private val _intensity = MutableLiveData<String>()
    val intensity: LiveData<String> get() = _intensity

    private val _language = MutableLiveData<String>()
    val language: LiveData<String> get() = _language



    // 🔥🔥 SECTION-WISE ROAST FUNCTION 🔥🔥
    fun roastCV(cvText: String, language: String, intensity: String) {

        _language.value = language
        _intensity.value = intensity

        // 1. Split CV into paragraphs/sections
        val sections: List<String> = cvText
            .split("\n\n", "\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        // 2. Roast each section individually
        val roastedSections = sections.mapIndexed { index, section ->

            val roastLine = when (intensity) {
                "Mild" -> "This part is okay… your CV isn’t burning yet 😐🔥"
                "Medium" -> "This section is questionable 😂🔥"
                "Nuclear" -> "This alone could give HR a heart attack 💀🔥🔥🔥"
                else -> "Interesting section 🤨"
            }

            "🔥 Section ${index + 1}\n$roastLine\n\n📄 Text: $section"
        }

        _sectionRoasts.value = roastedSections

        // 3. Combine into one big roast block (optional)
        _roastText.value = roastedSections.joinToString("\n\n--------------------\n\n")

        // 4. Metrics (scaled by number of sections)
        val boost = roastedSections.size * 5

        _hrTrauma.value = when (intensity) {
            "Mild" -> 20 + boost
            "Medium" -> 50 + boost
            "Nuclear" -> 90 + boost
            else -> 40
        }.coerceAtMost(100)

        _grammarDisaster.value = (20..95).random()
        _overconfidence.value = (10..90).random()
        _memePotential.value = (30..100).random()

        // 5. GIF suggestion (placeholder)
        _gifUrl.value =
            "https://media.giphy.com/media/3o7aD2saalBwwftBIY/giphy.gif"
    }
}