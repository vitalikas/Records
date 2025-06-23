package lt.vitalijus.records.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import lt.vitalijus.records.record.presentation.create_record.CreateRecordRoot
import lt.vitalijus.records.record.presentation.records.RecordsRoot
import lt.vitalijus.records.record.presentation.settings.SettingsRoot
import lt.vitalijus.records.record.presentation.util.toCreateRecordRoute

const val WIDGET_BASE_PATH = "https://records.com/records"
const val WIDGET_ACTION_CREATE_RECORD = "lt.vitalijus.CREATE_RECORD"

@Composable
fun NavigationRoot(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Records(startRecording = false),
    ) {
        composable<NavigationRoute.Records>(
            deepLinks = listOf(
                navDeepLink<NavigationRoute.Records>(
                    basePath = WIDGET_BASE_PATH
                ) {
                    action = WIDGET_ACTION_CREATE_RECORD
                }
            )
        ) {
            RecordsRoot(
                onNavigateToCreateRecord = { recordingDetails ->
                    val createRecordScreenRoute = recordingDetails.toCreateRecordRoute()
                    navController.navigate(createRecordScreenRoute)
                },
                onNavigateToSettings = {
                    navController.navigate(NavigationRoute.Settings)
                }
            )
        }

        composable<NavigationRoute.CreateRecord> {
            CreateRecordRoot(
                onConfirmLeave = navController::navigateUp
            )
        }

        composable<NavigationRoute.Settings> {
            SettingsRoot(
                onGoBack = navController::navigateUp
            )
        }
    }
}
