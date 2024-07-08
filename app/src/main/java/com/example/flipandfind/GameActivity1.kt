package com.example.flipandfind

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.utils.widget.ImageFilterButton
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.shuffle
import kotlin.concurrent.timer

/*
* Contributors:
*
* Kaashif
*   Cleaned up and refactored code
*   Created the main functionality of the game:
*       Button listeners
*       Changing tile image/background
*       Setting game category/mode based on selections in settings
*       Set up timer/counting flips
*       Implemented end of game (knowing when game over or completed)
*   Populated fruits list (before populateMutableList refactored)
*
* Mellat
*   Populated animals and shapes lists (before populateMutableList refactored)
*
* Jenna
*   Added mute button functionality
*   Implemented alert dialog for end of game pop up
*   refactored the PopulateMutableLists to be one small method instead of 3 long ones
*
* Hao
*   Implemented high score functionality
*       Different for each mode and category
*       Display high score
*       Add to end game pop up
*
* Saiz
*   Implemented text to speech for matches and game completed
*   Adapted 4x4 game activity into 3x4 and 4x5 activities
 */

class GameActivity1 : AppCompatActivity() {
    // Declaring TextToSpeech object as a class-level variable
    private lateinit var tts: TextToSpeech

    protected lateinit var buttons: List<ImageButton>   // List of buttons from the UI
    protected var mutableImages = mutableListOf<Int>()  // List of Images that we will shuffle based on the category
    protected var imagesMap = HashMap<Int, String>()    // Map of Image drawables and string names. Used to match images match & text-to-speech
    protected lateinit var firstButton: ImageButton     // Reference for the first clicked button
    protected lateinit var secondButton: ImageButton    // Reference for the second clicked button

    // Gamemode variables that we get from the settings activity
    protected lateinit var gameMode: String
    protected lateinit var category: String
    protected lateinit var sizeChosen: String

    protected var timer: Timer? = null      // Timer used to implement stop clock
    protected var isProcessing: Boolean = false     // Boolean to ignore user input / tile clicks

    // Game variables that keep count of the timer, moves and matches
    protected var seconds = 0
    protected var movesCounter = 0
    protected var matchesCount = 0
    protected val maxPairs = 6    // TODO: Change max matches in conditional based on grid size
    protected lateinit var displayText:TextView

