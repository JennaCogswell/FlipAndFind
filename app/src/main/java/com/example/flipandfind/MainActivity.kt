package com.example.flipandfind

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.utils.widget.ImageFilterButton

/*
* Contributors:
*
* Kaashif
*   cleaned up code
*
* Jenna
*   created media player singleton and updated the functionality of mute button
*
* Mellat
*   created original mediaplayer functionality
*
 */

// Creating a singleton object to hold the media player
// This allows the media player to play/pause the background music from within any activity
object MediaPlayerObject {

    lateinit var mp: MediaPlayer
    var created = false

    fun startMP(applicationContext: Context, song: Int){
        mp = MediaPlayer.create(applicationContext, song)
        created=true
        mp.setVolume(0.1f, 0.1f)
        mp.isLooping = true
        mp.start()
    }

    fun stopMP(){
        mp.stop()
        mp.reset()
    }

}
class MainActivity : AppCompatActivity() {
    protected lateinit var mediaPlayer: MediaPlayerObject
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Mute button
        val muteButton = findViewById<ImageFilterButton>(R.id.mute_button)

        if(!MediaPlayerObject.created){
            MediaPlayerObject.startMP(applicationContext, R.raw.backgroundmusic_2)
        }

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

        // Once the hint button is clicked, there will be an alert dialog displayed, telling the user how to play the game.
        // Custom view layout for pop up custom_hint_dialog.xml
        val hintBtn = findViewById<ImageFilterButton>(R.id.hint_button)
        hintBtn.setOnClickListener {
            val buildPopUp = AlertDialog.Builder(this)
            val hintView = LayoutInflater.from(this).inflate(R.layout.custom_hint_dialog, null)
            buildPopUp.setView(hintView)
            val popUp : AlertDialog = buildPopUp.create()

            val okayBtn = hintView.findViewById<Button>(R.id.btn_dialog)
            okayBtn.setOnClickListener {
                popUp.dismiss()
            }

            popUp.show()
        }

        // Start button that takes you to the settings page
        val startButton = findViewById<Button>(R.id.start_button);
        startButton.setOnClickListener {
            val startGame = Intent(this, Settings::class.java)
            startActivity(startGame)
        }

    }
}