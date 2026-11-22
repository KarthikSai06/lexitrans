package com.example.lexitrans.ui.main

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lexitrans.data.BenchmarkMetrics
import com.example.lexitrans.data.ComparativeSample
import com.example.lexitrans.data.CorpusPair
import com.example.lexitrans.data.DefaultTranslationRepository
import com.example.lexitrans.data.ExperimentMetric
import com.example.lexitrans.data.LexiconEntry
import com.example.lexitrans.data.ModelTechnique
import com.example.lexitrans.data.TranslationDirection
import com.example.lexitrans.data.TranslationResult
import com.example.lexitrans.ui.theme.AmberContainer
import com.example.lexitrans.ui.theme.AmberWarning
import com.example.lexitrans.ui.theme.BorderStroke
import com.example.lexitrans.ui.theme.BorderVariant
import com.example.lexitrans.ui.theme.EmeraldContainer
import com.example.lexitrans.ui.theme.EmeraldSuccess
import com.example.lexitrans.ui.theme.IndigoContainer
import com.example.lexitrans.ui.theme.IndigoTertiary
import com.example.lexitrans.ui.theme.LightBackground
import com.example.lexitrans.ui.theme.LightSurface
import com.example.lexitrans.ui.theme.LightSurfaceVariant
import com.example.lexitrans.ui.theme.NavyContainer
import com.example.lexitrans.ui.theme.NavyPrimary
import com.example.lexitrans.ui.theme.OnIndigoContainer
import com.example.lexitrans.ui.theme.OnNavyContainer
import com.example.lexitrans.ui.theme.OnNavyPrimary
import com.example.lexitrans.ui.theme.OnTealContainer
import com.example.lexitrans.ui.theme.TealContainer
import com.example.lexitrans.ui.theme.TealSecondary
import com.example.lexitrans.ui.theme.TextPrimary
import com.example.lexitrans.ui.theme.TextSecondary
import com.example.lexitrans.ui.theme.TextTertiary

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onItemClick: ((Any) -> Unit)? = null
) {
    val context = LocalContext.current
    val app = context.applicationContext as Application
    val viewModel: MainScreenViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainScreenViewModel(app, DefaultTranslationRepository(app)) as T
            }
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MainScreenContent(
        uiState = uiState,
        onTabSelected = viewModel::onTabSelected,
        onSourceTextChanged = viewModel::onSourceTextChanged,
        onDirectionSelected = viewModel::onDirectionSelected,
        onSwapDirection = viewModel::onSwapDirection,
        onTechniqueSelected = viewModel::onTechniqueSelected,
        onPresetSelected = viewModel::onPresetSelected,
        onTranslateClicked = viewModel::onTranslateClicked,
        onSelectComparativeSample = viewModel::onSelectComparativeSample,
        onSelectExperiment = viewModel::onSelectExperiment,
        onBpeWordChanged = viewModel::onBpeWordChanged,
        onCorpusFilterChanged = viewModel::onCorpusFilterChanged,
        onCorpusSearchChanged = viewModel::onCorpusSearchChanged,
        onLexiconSearchChanged = viewModel::onLexiconSearchChanged,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
    uiState: MainUiState,
    onTabSelected: (Int) -> Unit,
    onSourceTextChanged: (String) -> Unit,
    onDirectionSelected: (TranslationDirection) -> Unit,
    onSwapDirection: () -> Unit,
    onTechniqueSelected: (ModelTechnique) -> Unit,
    onPresetSelected: (String) -> Unit,
    onTranslateClicked: () -> Unit,
    onSelectComparativeSample: (ComparativeSample) -> Unit,
    onSelectExperiment: (ExperimentMetric) -> Unit,
    onBpeWordChanged: (String) -> Unit,
    onCorpusFilterChanged: (String) -> Unit,
    onCorpusSearchChanged: (String) -> Unit,
    onLexiconSearchChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "LexiTrans",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = NavyContainer,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "EN ⇄ KN (ಕನ್ನಡ)",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = NavyPrimary
                                    )
                                )
                            }
                        }
                        Text(
                            text = "Low-Resource Dravidian NLP Neural Translator",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightSurface,
                    titleContentColor = NavyPrimary
                )
            )
        },
        containerColor = LightBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Elegant Light Theme Tab Row
            val tabs = listOf("Translator", "Techniques Lab", "Corpus & BPE")
            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = LightSurface,
                contentColor = NavyPrimary,
                divider = {
                    HorizontalDivider(color = BorderStroke, thickness = 1.dp)
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = uiState.selectedTab == index,
                        onClick = { onTabSelected(index) },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (uiState.selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                    color = if (uiState.selectedTab == index) NavyPrimary else TextSecondary,
                                    fontSize = 12.sp
                                ),
                                maxLines = 1
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (uiState.selectedTab) {
                0 -> TranslationSection(
                    uiState = uiState,
                    onSourceTextChanged = onSourceTextChanged,
                    onDirectionSelected = onDirectionSelected,
                    onSwapDirection = onSwapDirection,
                    onTechniqueSelected = onTechniqueSelected,
                    onPresetSelected = onPresetSelected,
                    onTranslateClicked = onTranslateClicked
                )
                1 -> TechniquesLabSection(
                    benchmarkMetrics = uiState.benchmarkMetrics,
                    comparativeSamples = uiState.comparativeSamples,
                    selectedComparativeSample = uiState.selectedComparativeSample,
                    selectedExperimentDetail = uiState.selectedExperimentDetail,
                    onSelectComparativeSample = onSelectComparativeSample,
                    onSelectExperiment = onSelectExperiment
                )
                2 -> CorpusStudioSection(
                    bpeWord = uiState.bpeTestWord,
                    bpeTokens = uiState.bpeTokens,
                    onBpeWordChanged = onBpeWordChanged,
                    corpusPairs = uiState.corpusPairs,
                    corpusFilter = uiState.corpusFilter,
                    corpusSearchQuery = uiState.corpusSearchQuery,
                    onCorpusFilterChanged = onCorpusFilterChanged,
                    onCorpusSearchChanged = onCorpusSearchChanged,
                    lexiconEntries = uiState.lexiconEntries,
                    lexiconSearchQuery = uiState.lexiconSearchQuery,
                    onLexiconSearchChanged = onLexiconSearchChanged
                )
            }
        }
    }
}

