package lt.vitalijus.records.app.widget

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.ui.res.stringResource
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Column
import lt.vitalijus.records.R
import lt.vitalijus.records.app.MainActivity
import lt.vitalijus.records.app.navigation.WIDGET_ACTION_CREATE_RECORD
import lt.vitalijus.records.app.navigation.WIDGET_BASE_PATH

class RecordWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget
        get() = RecordWidget()
}

class RecordWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val description = context.getString(R.string.record_new_echo)
        provideContent {
            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .clickable {
                            val intent = Intent(context, MainActivity::class.java).also {
                                it.data = "$WIDGET_BASE_PATH/true".toUri()
                                it.action = WIDGET_ACTION_CREATE_RECORD
                            }
                            val pendingIntent = TaskStackBuilder
                                .create(context)
                                .addNextIntentWithParentStack(intent)
                                .getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)

                            pendingIntent?.send()
                        }
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.widget),
                        contentDescription = description
                    )
                }
            }
        }
    }
}
