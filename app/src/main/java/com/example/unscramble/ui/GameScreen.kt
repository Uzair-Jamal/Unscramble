import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unscramble.R
import com.example.unscramble.ui.GameViewModel
import com.example.unscramble.ui.theme.UnscrambleTheme

@Composable
fun GameScreen(
    gameViewModel: GameViewModel = viewModel()
){
    val mediumPadding = dimensionResource(id = R.dimen.padding_medium)
    val gameUiState by gameViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(mediumPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.titleLarge
            )
        GameLayout(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(mediumPadding),
            onUserGuessChanged = {gameViewModel.updateUserGuess(it)},
            onKeyboardDone = {gameViewModel.checkUserGuess()},
            currentScrambledWord = gameUiState.currentScrambledWord,
            userGuess = gameViewModel.userGuess,
            isGuessWrong = gameUiState.isGuessedWordWrong,
            correctWord = gameUiState.correctWord,
            wordCount = gameUiState.currentWordCount
            )
        Column(
            verticalArrangement = Arrangement.spacedBy(mediumPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(mediumPadding)
                .fillMaxWidth()
        ){
            Button(modifier = Modifier
                .fillMaxWidth(),
                onClick = {
                    gameViewModel.checkUserGuess()
                }
            ){
                Text(
                    text = stringResource(id = R.string.submit),
                    fontSize = 16.sp
                    )
            }

            OutlinedButton(modifier = Modifier
                .fillMaxWidth(),
                onClick = {gameViewModel.skipWord()}
                ){
                Text(
                    text = stringResource(R.string.skip),
                    fontSize = 16.sp
                )
            }
        }
            GameStatus(score = gameUiState.score , modifier = Modifier.padding(20.dp))
    }
    if(gameUiState.isGameOver){
        FinalScoreDialog(
            score = gameUiState.score,
            onPlayAgain = { gameViewModel.resetGame() }
        )
    }
}

@Composable
fun GameLayout(
    modifier: Modifier = Modifier,
    currentScrambledWord: String,
    onUserGuessChanged: (String) -> Unit,
    onKeyboardDone: () -> Unit,
    isGuessWrong: Boolean,
    userGuess: String,
    wordCount: Int,
    correctWord: String // Added parameter for correct word
) {
    val mediumPadding = dimensionResource(id = R.dimen.padding_medium)
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(mediumPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(mediumPadding)
        ) {
            Text(
                text = stringResource(id = R.string.word_count, wordCount),
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onPrimary,
                modifier = Modifier
                    .clip(shapes.medium)
                    .background(colorScheme.surfaceTint)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .align(alignment = Alignment.End)
            )
            Text(
                text = currentScrambledWord,
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = R.string.instructions),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = userGuess,
                onValueChange = onUserGuessChanged,
                singleLine = true,
                isError = isGuessWrong,
                label = {
                    if (isGuessWrong) {
                        Text(stringResource(R.string.wrong_guess))
                    } else {
                        Text(stringResource(id = R.string.enter_your_word))
                    }
                },
                colors = TextFieldDefaults.textFieldColors(containerColor = colorScheme.surface),
                shape = shapes.large,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onKeyboardDone() }),
                modifier = Modifier.fillMaxWidth()
            )
            if (correctWord.isNotEmpty()) {
                Text(
                    text = "Right Guess: $correctWord", // Display correct word
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun GameStatus(score: Int, modifier: Modifier = Modifier){
    Card(
        modifier = Modifier
    ){
        Text(
            text = stringResource(id = R.string.score, score),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(8.dp)
            )
    }
}


@Composable
private fun FinalScoreDialog(
    score: Int,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activity = (LocalContext.current as Activity)
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(text = stringResource(id = R.string.congratulations))
        },
        text = {Text(text = stringResource(id = R.string.score,score))
        },
        dismissButton = {
            TextButton(
                    onClick = {
                        activity.finish()
                    }
            ){
                Text(text = stringResource(id = R.string.exit))
            }
        },
        confirmButton = {
            TextButton(
                onClick = onPlayAgain
            ){
                Text(text = stringResource(id = R.string.play_again))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    UnscrambleTheme {
        GameScreen()
    }
}