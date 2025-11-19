package com.example.cvbbq

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class RoastViewModel : ViewModel() {

    private val _roastText = MutableLiveData<String>()
    val roastText: LiveData<String> get() = _roastText

    private val _sectionRoasts = MutableLiveData<List<String>>()
    val sectionRoasts: LiveData<List<String>> get() = _sectionRoasts

    val hrTrauma = MutableLiveData<Int>()
    val grammarDisaster = MutableLiveData<Int>()
    val memePotential = MutableLiveData<Int>()
    val gifUrl = MutableLiveData<String>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun roastCV(cvText: String, language: String, intensity: String) {
        _isLoading.postValue(true)

        val apiKey = BuildConfig.GEMINI_API_KEY

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val prompt = """
                    You are a brutal but funny HR roasting this CV.
                    Language: $language
                    Intensity: $intensity
                    
                    Output valid JSON ONLY:
                    {
                      "sections": [
                        {"title": "Experience", "roast": "short roast"},
                        {"title": "Skills", "roast": "short roast"}
                      ],
                      "overall": "summary roast"
                    }

                    CV Text:
                    $cvText
                """.trimIndent()

                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(GeminiPart(prompt))
                        )
                    )
                )

                val response = GeminiClient.api.generateRoast(apiKey, request)

                val aiText = response
                    .candidates
                    ?.firstOrNull()
                    ?.content
                    ?.parts
                    ?.firstOrNull()
                    ?.text

                if (aiText != null)
                    parseRoastJson(aiText)
                else
                    _roastText.postValue("No response from Gemini.")

                generateMetrics(intensity)

            } catch (e: Exception) {
                _roastText.postValue("Error: ${e.localizedMessage}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private fun parseRoastJson(raw: String) {
        val clean = raw.replace("```json", "").replace("```", "").trim()

        try {
            val obj = JSONObject(clean)
            val sections = obj.optJSONArray("sections") ?: JSONArray()
            val list = mutableListOf<String>()

            for (i in 0 until sections.length()) {
                val item = sections.getJSONObject(i)
                list.add("🔥 ${item.getString("title")}\n${item.getString("roast")}")
            }

            _sectionRoasts.postValue(list)
            _roastText.postValue(obj.optString("overall", "Roast complete."))

        } catch (e: Exception) {
            _roastText.postValue("JSON Parse Error: $clean")
        }
    }

    private fun generateMetrics(intensity: String) {
        val base = if (intensity == "Nuclear") 60 else 20

        hrTrauma.postValue((base..100).random())
        grammarDisaster.postValue((10..90).random())
        memePotential.postValue((20..100).random())
        gifUrl.postValue("https://media.giphy.com/media/3o7aD2saalBwwftBIY/giphy.gif")
    }
}
