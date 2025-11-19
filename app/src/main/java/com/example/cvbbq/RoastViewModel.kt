package com.example.cvbbq

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class RoastViewModel : ViewModel() {

    private val _roastText = MutableLiveData<String>()
    val roastText: LiveData<String> get() = _roastText

    private val _sectionRoasts = MutableLiveData<List<String>>()
    val sectionRoasts: LiveData<List<String>> get() = _sectionRoasts

    private val _hrTrauma = MutableLiveData<Int>()
    val hrTrauma: LiveData<Int> get() = _hrTrauma

    private val _grammarDisaster = MutableLiveData<Int>()
    val grammarDisaster: LiveData<Int> get() = _grammarDisaster

    private val _overconfidence = MutableLiveData<Int>()
    val overconfidence: LiveData<Int> get() = _overconfidence

    private val _memePotential = MutableLiveData<Int>()
    val memePotential: LiveData<Int> get() = _memePotential

    private val _gifUrl = MutableLiveData<String>()
    val gifUrl: LiveData<String> get() = _gifUrl

    private val _intensity = MutableLiveData<String>()
    val intensity: LiveData<String> get() = _intensity

    private val _language = MutableLiveData<String>()
    val language: LiveData<String> get() = _language

    private val API_KEY = BuildConfig.GEMINI_API_KEY

    fun roastCV(cvText: String, language: String, intensity: String) {
        _language.value = language
        _intensity.value = intensity

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val prompt = """
                    You are a CV roasting assistant.
                    Roast the following CV section-wise.

                    Language: $language
                    Intensity: $intensity

                    Respond in JSON:
                    {
                        "sections": [
                            {"title": "Section 1", "roast": "..."},
                            {"title": "Section 2", "roast": "..."}
                        ],
                        "overall": "Full combined roast"
                    }

                    CV Text:
                    $cvText
                """.trimIndent()

                val request = GeminiRequest(prompt = prompt, max_tokens = 2000)
                val response = GeminiClient.api.generateRoast(request, "Bearer $API_KEY")

                val jsonText = response.choices.firstOrNull()?.text ?: "{}"
                val jsonObject = JSONObject(jsonText)

                // Parse sections
                val sectionsArray = jsonObject.optJSONArray("sections") ?: JSONArray()
                val roastedSections = mutableListOf<String>()

                for (i in 0 until sectionsArray.length()) {
                    val sectionObj = sectionsArray.getJSONObject(i)
                    roastedSections.add("🔥 ${sectionObj.getString("title")}\n${sectionObj.getString("roast")}")
                }

                _sectionRoasts.postValue(roastedSections)
                _roastText.postValue(jsonObject.optString("overall", "AI roast unavailable"))

                // Example metrics (can be AI-generated too)
                val boost = roastedSections.size * 5
                val hr = when (intensity) {
                    "Mild" -> 20 + boost
                    "Medium" -> 50 + boost
                    "Nuclear" -> 90 + boost
                    else -> 40
                }.coerceAtMost(100)

                _hrTrauma.postValue(hr)
                _grammarDisaster.postValue((20..95).random())
                _overconfidence.postValue((10..90).random())
                _memePotential.postValue((30..100).random())

                _gifUrl.postValue("https://media.giphy.com/media/3o7aD2saalBwwftBIY/giphy.gif")

            } catch (e: Exception) {
                e.printStackTrace()
                _roastText.postValue("Failed to generate roast: ${e.message}")
            }
        }
    }
}
