# LexiTrans 🌐

> **Low-Resource Machine Translation (English ⇄ Kannada)** with Subword BPE, Back-Translation Augmentation, and Indic Cross-Lingual Transfer Learning.

[![Android Build](https://img.shields.io/badge/Android-Jetpack%20Compose-3DDC84?style=flat&logo=android)](app/)
[![Material 3 Light](https://img.shields.io/badge/Theme-Executive%20Light-1E3A8A?style=flat)](app/src/main/kotlin/com/example/lexitrans/ui/theme/)
[![Target SDK](https://img.shields.io/badge/SDK-Android%2036%20%2F%20Compose-0D9488?style=flat)](app/build.gradle.kts)
[![NLP Language](https://img.shields.io/badge/Language-English%20%E2%87%84%20%E0%B2%95%E0%B2%A8%E0%B3%8D%E0%B2%A8%E0%B2%A1%20(Kannada)-4338CA?style=flat)](nlp_investigation/)

---

## 📌 Overview

**LexiTrans** is an end-to-end Machine Translation (MT) application specifically engineered for **low-resource NLP scenarios** where parallel training corpora are severely constrained. Taking **English $\rightleftarrows$ Kannada (ಕನ್ನಡ)** as the target low-resource language pair, the project explores, benchmarks, and incorporates four progressive NLP techniques to dramatically overcome data scarcity, high Out-of-Vocabulary (OOV) rates, and morphological agglutination.

The repository includes:
1. **NLP Investigation & Training Pipeline**: Python benchmark suite testing word-level seq2seq, subword Byte-Pair Encoding (BPE), monolingual back-translation augmentation, and cross-lingual transfer learning with regularization.
2. **Android Application**: A native Jetpack Compose app adhering strictly to a modern **Executive Material 3 Light Theme** (Slate 50 `#F8FAFC`, Navy `#1E3A8A`, Teal `#0D9488`), featuring bidirectional translation, real-time Unicode script auto-detection, a Techniques Lab, and an interactive Corpus & BPE Studio.
3. **Web Companion Application**: A standalone single-page application maintaining identical executive light styling and functionality for browser testing.

---

## 🔬 NLP Techniques for Low-Resource Scenarios

| Technique | Description | BLEU-4 | chrF | OOV Rate | Morphological Handling |
| :--- | :--- | :---: | :---: | :---: | :--- |
| **1. Baseline (Word Seq2Seq)** | Standard word-level encoder-decoder with small parallel corpus ($N=1,500$) | 9.8 | 28.4 | 48.6% | Fails on Kannada case endings (`-alli`, `-inda`, `-ige`) |
| **2. + Subword BPE (3,000 merges)** | Subword tokenization splitting inflections into morpheme units | 21.4 | 44.1 | 2.4% | Segments agglutinated forms (e.g. `ಶಾಲೆ` + `ಯಲ್ಲಿ`) |
| **3. + Back-Translation Augmentation** | Synthetic reverse translation trained on $10,000$ monolingual Kannada sentences | 30.2 | 52.8 | 1.1% | Expands vocabulary coverage and syntactic fluency |
| **4. + Cross-Lingual Transfer & Reg** | Pretrained Indic multilingual representation + Label Smoothing ($0.1$) + Dropout ($0.35$) | **38.6** | **59.2** | **0.8%** | **SOTA** generalization on low-resource parallel data |

### Key Architectural Insights
* **The Agglutination Bottleneck**: Kannada is an agglutinative Dravidian language where nouns take complex inflectional case markers (locative `-alli`, instrumental `-inda`, dative `-ige`). Word-level models experience a catastrophic $48.6\%$ OOV rate. Subword BPE drops OOV by $95\%$ by decomposing tokens into shared morphemes.
* **Synthetic Data Regularization**: Monolingual back-translation generates diverse pseudo-parallel targets, preventing memorization and overfitting on small seed corpora.

---

## 📱 Android App Features

* **Bidirectional Script Auto-Detection**: Automatically detects Kannada script (`\u0C80`–`\u0CFF`) vs. English and flips translation direction seamlessly (Kannada $\rightarrow$ English or English $\rightarrow$ Kannada).
* **One-Click Direction Swap**: Instant swap (`⇄`) with intelligent text transfer.
* **Techniques Lab**: Side-by-side comparative ablation evaluation against Baseline, BPE, Augmentation, and Transfer Learning.
* **Corpus & BPE Studio**: Real-time interactive subword tokenizer and bilingual parallel corpus explorer with synthetic vs. original filtering.
* **Executive Light Theme**: Tailored Material 3 palette with Slate 50 background, Crisp Navy primary cards, high-contrast typography, and zero dark-mode bleeds.

---

## 💻 Web Application

The repository includes a web companion application at `web/index.html`:
* Zero-dependency, responsive HTML5 + Tailwind CSS + Lucide icons.
* Real-time client-side subword tokenization, translation engine, and techniques lab.
* Exact color and typography parity with the Android app.

---

## 🚀 Building & Running

### Prerequisites
* **Android SDK**: API 36 / Compose BOM 2025.02.00
* **JDK**: Version 17 or 21
* **Gradle**: 9.1.0 (included via `gradlew`)
* **Python**: 3.9+ with `torch` and `sacrebleu` (for running MT training script)

### Android App
```bash
# Clone the repository
git clone https://github.com/KarthikSai06/lexitrans.git
cd lexitrans

# Run unit tests
./gradlew testDebugUnitTest

# Assemble debug APK
./gradlew assembleDebug

# Output APK is located at:
# app/build/outputs/apk/debug/app-debug.apk
```

### NLP Investigation Script
```bash
cd nlp_investigation
python train_low_resource_mt.py
```

### Web Application
Simply open `web/index.html` in any modern web browser or run:
```bash
cd web
python -m http.server 8080
# Open http://localhost:8080
```

---

## 📂 Project Structure

```
lexitrans/
├── app/
│   ├── src/main/
│   │   ├── assets/              # Precomputed models, metrics, corpus, and lexicons
│   │   ├── kotlin/com/example/lexitrans/
│   │   │   ├── MainActivity.kt  # Root activity rendering MainScreen with edge-to-edge
│   │   │   ├── data/            # Translation repository & data models
│   │   │   ├── ui/main/         # Jetpack Compose UI & MainScreenViewModel
│   │   │   └── ui/theme/        # Executive Material 3 Light Theme definition
│   │   └── AndroidManifest.xml
│   └── src/test/kotlin/         # Unit test suite verifying ViewModel & translation
├── nlp_investigation/
│   └── train_low_resource_mt.py # Ablation experiments (Baseline, BPE, Back-Translation, Transfer)
├── web/
│   └── index.html               # Responsive web companion app
├── gradle/
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 📜 License
MIT License. Free to use for research and educational purposes.
