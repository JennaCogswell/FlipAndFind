//Espresso Testing performed by Saiz

package com.example.flipandfind

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class SettingsTest {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(Settings::class.java)


    @Test
    fun visibilityTest(){

        onView(withId(R.id.choose_category)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.choose_gridsize)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.choose_mode)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.button)).check(ViewAssertions.matches(isDisplayed()))

        onView(withId(R.id.muteBtn)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.buttonMain)).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun clickTest(){

        onView(withId(R.id.animal_category)).perform(click())
        onView(withId(R.id.fruits_category)).perform(click())
        onView(withId(R.id.shapes_category)).perform(click())

        onView(withId(R.id.size_34)).perform(click())
        onView(withId(R.id.size_44)).perform(click())
        onView(withId(R.id.size_45)).perform(click())

        onView(withId(R.id.timed_mode)).perform(click())
        onView(withId(R.id.free_mode)).perform(click())
        onView(withId(R.id.choose_mode)).perform(click())
    }


    @Test
    fun muteButtonTest(){

        onView(withId(R.id.muteBtn)).perform(click())
        assertFalse(MediaPlayerObject.mp.isPlaying)

        onView(withId(R.id.muteBtn)).perform(click())
        assertTrue(MediaPlayerObject.mp.isPlaying)

    }

    @Test
    fun backButtonTest(){

        onView(withId(R.id.buttonMain)).perform(click())
        // Check that the Main activity 1 is displayed
        onView(withId(R.id.main_Activity)).check(ViewAssertions.matches(isDisplayed()))

        onView(withId(R.id.start_button)).perform(click())
        // Check that the Settings activity 1 is displayed
        onView(withId(R.id.settings_Activity)).check(ViewAssertions.matches(isDisplayed()))

    }


    @Test
    fun startButtonTest() {

        onView(withId(R.id.fruits_category)).perform(click())
        onView(withId(R.id.size_34)).perform(click())
        onView(withId(R.id.moves_mode)).perform(click())
        onView(withId(R.id.button)).perform(click())

        // Check that the Game activity 1 is displayed
        onView(withId(R.id.constraintLayout)).check(ViewAssertions.matches(isDisplayed()))
    }




}