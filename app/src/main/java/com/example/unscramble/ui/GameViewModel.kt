package com.example.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel: ViewModel(){

    private val _uiState = MutableStateFlow(GameUiState())

    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private lateinit var currentWord: String
    private var usedWords: MutableSet<String> = mutableSetOf()

    var userGuess by mutableStateOf("")
        private set

    init{
        resetGame()
    }

    //Reset Game
    fun resetGame(){
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
    }

    //shuffle current Word
    private fun shuffleCurrentWord(word: String): String{
        val tempWord = word.toCharArray()
        tempWord.shuffle()
        while (String(tempWord) == word){
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    // update user guess
    fun updateUserGuess(guessedWord: String){
        userGuess = guessedWord
    }

    // check user guess
    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {
            val updatedScore = _uiState.value.score + SCORE_INCREASE
            val isGameWon = isGameOver()
            updateUserGuess("") // Clear the user's guess

            // Display the next word if the game is not won
            if (!isGameWon) {
                val nextWord = pickRandomWordAndShuffle()
                _uiState.update { currentState ->
                    currentState.copy(
                        currentScrambledWord = nextWord,
                        currentWordCount = currentState.currentWordCount + 1,
                        isGuessedWordWrong = false,
                        score = updatedScore
                    )
                }
            } else {
                // If the game is won, update the state accordingly
                _uiState.update { currentState ->
                    currentState.copy(
                        isGuessedWordWrong = false,
                        score = updatedScore,
                        isGameOver = true
                    )
                }
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }
    }

    private fun isGameOver(): Boolean {
        return usedWords.size >= MAX_NO_OF_WORDS
    }


    // update game state
private fun updateGameState(updatedScore: Int) {
        // Last round in the game, Update Game over here
        if (usedWords.size == MAX_NO_OF_WORDS){
            //Last round in the game, update isGameOver to true, don't pick a new word
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true
                )
            }
        }
        else {
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentScrambledWord = pickRandomWordAndShuffle(),
                    currentWordCount = currentState.currentWordCount.inc(),
                    score = updatedScore
                    )
            }
        }

}

    // pick and shuffle word
private fun pickRandomWordAndShuffle(): String {
    // Continue picking up a new random word until you get one that hasn't been used before
    currentWord = allWords.random()
    return if (usedWords.contains(currentWord)) {
        pickRandomWordAndShuffle()
    } else {
        usedWords.add(currentWord)
        shuffleCurrentWord(currentWord)
    }
}

    fun skipWord() {
        updateGameState(_uiState.value.score)
        updateUserGuess("")
    }
}

