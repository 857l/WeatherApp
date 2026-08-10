package ru.n857l.weatherapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement

class FindCityPage(private val composeTestRule: ComposeContentTestRule) {

    private val inputField = composeTestRule.onNodeWithTag("findCityInputField")
    private val noConnectionError = composeTestRule.onNodeWithTag("noInternetConnection")
    private val retryButton = composeTestRule.onNodeWithTag("retryButton")
    private val loading = composeTestRule.onNodeWithTag("CircleLoading")

    fun input(text: String) {
        inputField.performTextReplacement(text)
    }

    fun assertCityFound(cityName: String) {
        composeTestRule.onNodeWithText(cityName).assertIsDisplayed()
    }

    fun clickFoundCity(cityName: String) {
        composeTestRule.onNodeWithText(cityName).performClick()
    }

    fun assertNoConnectionIsDisplayed() {
        noConnectionError.assertIsDisplayed()
    }

    fun clickRetry() {
        retryButton.performClick()
    }

    fun assertEmptyResult() {
        noConnectionError.assertDoesNotExist()
        retryButton.assertDoesNotExist()
    }

    fun assertLoading() {
        loading.assertIsDisplayed()
    }
}