    // Variables that keep track of and display the high score on the UI
    protected var highScore = 0
    protected lateinit var sp:SharedPreferences
    protected lateinit var highScoreTextView:TextView
    protected lateinit var backgroundHS: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game1)

        // Getting the intent from the previous activity and setting the game variables.
        gameMode = intent.getStringExtra("modeChosen").toString()
        category = intent.getStringExtra("categoryChosen").toString()
        sizeChosen = intent.getStringExtra("sizeChosen").toString()

        // Back Button -> Back to the settings activity
        val backButton = findViewById<ImageButton>(R.id.buttonBack)
        backButton.setOnClickListener{
            val backToSettings = Intent(this, Settings::class.java)
            timer?.cancel()
            timer = null
            startActivity(backToSettings)
        }

        // Accessing our singleton object that holds the media player
        if(!MediaPlayerObject.created){
            MediaPlayerObject.startMP(applicationContext, R.raw.backgroundmusic_2)
        }

        // Mute button
        val muteButton = findViewById<ImageFilterButton>(R.id.muteUnmuteBtn)

        // Setting the mute button image based on the current state of the media player
        if(MediaPlayerObject.mp.isPlaying){
            muteButton.setImageResource(R.drawable.baseline_music_note_24)  // muteButton image -> playing
        } else{
            muteButton.setImageResource(R.drawable.baseline_music_off_24)   // muteButton image -> muted
        }

        // Mute/Unmute button
        muteButton.setOnClickListener{
            if(!MediaPlayerObject.mp.isPlaying){
                MediaPlayerObject.startMP(applicationContext, R.raw.backgroundmusic_2)
                muteButton.setImageResource(R.drawable.baseline_music_note_24)
            } else{
                MediaPlayerObject.stopMP()
                muteButton.setImageResource(R.drawable.baseline_music_off_24)
            }
        }

        // TILE SETUP
        // Lists of possible items for each category
        val possibleAnimals = listOf("cat", "cheetah", "dog", "elephant", "giraffe", "gorilla",
            "kangaroo", "lion", "monkey", "panda", "penguin", "polarbear", "tiger", "whale", "zebra")
        val possibleShapes = listOf("circle", "diamond", "cylinder", "hexagon", "cube", "octagon",
            "parallelogram", "heart", "rectangle", "pyramid", "square", "star", "trapezoid", "triangle")
        val possibleFruits = listOf("apple", "avocado", "banana", "berry", "cherry", "grapes", "kiwi",
            "lemon", "mango", "orange", "pear", "pineapple", "pomegranate", "strawberry", "watermelon")

        var possibleItems: List<String>

        // Selecting the items list based on the category selected
        if (category.equals("Fruits")){
            possibleItems = possibleFruits
        } else if (category.equals("Animals")){
            possibleItems = possibleAnimals
        } else {
            possibleItems = possibleShapes
        }

        // Randomly selecting the pairs from possible items based on the grid size
        val selectedItems = possibleItems.asSequence().shuffled().take(maxPairs).toList()

        // Adding pairs of drawables to a list for each button. Also added to a hashmap to create a pair of drawable and string
        populateMutableList(selectedItems)

        // Shuffling the selected images/drawables list
        mutableImages.shuffle()

        // We use shared preferences to store and retrieve highscores for each game mode, category and grid size
        sp = getSharedPreferences("cache", MODE_PRIVATE)

        // Retrieving and displaying the high score
        highScore = sp.getInt(gameMode + category + sizeChosen, 0)
        val highScoreToDisplay = if (highScore == 0) "--" else highScore.toString()

        highScoreTextView = findViewById(R.id.highScoreTextView)
        highScoreTextView.text = "BEST SCORE: $highScoreToDisplay"

        // If the game mode is 'Practice', we hide the high score and its background from the UI
        if ("free" == gameMode){
            highScoreTextView.visibility = View.GONE
            backgroundHS = findViewById(R.id.highScore_background)
            backgroundHS.visibility = View.GONE
        }

        displayText = findViewById<TextView>(R.id.CounterTextView)  // TextView used to display the timer / moves counter

        // Starting timer or moves counter depending on the gameMode
        if (gameMode.equals("flips")){
            displayText.text = "Moves: " + movesCounter
        } else if (gameMode.equals("timed")){
            displayText.text = "Timer: 0 seconds"
            // Starting the timer after 5 seconds since we display the images/cards for 5 seconds
            Handler().postDelayed({
                // Timer Initialization
                timer = timer(period = 1000) {
                    seconds++
                    // Because the timer is running on a separate thread
                    runOnUiThread{
                        displayText.text = "Timer: " + seconds + " seconds"
                    }
                }
            }, 5000)
        }

        // Initializing TextToSpeech object
        tts = TextToSpeech(applicationContext, object : TextToSpeech.OnInitListener {
            override fun onInit(status: Int) {
                if (status == TextToSpeech.SUCCESS) {
                    // Setting the language for Text-to-Speech
                    tts.language = Locale.getDefault()
                }
            }
        })

        // Creating a list of tiles in the UI that we will map the drawables to
        buttons = listOf(
            findViewById(R.id.button1),
            findViewById(R.id.button2),
            findViewById(R.id.button3),
            findViewById(R.id.button4),
            findViewById(R.id.button5),
            findViewById(R.id.button6),
            findViewById(R.id.button7),
            findViewById(R.id.button8),
            findViewById(R.id.button9),
            findViewById(R.id.button10),
            findViewById(R.id.button11),
            findViewById(R.id.button12),
        )

        // Initializing / Resetting the buttons
        firstButton = ImageButton(this)
        secondButton = ImageButton(this)

        for (button in buttons) {
            isProcessing = true // Not accepting any user input since the round hasn't started yet
            val idx = buttons.indexOf(button) // Buttons index in the list


            // Game Start Logic
            // Showing all the images on the buttons so that the user can memorize them
            button.setImageResource(mutableImages[idx])
            button.setBackgroundResource(android.R.color.transparent)
            // Setting a tag to the imageButton that is also a key to the hashmap
            // Allows us to use the string represenation of the image to check for matches
            // Reduces the number of drawables we need to store and makes retrieving the string representation efficient
            button.setTag(imagesMap.get(mutableImages[idx]))

            // Hiding the buttons after 5 seconds
            Handler().postDelayed({
                button.setBackgroundResource(R.drawable.blanktile)
                button.setImageResource(android.R.color.transparent)
                isProcessing = false    // Allowing user input since the tiles are hidden now
            }, 5000)


            // Game In-Progress Logic
            // Setting up onClick listeners for each button
            button.setOnClickListener {
                val index = buttons.indexOf(button)
                onTileClick(buttons[index], index)
            }
        }
    }

    // Function that handles the logic when a tile is clicked
    protected fun onTileClick(button: ImageButton, index: Int){
        // If true, we return early, which basically disables/ignores all user input
        if (isProcessing) return

        // Updating moves counter everytime a button is clicked
        // Displaying it only if the gameMode is "flips"
        movesCounter++
        if (gameMode.equals("flips")){
            displayText.text = "Moves: " + movesCounter
        }

        // If button is ImageButton(this), then it is empty, and has an id of -1
        // If the first button reference is empty, then we set it to the button that was clicked
        if (firstButton.id == -1) {
            firstButton = button
            // Displaying the item on the button
            firstButton.setImageResource(mutableImages[index])
            firstButton.setBackgroundResource(android.R.color.transparent)
            firstButton.isEnabled = false   // Disabling that button so that the user cant click it again

        // First button isn't empty, then we set the secondReference to the button that was clicked
        } else {
            secondButton = button
            // Displaying the item on the button
            secondButton.setImageResource(mutableImages[index])
            secondButton.setBackgroundResource(android.R.color.transparent)

            isProcessing = true // Not accepting any user input until we process the 2 selected buttons

            // If both tiles match, we disable them and reset the references
            if (firstButton.getTag().equals(secondButton.getTag())) {

                val text = firstButton.tag.toString()
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)


                Toast.makeText(this, "Match Found!", Toast.LENGTH_SHORT).show()
                Handler().postDelayed({     // Adding delay to allow the user to process the information
                    // Disabling both buttons and leaving them face up with content/text
                    firstButton.isEnabled = false
                    secondButton.isEnabled = false
                    // Resetting the references
                    firstButton = ImageButton(this)
                    secondButton = ImageButton(this)
                    isProcessing = false    // Accepting user input again
                    matchesCount++
                    // Checking if the game is complete
                    checkIfGameComplete()
                }, 500)
            // If the tiles don't match, we enable them again and hide the content
            } else {
                Toast.makeText(this, "Tiles don't match!", Toast.LENGTH_SHORT).show()
                Handler().postDelayed({
                    // Hiding button content/text. Tiles face down
                    firstButton.isEnabled = true    // Enabling the button we disabled earlier
                    firstButton.setBackgroundResource(R.drawable.blanktile)
                    firstButton.setImageResource(android.R.color.transparent)
                    secondButton.setBackgroundResource(R.drawable.blanktile)
                    secondButton.setImageResource(android.R.color.transparent)
                    // Resetting the references
                    firstButton = ImageButton(this)
                    secondButton = ImageButton(this)
                    isProcessing = false
                }, 1300)
            }
        }
    }

    // Function that checks if the game is complete
    protected fun checkIfGameComplete(){
        if (matchesCount >= maxPairs){ // 8 pairs matched
            timer?.cancel()  // If the timer is active, pause it
            timer = null    // Stopping and changing timer to prevent memory leaks

            // Checking and saving highscore
            val isNewHighScore = saveHighScore()

            if (isNewHighScore) {
                // Retrieving the new high score and appending it to the end game message
                val newHighScore = sp.getInt(gameMode + category + sizeChosen, 0)
                endGame("Congratulations! You found all the matches.\nYour new best score: $newHighScore",
                        "Game Solved!")
            } else{
                // If the new score is not a high score, we just display the normal end game message
                endGame("Congratulations! You found all the matches.", "Game Solved!")
            }
        }
    }

    // Function that displays the end game popup
    // and forces the user to start a new game or go back to the main menu
    protected fun endGame(message: String = "", title: String = "Game Over!"){
        // not accepting any user input and disabling the tiles since the game is complete
        isProcessing = true
        for (button in buttons){
            button.isEnabled = false
        }

        // Building an alert box with the appropriate game over message
        // The popup will have 2 buttons, one to start a new game and one to go back to the main menu
        // The popup is not cancelable, so the user has to click one of the buttons
        // View for pop up is custom layout custom_endgame_dialog.xml
        val buildPopUp = AlertDialog.Builder(this)
        val endGameView = LayoutInflater.from(this).inflate(R.layout.custom_endgame_dialog, null)
        buildPopUp.setView(endGameView)
        val popUp : AlertDialog = buildPopUp.create()
        popUp.setCancelable(false)

        val titleText = endGameView.findViewById<TextView>(R.id.title_alert)
        titleText.text = title

        val messageText = endGameView.findViewById<TextView>(R.id.message_alert)
        messageText.text = message

        val newGameBtn = endGameView.findViewById<Button>(R.id.new_alert)
        newGameBtn.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }

        val menuBtn = endGameView.findViewById<Button>(R.id.menu_alert)
        menuBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        popUp.show()
    }

    // Function that saves the high score into shared preferences
    protected fun saveHighScore():Boolean{
        // Checking if the current score is a high score based on the game mode
        if (gameMode.equals("flips")){
            // If the score is a highscore, we save it and return true
            if(movesCounter < highScore || highScore == 0){
                sp.edit().putInt(gameMode + category + sizeChosen, movesCounter).commit()
                return true
            }
        } else if (gameMode.equals("timed")){
            // If the score is a highscore, we save it and return true
            if(seconds < highScore || highScore == 0){
                sp.edit().putInt(gameMode + category + sizeChosen, seconds).commit()
                return true
            }
        }
        return false
    }

    protected fun populateMutableList(selectedItems: List<String>){
        for (element in selectedItems){
            val item1 = element + "1"
            val item2 = element + "2"
            mutableImages.add(resources.getIdentifier(item1, "drawable", packageName))
            mutableImages.add(resources.getIdentifier(item2, "drawable", packageName))
            imagesMap.put(resources.getIdentifier(item1, "drawable", packageName), element)
            imagesMap.put(resources.getIdentifier(item2, "drawable", packageName), element)
        }
    }

}