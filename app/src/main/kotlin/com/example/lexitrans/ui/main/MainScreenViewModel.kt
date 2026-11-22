package com.example.lexitrans.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lexitrans.data.BenchmarkMetrics
import com.example.lexitrans.data.ComparativeSample
import com.example.lexitrans.data.CorpusPair
import com.example.lexitrans.data.DefaultTranslationRepository
import com.example.lexitrans.data.ExperimentMetric
import com.example.lexitrans.data.LexiconEntry
import com.example.lexitrans.data.ModelTechnique
import com.example.lexitrans.data.TranslationDirection
import com.example.lexitrans.data.TranslationRepository
import com.example.lexitrans.data.TranslationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val selectedTab: Int = 0, // 0: Translate, 1: Lab, 2: Corpus
    // Translator Section
    val sourceText: String = "We learn natural language processing",
    val selectedDirection: TranslationDirection = TranslationDirection.AUTO,
    val selectedTechnique: ModelTechnique = ModelTechnique.TRANSFER,
    val translationResult: TranslationResult? = null,
    val isTranslating: Boolean = false,
    val enPresets: List<String> = listOf(
        "We learn natural language processing",
        "Welcome to our school",
        "Where is the nearest hospital",
        "Clean drinking water prevents many diseases",
        "The computer is working fast",
        "Education brings great opportunities"
    ),
    val knPresets: List<String> = listOf(
        "ನಮ್ಮ ಶಾಲೆಗೆ ಸುಸ್ವಾಗತ",
        "ಹತ್ತಿರದ ಆಸ್ಪತ್ರೆ ಎಲ್ಲಿದೆ",
        "ನಾವು ನೈಸರ್ಗಿಕ ಭಾಷಾ ಸಂಸ್ಕರಣೆಯನ್ನು ಕಲಿಯುತ್ತಿದ್ದೇವೆ",
        "ಕಂಪ್ಯೂಟರ್ ವೇಗವಾಗಿ ಕೆಲಸ ಮಾಡುತ್ತಿದೆ",
        "ಶುದ್ಧ ಕುಡಿಯುವ ನೀರು ಅನೇಕ ರೋಗಗಳನ್ನು ತಡೆಯುತ್ತದೆ",
        "ಶಿಕ್ಷಣವೇ ಜೀವನದ ನಿಜವಾದ ಬೆಳಕು"
    ),
    // Techniques Lab
    val benchmarkMetrics: BenchmarkMetrics? = null,
    val comparativeSamples: List<ComparativeSample> = emptyList(),
    val selectedComparativeSample: ComparativeSample? = null,
    val selectedExperimentDetail: ExperimentMetric? = null,
    // Corpus & BPE Studio
    val bpeTestWord: String = "ಶಾಲೆಯಲ್ಲಿ",
    val bpeTokens: List<String> = emptyList(),
    val corpusPairs: List<CorpusPair> = emptyList(),
    val corpusFilter: String = "All", // "All", "Original", "Synthetic"
    val corpusSearchQuery: String = "",
    val lexiconEntries: List<LexiconEntry> = emptyList(),
    val lexiconSearchQuery: String = ""
)

class MainScreenViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: TranslationRepository = DefaultTranslationRepository(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        try {
            loadInitialData()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun loadInitialData() {
        val metrics = repository.getBenchmarkMetrics()
        val samples = repository.getComparativeSamples()
        val corpus = repository.getCorpusPairs()
        val lexicon = repository.getLexiconEntries()
        val initialBpe = repository.tokenizeBPE(_uiState.value.bpeTestWord)

        _uiState.update { current ->
            current.copy(
                benchmarkMetrics = metrics,
                comparativeSamples = samples,
                selectedComparativeSample = samples.firstOrNull(),
                selectedExperimentDetail = metrics.experiments.lastOrNull(),
                corpusPairs = corpus,
                lexiconEntries = lexicon,
                bpeTokens = initialBpe
            )
        }
        // Run initial translation
        translateText(_uiState.value.sourceText, _uiState.value.selectedDirection, _uiState.value.selectedTechnique)
    }

    fun onTabSelected(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
    }

    fun onSourceTextChanged(newText: String) {
        val hasKannada = newText.any { it in '\u0C80'..'\u0CFF' }
        _uiState.update { current ->
            val detectedDir = if (hasKannada) TranslationDirection.KN_TO_EN else TranslationDirection.EN_TO_KN
            val newDir = if (newText.isBlank()) {
                current.selectedDirection
            } else if (current.selectedDirection == TranslationDirection.AUTO ||
                (hasKannada && current.selectedDirection == TranslationDirection.EN_TO_KN) ||
                (!hasKannada && current.selectedDirection == TranslationDirection.KN_TO_EN)
            ) {
                detectedDir
            } else {
                current.selectedDirection
            }
            current.copy(
                sourceText = newText,
                selectedDirection = newDir
            )
        }
    }

    fun onDirectionSelected(direction: TranslationDirection) {
        _uiState.update { it.copy(selectedDirection = direction) }
        translateText(_uiState.value.sourceText, direction, _uiState.value.selectedTechnique)
    }

    fun onSwapDirection() {
        val currentRes = _uiState.value.translationResult
        val currentSrc = _uiState.value.sourceText

        val newDir = when (_uiState.value.selectedDirection) {
            TranslationDirection.EN_TO_KN -> TranslationDirection.KN_TO_EN
            TranslationDirection.KN_TO_EN -> TranslationDirection.EN_TO_KN
            TranslationDirection.AUTO -> {
                val hasKannada = currentSrc.any { it in '\u0C80'..'\u0CFF' }
                if (hasKannada) TranslationDirection.EN_TO_KN else TranslationDirection.KN_TO_EN
            }
        }

        val newSrc = currentRes?.translation?.takeIf { it.isNotBlank() && !it.contains("<unk>") }
            ?: if (newDir == TranslationDirection.KN_TO_EN) "ನಮ್ಮ ಶಾಲೆಗೆ ಸುಸ್ವಾಗತ" else "Welcome to our school"

        _uiState.update {
            it.copy(
                selectedDirection = newDir,
                sourceText = newSrc
            )
        }
        translateText(newSrc, newDir, _uiState.value.selectedTechnique)
    }

    fun onTechniqueSelected(technique: ModelTechnique) {
        _uiState.update { it.copy(selectedTechnique = technique) }
        translateText(_uiState.value.sourceText, _uiState.value.selectedDirection, technique)
    }

    fun onPresetSelected(prompt: String) {
        val hasKannada = prompt.any { it in '\u0C80'..'\u0CFF' }
        val dir = if (hasKannada) TranslationDirection.KN_TO_EN else TranslationDirection.EN_TO_KN
        _uiState.update { 
            it.copy(
                sourceText = prompt,
                selectedDirection = dir
            ) 
        }
        translateText(prompt, dir, _uiState.value.selectedTechnique)
    }

    fun onTranslateClicked() {
        translateText(_uiState.value.sourceText, _uiState.value.selectedDirection, _uiState.value.selectedTechnique)
    }

    private fun translateText(text: String, direction: TranslationDirection, technique: ModelTechnique) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isTranslating = true) }
                val result = repository.translate(text, direction, technique)
                _uiState.update {
                    it.copy(
                        translationResult = result,
                        isTranslating = false
                    )
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                _uiState.update { it.copy(isTranslating = false) }
            }
        }
    }

    fun onSelectComparativeSample(sample: ComparativeSample) {
        _uiState.update { it.copy(selectedComparativeSample = sample) }
    }

    fun onSelectExperiment(exp: ExperimentMetric) {
        _uiState.update { it.copy(selectedExperimentDetail = exp) }
    }

    fun onBpeWordChanged(word: String) {
        try {
            val tokens = repository.tokenizeBPE(word)
            _uiState.update {
                it.copy(
                    bpeTestWord = word,
                    bpeTokens = tokens
                )
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun onCorpusFilterChanged(filter: String) {
        _uiState.update { it.copy(corpusFilter = filter) }
    }

    fun onCorpusSearchChanged(query: String) {
        _uiState.update { it.copy(corpusSearchQuery = query) }
    }

    fun onLexiconSearchChanged(query: String) {
        _uiState.update { it.copy(lexiconSearchQuery = query) }
    }
}
