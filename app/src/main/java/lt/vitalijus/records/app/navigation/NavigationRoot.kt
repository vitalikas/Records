package lt.vitalijus.records.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import lt.vitalijus.records.record.presentation.create_record.CreateRecordRoot
import lt.vitalijus.records.record.presentation.records.RecordsRoot
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
            CreateRecordRoot(
                onConfirmLeave = navController::navigateUp
            )
        }
    }
}
