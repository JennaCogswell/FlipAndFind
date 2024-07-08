//Espresso Testing performed by Saiz

package com.example.flipandfind

import android.widget.ImageButton
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import org.hamcrest.CoreMatchers.not
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
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
class Game3Test {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(GameActivity3::class.java)


    @Test
    fun visibilityTest(){

        onView(withId(R.id.button1)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.button2)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.button3)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.button4)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.button5)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.button6)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.button7)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.button8)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.button9)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.button10)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.button11)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.button12)).check(ViewAssertions.matches(isDisplayed()))

        onView(withId(R.id.muteUnmuteBtn)).check(ViewAssertions.matches(isDisplayed()))

        onView(withId(R.id.buttonBack)).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun clickTest(){

        Thread.sleep(5000)

        onView(withId(R.id.button1)).perform(click())
        onView(withId(R.id.button2)).perform(click())
        onView(withId(R.id.button3)).perform(click())
        onView(withId(R.id.button4)).perform(click())
        onView(withId(R.id.button5)).perform(click())
        onView(withId(R.id.button6)).perform(click())
        onView(withId(R.id.button7)).perform(click())
        onView(withId(R.id.button8)).perform(click())
        onView(withId(R.id.button9)).perform(click())
        onView(withId(R.id.button10)).perform(click())
        onView(withId(R.id.button11)).perform(click())
        onView(withId(R.id.button12)).perform(click())
        onView(withId(R.id.button13)).perform(click())
        onView(withId(R.id.button14)).perform(click())
        onView(withId(R.id.button15)).perform(click())
        onView(withId(R.id.button16)).perform(click())
        onView(withId(R.id.button17)).perform(click())
        onView(withId(R.id.button18)).perform(click())
        onView(withId(R.id.button19)).perform(click())
        onView(withId(R.id.button20)).perform(click())
    }


    @Test
    fun muteButtonTest(){

        onView(withId(R.id.muteUnmuteBtn)).perform(click())
        assertFalse(MediaPlayerObject.mp.isPlaying)

        onView(withId(R.id.muteUnmuteBtn)).perform(click())
        assertTrue(MediaPlayerObject.mp.isPlaying)

    }

    @Test
    fun backButtonTest(){

        onView(withId(R.id.buttonBack)).perform(click())
        // Check that the Setting activity 1 is displayed
        onView(withId(R.id.settings_Activity)).check(ViewAssertions.matches(isDisplayed()))

        onView(withId(R.id.fruits_category)).perform(click())
        onView(withId(R.id.size_45)).perform(click())
        onView(withId(R.id.moves_mode)).perform(click())
        onView(withId(R.id.button)).perform(click())

        // Check that the Game activity 1 is displayed
        onView(withId(R.id.constraintLayout)).check(ViewAssertions.matches(isDisplayed()))
    }


}