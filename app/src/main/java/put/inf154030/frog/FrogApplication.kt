package put.inf154030.frog

import android.app.Application
import put.inf154030.frog.network.SessionManager

class FrogApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionManager.init(applicationContext)
    }
}