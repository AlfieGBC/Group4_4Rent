package com.pp.a4rent.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.firestore
import com.pp.a4rent.models.Blog

class BlogRepository(
    private val context: Context
) {
    private val TAG = "BLOG"
    private val db = Firebase.firestore

    private val COLLECTION_BLOGS = "Blogs"
    private val FIELD_TITLE = "title"
    private val FIELD_DESCRIPTION = "description"
    private val FIELD_IMAGE = "image"

    var allBlogs : MutableLiveData<List<Blog>> = MutableLiveData<List<Blog>>()

    fun retrieveAllBlogs() {
        try {
            db.collection(COLLECTION_BLOGS)
                .addSnapshotListener(EventListener { result, error ->
                    if (error != null) {
                        Log.d(TAG, "retrieveAllBlogs: Listening to Blogs collection failed due to error : $error")
                        return@EventListener
                    }

                    if (result != null) {
                        Log.d(TAG, "retrieveAllBlogs: Number of documents retrieved: ${result.size()}")

                        val tempList: MutableList<Blog> = ArrayList<Blog>()

                        for (docChanges in result.documentChanges) {

                            val currentDocument : Blog = docChanges.document.toObject(Blog::class.java)
                            currentDocument.blogID = docChanges.document.id
                            Log.d(TAG, "retrieveAllBlogs: currentDocument : $currentDocument ")

                            when(docChanges.type) {
                                DocumentChange.Type.ADDED -> {
                                    tempList.add(currentDocument)
                                }
                                DocumentChange.Type.MODIFIED -> {
                                    tempList.add(currentDocument)
                                }
                                DocumentChange.Type.REMOVED -> {

                                }
                            }
                        } // for

                        Log.d(TAG, "retrieveAllBlogs: tempList : $tempList")
                        // replace the value in allBLogs
                        allBlogs.postValue(tempList)

                    } else {
                        Log.d(TAG, "retrieveAllBlogs: No data in the result after retrieving")
                    }
                })
        } catch (ex : java.lang.Exception) {
            Log.d(TAG, "retrieveAllBlogs: Unable to retrieve all blogs : $ex")
        }
    }

}