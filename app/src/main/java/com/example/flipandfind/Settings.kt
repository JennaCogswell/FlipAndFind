package com.example.flipandfind

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.utils.widget.ImageFilterButton
import com.example.flipandfind.MediaPlayerObject

/*
* Contributors:
*
* Jenna
*   radio buttons functionality
*   start game button listener
*   media player mute button
*   back button
*
* Saiz
*   choose which game activity to start depending on grid size chosen
*
 */
class Settings : AppCompatActivity() {

    // Arrays containing options for the radio groups
    private val categories = arrayOf("Animals", "Fruits", "Shapes");
    private val gridSizes = arrayOf("3x4", "4x4", "4x5");
    private val modes = arrayOf("timed", "flips", "free");

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Radio groups for the game options
        val rgCategories = findViewById<RadioGroup>(R.id.choose_category)
        val rgSize = findViewById<RadioGroup>(R.id.choose_gridsize)
        val rgMode = findViewById<RadioGroup>(R.id.choose_mode)

        // Start button that starts the game
        val startButton = findViewById<Button>(R.id.button);
        startButton.setOnClickListener{

            // Getting the category chosen from the radio group
            val category = when(rgCategories!!.checkedRadioButtonId){
                R.id.animal_category -> categories[0]
                R.id.fruits_category -> categories[1]
                else -> categories[2]
            }

            // Getting the grid size chosen from the radio group
            val size = when(rgSize!!.checkedRadioButtonId){
                R.id.size_34 -> gridSizes[0]
                R.id.size_44 -> gridSizes[1]
                else -> gridSizes[2]
            }

            // Getting the mode chosen from the radio group
            val limitMode = when(rgMode!!.checkedRadioButtonId){
                R.id.timed_mode -> modes[0]
                R.id.moves_mode -> modes[1]
                else -> modes[2]
            }


            if (size.equals(gridSizes[0])) {
                val startGame = Intent(this, GameActivity1::class.java)
                startGame.putExtra("categoryChosen", category)
                startGame.putExtra("sizeChosen", size)
                startGame.putExtra("modeChosen", limitMode)
                startActivity(startGame)
            } else if (size.equals(gridSizes[1])) {
                val startGame = Intent(this, GameActivity2::class.java)
                startGame.putExtra("categoryChosen", category)
                startGame.putExtra("sizeChosen", size)
                startGame.putExtra("modeChosen", limitMode)
                startActivity(startGame)
            } else {
                val startGame = Intent(this, GameActivity3::class.java)
                startGame.putExtra("categoryChosen", category)
                startGame.putExtra("sizeChosen", size)
                startGame.putExtra("modeChosen", limitMode)
                startActivity(startGame)
            }

        }

        // Back to main menu button
        val menuBtn = findViewById<ImageButton>(R.id.buttonMain)
        menuBtn.setOnClickListener{
            val menuIntent = Intent(this, MainActivity::class.java)
            startActivity(menuIntent)
        }

        // Accessing our singleton object that holds the media player
        if(!MediaPlayerObject.created){
            MediaPlayerObject.startMP(applicationContext, R.raw.backgroundmusic_2)
        }

        // Mute button
        val muteButton = findViewById<ImageFilterButton>(R.id.muteBtn)

        // Setting the mute button image based on the current state of the media player
        if(MediaPlayerObject.mp.isPlaying){
            muteButton.setImageResource(R.drawable.baseline_music_note_24)  // muteButton image -> playing
        } else{
            muteButton.setImageResource(R.drawable.baseline_music_off_24)   // muteButton image -> muted
        }

        // Looking for Mute/Unmute input
        muteButton.setOnClickListener{
            if(!MediaPlayerObject.mp.isPlaying){
                MediaPlayerObject.startMP(applicationContext, R.raw.backgroundmusic_2)
                muteButton.setImageResource(R.drawable.baseline_music_note_24)
            } else{
                MediaPlayerObject.stopMP()
                muteButton.setImageResource(R.drawable.baseline_music_off_24)
            }
        }
    }

}