package com.br444n.constructionmaterialtrack.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.br444n.constructionmaterialtrack.data.local.dao.MaterialDao
import com.br444n.constructionmaterialtrack.data.local.dao.ProjectDao
import com.br444n.constructionmaterialtrack.data.local.entity.MaterialEntity
import com.br444n.constructionmaterialtrack.data.local.entity.ProjectEntity

@Database(
    entities = [ProjectEntity::class, MaterialEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ConstructionDatabase : RoomDatabase() {
    
    abstract fun projectDao(): ProjectDao
    abstract fun materialDao(): MaterialDao
    
    companion object {
        @Volatile
        private var INSTANCE: ConstructionDatabase? = null
        
        fun getDatabase(context: Context): ConstructionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ConstructionDatabase::class.java,
                    "construction_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}