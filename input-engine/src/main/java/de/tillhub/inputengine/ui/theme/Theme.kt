import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import de.tillhub.inputengine.ui.theme.typography

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        typography = typography,
        content = content
    )
}