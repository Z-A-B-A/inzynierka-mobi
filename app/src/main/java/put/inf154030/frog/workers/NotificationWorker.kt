package put.inf154030.frog.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class NotificationWorker (
    private val appContext: Context,
    private val params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        delay(10000)
        Log.d("Notification Worker", "Success")
        return Result.success()
    }
}