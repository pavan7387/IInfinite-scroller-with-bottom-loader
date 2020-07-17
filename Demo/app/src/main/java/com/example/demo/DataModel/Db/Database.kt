package com.example.demo.DataModel.Db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.concurrent.atomic.AtomicInteger

class Database  //Constructor
private constructor() {
    private val context: Context? = null
    private val mOpenCounter = AtomicInteger()
    private var sqliteDatabase: SQLiteDatabase? = null

    /**
     * Open Database, return the SqliteDatabase Object.
     * This method increments the counter to track how many threads are accessing the database
     * @return sqliteDatabase - write database object
    */
    @Synchronized
    fun openDatabase(): SQLiteDatabase? {
        if (mOpenCounter.incrementAndGet() == 1) { // Opening new database
            sqliteDatabase = sqliteOpenHelper!!.writableDatabase
            //Enable Write Ahead Logging
            sqliteDatabase!!.enableWriteAheadLogging()
        }
        return sqliteDatabase
    }

    /**
     * CLose Database,
     * This method checks If any Thread is Accessing the Database. If no thread is accessing the database then it
     * close the database.
     */
    @Synchronized
    fun closeDatabase() {
        if (mOpenCounter.decrementAndGet() == 0) {
            if (sqliteDatabase != null) { // Closing database
                sqliteDatabase!!.close()
            }
        }
    }

    /*
     * Open Helper
     */
    private class OpenHelper internal constructor(context: Context?) :
        SQLiteOpenHelper(
            context,
            DATABASE_NAME,
            null,
            DATABASE_VERSION
        ) {
        override fun onOpen(sqLiteDatabase: SQLiteDatabase) {
            super.onOpen(sqLiteDatabase)
        }

        override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
            try {
                sqLiteDatabase.execSQL(CREATE_TABLE_PATIENT)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onUpgrade(
            sqLiteDatabase: SQLiteDatabase,
            oldVersion: Int,
            newVersion: Int
        ) {
        }

        private fun upgradeFromZerroToOne(sqLiteDatabase: SQLiteDatabase) {}
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private var databaseInstance: Database? = null
        private var sqliteOpenHelper: SQLiteOpenHelper? = null
        const val DATABASE_NAME = "GalleryData"
        /*Table*/
        const val TABLE_GALLERY = "table_gallery"
        /*Column  gallery*/
        const val COLUMN_ID = "column_id"
        const val COLUMN_FIRST_NAME ="column_first_name"
        const val COLUMN_IMAGE_ID = "column_image_id"
        const val COLUMN_IMAGE_URL = "column_image_url"
        const val COLUMN_LAST_NAME = "column_last_name"
        const val COLUMN_EMAIL = "column_email"

        /*Table gallery*/
        private const val CREATE_TABLE_PATIENT =
            ("CREATE TABLE IF NOT EXISTS " + TABLE_GALLERY
                    + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_IMAGE_ID + " TEXT, "
                    + COLUMN_FIRST_NAME + " TEXT, "
                    + COLUMN_LAST_NAME + " TEXT, "
                    + COLUMN_EMAIL + " TEXT, "
                    + COLUMN_IMAGE_URL + " TEXT "
                    + ")")

        /**
         * Singleton SqLite Object Approach
         *Initialize Instance, Singleton Factory Method. Call this method from once to initialize the "Database"
         * and "SqliteOpenHelper" instance throughout the application. If you subclass the Application Class, you can call
         * it from there.
         *
         * @param context
         */
        @JvmStatic
        @Synchronized
        fun initializeInstance(context: Context?) {
            if (databaseInstance == null) {
                databaseInstance = Database()
                sqliteOpenHelper = OpenHelper(context)
            }
        }

        /**
         * Synchronized method to get the Singleton instance of TestDatabase
         *
         * @return databaseInstance
         */
        @get:Synchronized
        val instance: Database?
            get() {
                checkNotNull(databaseInstance) { Database::class.java.simpleName + " is not initialized, call initializeInstance(..) method first." }
                return databaseInstance
            }
    }
}