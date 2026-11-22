package com.example.lexitrans.data

data class ExperimentMetric(
    val id: Int,
    val name: String,
    val shortName: String,
    val description: String,
    val bleu1: Double,
    val bleu4: Double,
    val chrf: Double,
    val oovRate: Double,
    val finalLoss: Double,
    val historyLoss: List<Double>
)

data class BenchmarkMetrics(
    val experiments: List<ExperimentMetric>,
    val trainSetSize: Int,
    val testSetSize: Int,
    val augmentedSize: Int,
    val languagePair: String = "en-kn"
)

enum class TranslationDirection(
    val sourceName: String,
    val targetName: String,
    val label: String
) {
    AUTO("Auto-Detect", "Auto", "Auto-Detect (ಸ್ವಯಂ ಪತ್ತೆ)"),
    EN_TO_KN("English", "ಕನ್ನಡ (Kannada)", "English ➔ ಕನ್ನಡ"),
    KN_TO_EN("ಕನ್ನಡ (Kannada)", "English", "ಕನ್ನಡ ➔ English")
}

data class ComparativeSample(
    val id: Int,
    val direction: String = "EN_TO_KN",
    val source: String,
    val reference: String,
    val baseline: String,
    val bpe: String,
    val augmentation: String,
    val transfer: String
)

data class CorpusPair(
    val source: String,
    val target: String,
    val isSynthetic: Boolean = false
)

data class LexiconEntry(
    val source: String,
    val target: String
)

enum class ModelTechnique(val displayName: String, val badge: String, val description: String) {
    BASELINE("Baseline (Word-Level)", "Word Seq2Seq", "Limited parallel corpus with word tokenization. High OOV on suffixes."),
    BPE("Technique 1: +Subword BPE", "BPE Subwords", "Decomposes compound words and morphological suffixes (-alli, -inda, -ige)."),
    AUGMENTATION("Technique 2: +Augmentation", "Back-Translation", "Augmented with reverse synthetic translations + monolingual Kannada corpus."),
    TRANSFER("Technique 3: +Transfer & Reg", "SOTA IndicTrans", "Indic cross-lingual transfer, label smoothing (0.1), and high dropout.")
}

data class TranslationResult(
    val source: String,
    val translation: String,
    val direction: TranslationDirection,
    val technique: ModelTechnique,
    val subwords: List<String>,
    val confidence: Double,
    val usedDictionaryFallback: Boolean,
    val oovTokens: List<String>
)
