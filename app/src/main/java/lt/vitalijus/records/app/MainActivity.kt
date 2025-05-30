package lt.vitalijus.records.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import lt.vitalijus.records.app.navigation.NavigationRoot
import lt.vitalijus.records.app.navigation.NavigationRoute
import lt.vitalijus.records.core.presentation.designsystem.theme.RecordsTheme
import lt.vitalijus.records.record.domain.recording.RecordingDetails
import lt.vitalijus.records.record.presentation.record.RecordsRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecordsTheme {
                NavigationRoot(
                    navController = rememberNavController()
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RecordsTheme {
        Greeting("Android")
    }
}
