package com.example.lexitrans.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** UI tests for [com.example.lexitrans.ui.main.MainScreenContent]. */
class MainScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() {
    composeTestRule.setContent {
      MainScreenContent(
        uiState = MainUiState(),
        onTabSelected = {},
        onSourceTextChanged = {},
        onDirectionSelected = {},
        onSwapDirection = {},
        onTechniqueSelected = {},
        onPresetSelected = {},
        onTranslateClicked = {},
        onSelectComparativeSample = {},
        onSelectExperiment = {},
        onBpeWordChanged = {},
        onCorpusFilterChanged = {},
        onCorpusSearchChanged = {},
        onLexiconSearchChanged = {}
      )
    }
  }

  @Test
  fun appTitle_exists() {
    composeTestRule.onNodeWithText("LexiTrans").assertExists()
  }
}
