package cl.duoc.dsy1105.moodtracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.duoc.dsy1105.moodtracker.data.local.dao.MoodDao
import cl.duoc.dsy1105.moodtracker.data.local.dao.UserDao
import cl.duoc.dsy1105.moodtracker.data.local.entities.MoodEntry
import cl.duoc.dsy1105.moodtracker.data.local.entities.User

@Database(
    entities = [User::class, MoodEntry::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun moodDao(): MoodDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mood_tracker_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
