package lt.vitalijus.records.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import lt.vitalijus.records.record.presentation.record.RecordsRoot
import lt.vitalijus.records.record.presentation.util.toCreateRecordRoute

@Composable
fun NavigationRoot(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Records,
    ) {
        composable<NavigationRoute.Records> {
            RecordsRoot(
                onNavigateToCreateRecord = { recordingDetails ->
                    val createRecordScreenRoute = recordingDetails.toCreateRecordRoute()
                    navController.navigate(createRecordScreenRoute)
                }
            )
        }

        composable<NavigationRoute.CreateRecord> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Create Record Screen")
            }
        }
    }
}
