package com.br444n.constructionmaterialtrack.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.br444n.constructionmaterialtrack.data.local.dao.MaterialDao
import com.br444n.constructionmaterialtrack.data.local.dao.ProjectDao
import com.br444n.constructionmaterialtrack.data.local.entity.MaterialEntity
import com.br444n.constructionmaterialtrack.data.local.entity.ProjectEntity

@Database(
    entities = [ProjectEntity::class, MaterialEntity::class],
    version = 2,
    exportSchema = false
)
abstract class ConstructionDatabase : RoomDatabase() {
    
    abstract fun projectDao(): ProjectDao
    abstract fun materialDao(): MaterialDao
    
    companion object {
        @Volatile
        private var INSTANCE: ConstructionDatabase? = null
        
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add unit column to materials table with default value
                db.execSQL("ALTER TABLE materials ADD COLUMN unit TEXT NOT NULL DEFAULT 'pcs'")
            }
        }
        
        fun getDatabase(context: Context): ConstructionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ConstructionDatabase::class.java,
                    "construction_database"
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}