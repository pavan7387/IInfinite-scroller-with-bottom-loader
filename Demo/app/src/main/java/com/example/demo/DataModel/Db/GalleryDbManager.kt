package com.example.demo.DataModel.Db

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.demo.Model.Data
import com.example.demo.Model.ModelPage
import java.util.zip.ZipException

class GalleryDbManager {
    /**
     * add patient item in database table
     * @param modelPatient list of stickers
     * @return
     * @throws ZipException exception
     */
    @Throws(ZipException::class)
    fun addGalleryData(modelGallery: Data): Int {
        var sqLiteDatabase: SQLiteDatabase? = null
        var database: Database? = null
        var result: Int = 0

        try {
            database = Database.instance
            sqLiteDatabase = database!!.openDatabase()
                beginTransaction(sqLiteDatabase)
                val values = ContentValues()
                values.put(Database.COLUMN_IMAGE_ID, modelGallery.id)
                values.put(Database.COLUMN_IMAGE_URL, modelGallery.avatar)
                values.put(Database.COLUMN_EMAIL, modelGallery.email)
                values.put(Database.COLUMN_FIRST_NAME, modelGallery.firstName)
                values.put(Database.COLUMN_LAST_NAME, modelGallery.lastName)


                if (doesImageIdExist(modelGallery.id.toString(), sqLiteDatabase!!, Database.TABLE_GALLERY) ) {
                    val whereClause: String =Database.COLUMN_IMAGE_ID.toString() + " = ?"
                    val whereClauseArgs = arrayOf<String>(java.lang.String.valueOf(modelGallery.id))
                    result =   sqLiteDatabase!!.update(Database.TABLE_GALLERY, values,whereClause,whereClauseArgs )
                } else {
                    result =    sqLiteDatabase!!.insert(Database.TABLE_GALLERY, null, values).toInt()
                }
                setTransactionSuccessful(sqLiteDatabase)

        } catch (exception: Exception) {
            Log.d("databaseexception",exception.toString())
        } finally {
            endTransaction(sqLiteDatabase)
            database!!.closeDatabase()
        }
        return result
    }


    /**
     * check the media already added or not
     * @param id
     * @param sqliteDatabase Sqlite instance
     * @param tableName database table name
     * @return boolean if sticker exist or not
     * @throws ZipException
     */
    @Throws(ZipException::class)
    private fun doesImageIdExist( id: String,sqliteDatabase: SQLiteDatabase, tableName: String ): Boolean {
        var isMediaExist = false
        var count = 0
        var cursor: Cursor? = null
        try {
            cursor = sqliteDatabase.query(tableName,arrayOf(Database.COLUMN_IMAGE_ID), Database.COLUMN_IMAGE_ID.toString() + " = ?",
                arrayOf(id.toString()),null,null,null)
            if (cursor != null) {
                count = cursor.count
                if (count > 0) {
                    isMediaExist = true
                }
            }
        } catch (exception: java.lang.Exception) {
            Log.d("databaseexception",exception.toString())

        } finally {
            closeCursor(cursor)
        }
        return isMediaExist
    }

    /***
     * Close cursor
     * @param cursor
     */
    fun closeCursor(cursor: Cursor?) {
        if (cursor != null && !cursor.isClosed) {
            try {
                cursor.close()
            } catch (e: java.lang.Exception) { //Do Nothing
                e.printStackTrace()
            }
        }
    }


    fun endTransaction(sqliteDatabase: SQLiteDatabase?) {
        try {
            sqliteDatabase?.endTransaction()
        } catch (exception: java.lang.Exception) { //Consume
        }
    }

    /**
     * Sql Transaction Methods
     */
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    fun beginTransaction(sqliteDatabase: SQLiteDatabase?) {
        try {
            sqliteDatabase?.beginTransactionNonExclusive()
        } catch (exception: java.lang.Exception) { //Consume
        }
    }

    fun setTransactionSuccessful(sqliteDatabase: SQLiteDatabase?) {
        try {
            sqliteDatabase?.setTransactionSuccessful()
        } catch (exception: java.lang.Exception) { //Consume
        }
    }

    fun getImageData(): ArrayList<Data> {
        var sqLiteDatabase: SQLiteDatabase? = null
        var database: Database? = null
        var cursorMedia: Cursor? = null
        val gallery :ArrayList<Data> = ArrayList()
        try {
            database = Database.instance
            sqLiteDatabase = database!!.openDatabase()
            val query = " SELECT *"  + " FROM " + Database.TABLE_GALLERY
            cursorMedia = sqLiteDatabase?.rawQuery(query,null)
            if (cursorMedia != null && cursorMedia.moveToFirst()){
                do {
                    val media = Data()
                    media.id =cursorMedia.getInt(cursorMedia.getColumnIndex(Database.COLUMN_IMAGE_ID))
                    media.firstName =cursorMedia.getString(cursorMedia.getColumnIndex(Database.COLUMN_FIRST_NAME))
                    media.lastName =cursorMedia.getString(cursorMedia.getColumnIndex(Database.COLUMN_LAST_NAME))
                    media.email =cursorMedia.getString(cursorMedia.getColumnIndex(Database.COLUMN_EMAIL))
                    media.avatar =cursorMedia.getString(cursorMedia.getColumnIndex(Database.COLUMN_IMAGE_URL))
                    gallery.add(media)
                }while (cursorMedia.moveToNext())
                closeCursor(cursorMedia)
            }
        } catch (exception: Exception) {
            Log.d("databaseexception",exception.toString())
        } finally {
            endTransaction(sqLiteDatabase)
            database!!.closeDatabase()
        }
        return gallery

    }
}