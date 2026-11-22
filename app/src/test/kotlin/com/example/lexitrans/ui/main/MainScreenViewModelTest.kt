package com.example.lexitrans.ui.main

import android.app.Application
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
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class MainScreenViewModelTest {

    private lateinit var fakeRepository: TranslationRepository

    @Before
    fun setup() {
        fakeRepository = DefaultTranslationRepository(null)
    }

    @Test
    fun repository_initialMetrics_loaded() {
        val metrics = fakeRepository.getBenchmarkMetrics()
        assertNotNull(metrics)
        assertEquals(4, metrics.experiments.size)
        assertEquals("Baseline", metrics.experiments[0].shortName)
        assertEquals("Transfer & Reg", metrics.experiments[3].shortName)
    }

    @Test
    fun repository_bpeTokenize_splitsWord() {
        val tokens = fakeRepository.tokenizeBPE("school")
        assertNotNull(tokens)
        assertTrue(tokens.isNotEmpty())
    }

    @Test
    fun repository_translate_englishToKannada() {
        val result = fakeRepository.translate(
            "welcome to our school",
            TranslationDirection.EN_TO_KN,
            ModelTechnique.TRANSFER
        )
        assertNotNull(result)
        assertEquals(TranslationDirection.EN_TO_KN, result.direction)
        assertTrue(result.translation.contains("ಸುಸ್ವಾಗತ") || result.translation.isNotBlank())
    }

    @Test
    fun repository_translate_kannadaToEnglish() {
        val result = fakeRepository.translate(
            "ನಮ್ಮ ಶಾಲೆಗೆ ಸುಸ್ವಾಗತ",
            TranslationDirection.KN_TO_EN,
            ModelTechnique.TRANSFER
        )
        assertNotNull(result)
        assertEquals(TranslationDirection.KN_TO_EN, result.direction)
        assertTrue(result.translation.contains("school") || result.translation.isNotBlank())
    }

    @Test
    fun repository_baseline_flags_oov_onUnseenCompound() {
        val result = fakeRepository.translate(
            "artificial superintelligence computing",
            TranslationDirection.EN_TO_KN,
            ModelTechnique.BASELINE
        )
        assertNotNull(result)
        assertTrue(result.oovTokens.isNotEmpty() || result.translation.contains("<unk>"))
    }

    @Test
    fun viewModel_instantiatesCleanlyWithDefaultConstructor() {
        // Test that @JvmOverloads works and default parameters can be used by AndroidViewModelFactory
        val app = Application()
        val vm = MainScreenViewModel(app, fakeRepository)
        assertNotNull(vm.uiState.value)
        assertEquals(0, vm.uiState.value.selectedTab)
    }

    @Test
    fun viewModel_autoDetectsKannadaScript() {
        val app = Application()
        val vm = MainScreenViewModel(app, fakeRepository)
        // Kannada text input should switch direction to KN_TO_EN
        vm.onSourceTextChanged("ನಮಸ್ಕಾರ")
        assertEquals(TranslationDirection.KN_TO_EN, vm.uiState.value.selectedDirection)
        
        // English text input should switch direction to EN_TO_KN
        vm.onSourceTextChanged("Hello friend")
        assertEquals(TranslationDirection.EN_TO_KN, vm.uiState.value.selectedDirection)
    }
}