// ============================================================================
// TAB 1: NEURAL TRANSLATOR SECTION (BIDIRECTIONAL EN <-> KN)
// ============================================================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TranslationSection(
    uiState: MainUiState,
    onSourceTextChanged: (String) -> Unit,
    onDirectionSelected: (TranslationDirection) -> Unit,
    onSwapDirection: () -> Unit,
    onTechniqueSelected: (ModelTechnique) -> Unit,
    onPresetSelected: (String) -> Unit,
    onTranslateClicked: () -> Unit
) {
    val activeDir = uiState.translationResult?.direction ?: uiState.selectedDirection
    val isKnToEn = activeDir == TranslationDirection.KN_TO_EN

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(2.dp)) }

        // Direction & Swap Selector Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderStroke)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TRANSLATION DIRECTION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                        )
                        Surface(
                            color = TealContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (isKnToEn) "ಕನ್ನಡ ➔ English" else "English ➔ ಕನ್ನಡ",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnTealContainer
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Direction Chips
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            listOf(
                                TranslationDirection.AUTO to "Auto",
                                TranslationDirection.EN_TO_KN to "EN ➔ KN",
                                TranslationDirection.KN_TO_EN to "KN ➔ EN"
                            ).forEach { (dir, label) ->
                                val isSelected = uiState.selectedDirection == dir
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) NavyPrimary else LightSurfaceVariant)
                                        .clickable { onDirectionSelected(dir) }
                                        .padding(horizontal = 10.dp, vertical = 7.dp)
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) OnNavyPrimary else TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }

                        // Swap Button
                        IconButton(
                            onClick = onSwapDirection,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = NavyContainer,
                                contentColor = NavyPrimary
                            ),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Text(
                                text = "⇄",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }

        // Technique Selector Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderStroke)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LOW-RESOURCE TECHNIQUE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                        )
                        Surface(
                            color = when (uiState.selectedTechnique) {
                                ModelTechnique.BASELINE -> AmberContainer
                                ModelTechnique.BPE -> IndigoContainer
                                ModelTechnique.AUGMENTATION -> TealContainer
                                ModelTechnique.TRANSFER -> EmeraldContainer
                            },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = uiState.selectedTechnique.badge,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = when (uiState.selectedTechnique) {
                                        ModelTechnique.BASELINE -> AmberWarning
                                        ModelTechnique.BPE -> OnIndigoContainer
                                        ModelTechnique.AUGMENTATION -> OnTealContainer
                                        ModelTechnique.TRANSFER -> EmeraldSuccess
                                    }
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 4 Technique Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ModelTechnique.values().forEach { tech ->
                            val isSelected = tech == uiState.selectedTechnique
                            val shortLabel = when (tech) {
                                ModelTechnique.BASELINE -> "Baseline"
                                ModelTechnique.BPE -> "+BPE"
                                ModelTechnique.AUGMENTATION -> "+Augment"
                                ModelTechnique.TRANSFER -> "+Transfer"
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NavyPrimary else LightSurfaceVariant)
                                    .clickable { onTechniqueSelected(tech) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = shortLabel,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) OnNavyPrimary else TextSecondary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = uiState.selectedTechnique.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        // Source Input Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderStroke)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isKnToEn) "SOURCE TEXT (ಕನ್ನಡ - KANNADA)" else "SOURCE TEXT (ENGLISH)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = uiState.sourceText,
                        onValueChange = onSourceTextChanged,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LightSurface,
                            unfocusedContainerColor = LightSurface,
                            focusedBorderColor = NavyPrimary,
                            unfocusedBorderColor = BorderStroke,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        placeholder = {
                            Text(
                                text = if (isKnToEn) "ಇಲ್ಲಿ ಕನ್ನಡ ಪಠ್ಯವನ್ನು ಟೈಪ್ ಮಾಡಿ..." else "Type English sentence here...",
                                color = TextTertiary
                            )
                        },
                        minLines = 2,
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset Chips (dynamically adapts to source language)
                    val activePresets = if (isKnToEn) uiState.knPresets else uiState.enPresets
                    Text(
                        text = "Benchmark Test Sentences:",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        activePresets.forEach { prompt ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = LightSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke),
                                modifier = Modifier.clickable { onPresetSelected(prompt) }
                            ) {
                                Text(
                                    text = prompt,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = NavyPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onTranslateClicked,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (uiState.isTranslating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = OnNavyPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Decoding Subwords...")
                        } else {
                            Text(
                                text = if (isKnToEn) "Translate to English" else "Translate to Kannada (ಅನುವಾದಿಸಿ)",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Translation Output Card
        item {
            val result = uiState.translationResult
            Card(
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderStroke)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isKnToEn) "TRANSLATION (ENGLISH)" else "TRANSLATION (ಕನ್ನಡ - KANNADA)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                        )
                        if (result != null) {
                            Text(
                                text = "Conf: ${(result.confidence * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (result.confidence > 0.8) EmeraldSuccess else AmberWarning,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (result != null && result.translation.isNotBlank()) {
                        Surface(
                            color = NavyContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = result.translation,
                                modifier = Modifier.padding(14.dp),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = NavyPrimary,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 26.sp
                                )
                            )
                        }

                        // OOV Warning if any
                        if (result.oovTokens.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = AmberContainer,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "⚠️ Suffix / OOV Alert: Agglutinative suffixes failed in Baseline word model. Use '+Subword BPE' or '+Transfer' to preserve Dravidian morphology.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = AmberWarning,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }

                        // Subword Decomposition Inspector
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "BPE Subword Segmentation Analysis:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            result.subwords.forEach { token ->
                                Surface(
                                    color = IndigoContainer,
                                    shape = RoundedCornerShape(6.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderVariant)
                                ) {
                                    Text(
                                        text = token.replace("</w>", " ↵"),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = OnIndigoContainer,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }

                        if (result.usedDictionaryFallback) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "ℹ️ Bilingual lexicon induction applied for regional terminology.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TealSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    } else {
                        Text(
                            text = "Tap 'Translate' to run sequence-to-sequence decoding.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextTertiary)
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

// ============================================================================
// TAB 2: TECHNIQUES LAB & BENCHMARK SECTION
// ============================================================================
@Composable
fun TechniquesLabSection(
    benchmarkMetrics: BenchmarkMetrics?,
    comparativeSamples: List<ComparativeSample>,
    selectedComparativeSample: ComparativeSample?,
    selectedExperimentDetail: ExperimentMetric?,
    onSelectComparativeSample: (ComparativeSample) -> Unit,
    onSelectExperiment: (ExperimentMetric) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Empirical Benchmark Summary
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderStroke)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ENGLISH ⇄ KANNADA BENCHMARK ABLATIONS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "Agglutinative Dravidian benchmark: 80 Parallel Pairs + 55 Synthetic Back-Translations",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    benchmarkMetrics?.experiments?.forEach { exp ->
                        val isSelected = exp.id == selectedExperimentDetail?.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onSelectExperiment(exp) },
                            color = if (isSelected) NavyContainer else LightSurfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) NavyPrimary else BorderStroke
                            )
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = exp.name,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) NavyPrimary else TextPrimary
                                        )
                                    )
                                    Text(
                                        text = "BLEU-4: ${exp.bleu4}",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldSuccess
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "chrF: ${exp.chrf}  |  OOV: ${exp.oovRate}%",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Text(
                                        text = "Loss: ${exp.finalLoss}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { (exp.bleu4 / 45.0).toFloat().coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = if (exp.id == 4) EmeraldSuccess else NavyPrimary,
                                    trackColor = BorderStroke
                                )
                            }
                        }
                    }
                }
            }
        }

        // Comparative Sample Sentence Explorer (Both Directions)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderStroke)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "BIDIRECTIONAL SENTENCE ABLATION MATRIX",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "Comparing Baseline vs +BPE vs +Back-Translation vs +Transfer & Regularization",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Sentence Selector Pills
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        comparativeSamples.forEach { sample ->
                            val isSelected = sample.id == selectedComparativeSample?.id
                            val dirBadge = if (sample.direction == "KN_TO_EN") "KN➔EN" else "EN➔KN"
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) NavyPrimary else LightSurfaceVariant,
                                modifier = Modifier.clickable { onSelectComparativeSample(sample) }
                            ) {
                                Text(
                                    text = "$dirBadge #${sample.id}",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) OnNavyPrimary else TextSecondary
                                    )
                                )
                            }
                        }
                    }

                    if (selectedComparativeSample != null) {
                        Spacer(modifier = Modifier.height(14.dp))

                        // Source & Reference
                        val srcLang = if (selectedComparativeSample.direction == "KN_TO_EN") "Kannada" else "English"
                        val refLang = if (selectedComparativeSample.direction == "KN_TO_EN") "English" else "Kannada"
                        Surface(
                            color = LightSurfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Source ($srcLang): \"${selectedComparativeSample.source}\"",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Reference ($refLang): \"${selectedComparativeSample.reference}\"",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TealSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Model Output Comparisons
                        ModelComparisonRow("1. Baseline", selectedComparativeSample.baseline, AmberWarning, "Heavy Suffix OOV")
                        Spacer(modifier = Modifier.height(6.dp))
                        ModelComparisonRow("2. +Subword BPE", selectedComparativeSample.bpe, IndigoTertiary, "Subwords segment Kannada morphemes")
                        Spacer(modifier = Modifier.height(6.dp))
                        ModelComparisonRow("3. +Augmentation", selectedComparativeSample.augmentation, TealSecondary, "Back-translation boosts fluency")
                        Spacer(modifier = Modifier.height(6.dp))
                        ModelComparisonRow("4. +Transfer & Reg", selectedComparativeSample.transfer, EmeraldSuccess, "Optimal SOTA quality")
                    }
                }
            }
        }

        // Low-Resource Technique Deep Dives
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderStroke)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "DRAVIDIAN NLP LOW-RESOURCE TECHNIQUES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    TechInsightItem(
                        title = "1. Subword Tokenization (BPE / SentencePiece)",
                        description = "Kannada has agglutinative postpositions (e.g. ಶಾಲೆಯಲ್ಲಿ = ಶಾಲೆ + ಯಲ್ಲಿ, 'in school'). Word-level tokenizers treat each inflected form as an unseen OOV token. BPE isolates root nouns from grammatical affixes.",
                        accentColor = IndigoTertiary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TechInsightItem(
                        title = "2. Back-Translation (Synthetic Corpus)",
                        description = "Monolingual Kannada sentences from regional articles are translated back into English using an inverse model, synthesizing thousands of parallel sentences at zero labeling cost.",
                        accentColor = TealSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TechInsightItem(
                        title = "3. Indic Cross-Lingual Transfer & Regularization",
                        description = "Initializes shared representations across Indic languages (like IndicTrans/mBART). Incorporates label smoothing (0.1) and higher dropout (0.35) to prevent memorization and alignment drift on tiny corpora.",
                        accentColor = EmeraldSuccess
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun ModelComparisonRow(title: String, text: String, color: Color, note: String) {
    Surface(
        color = LightSurface,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                )
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text.ifBlank { "..." },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            )
        }
    }
}

@Composable
fun TechInsightItem(title: String, description: String, accentColor: Color) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(accentColor)
                .align(Alignment.Top)
                .padding(top = 4.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            )
        }
    }
}

// ============================================================================
// TAB 3: CORPUS & BPE STUDIO
// ============================================================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CorpusStudioSection(
    bpeWord: String,
    bpeTokens: List<String>,
    onBpeWordChanged: (String) -> Unit,
    corpusPairs: List<CorpusPair>,
    corpusFilter: String,
    corpusSearchQuery: String,
    onCorpusFilterChanged: (String) -> Unit,
    onCorpusSearchChanged: (String) -> Unit,
    lexiconEntries: List<LexiconEntry>,
    lexiconSearchQuery: String,
    onLexiconSearchChanged: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Interactive BPE Tokenizer Sandbox
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderStroke)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "KANNADA & ENGLISH BPE SUBWORD SANDBOX",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "Test live decomposition of agglutinated Kannada words (e.g. ಶಾಲೆಯಲ್ಲಿ, ಬೆಂಗಳೂರಿನಲ್ಲಿ, ಪುಸ್ತಕಗಳನ್ನು)",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = bpeWord,
                        onValueChange = onBpeWordChanged,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        label = { Text("Word to Segment (ಪದ)", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LightSurface,
                            unfocusedContainerColor = LightSurface,
                            focusedBorderColor = IndigoTertiary,
                            unfocusedBorderColor = BorderStroke
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Segmented Subwords (${bpeTokens.size} units):",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        bpeTokens.forEach { token ->
                            Surface(
                                color = IndigoContainer,
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderVariant)
                            ) {
                                Text(
                                    text = token.replace("</w>", " ↵"),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = OnIndigoContainer,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Parallel Corpus Explorer
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderStroke)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ENGLISH ⇄ KANNADA CORPUS EXPLORER",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "Browse authentic training pairs and synthetic back-translations",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Filter Options
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("All", "Original", "Synthetic").forEach { filter ->
                            val isSelected = filter == corpusFilter
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) TealSecondary else LightSurfaceVariant,
                                modifier = Modifier.clickable { onCorpusFilterChanged(filter) }
                            ) {
                                Text(
                                    text = filter,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) OnTealContainer else TextSecondary
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = corpusSearchQuery,
                        onValueChange = onCorpusSearchChanged,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        placeholder = { Text("Search English or Kannada text...", color = TextTertiary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LightSurface,
                            unfocusedContainerColor = LightSurface,
                            focusedBorderColor = TealSecondary,
                            unfocusedBorderColor = BorderStroke
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val filtered = corpusPairs.filter { pair ->
                        when (corpusFilter) {
                            "Original" -> !pair.isSynthetic
                            "Synthetic" -> pair.isSynthetic
                            else -> true
                        } && (corpusSearchQuery.isBlank() ||
                                pair.source.contains(corpusSearchQuery, ignoreCase = true) ||
                                pair.target.contains(corpusSearchQuery, ignoreCase = true))
                    }

                    Text(
                        text = "Displaying ${filtered.size} pairs:",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    filtered.take(15).forEach { pair ->
                        Surface(
                            color = LightSurfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = pair.source,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                    if (pair.isSynthetic) {
                                        Surface(
                                            color = TealContainer,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "Back-Trans",
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = OnTealContainer,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = pair.target,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = NavyPrimary,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}
