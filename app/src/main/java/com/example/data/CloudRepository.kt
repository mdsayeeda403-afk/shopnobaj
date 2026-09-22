package com.example.data

import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CloudRepository {
    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w("CloudRepository", "Firebase not yet initialized or configured: ${e.message}")
            null
        }
    }

    val isCloudAvailable: Boolean
        get() = firestore != null

    /**
     * Real-time stream of all stories published across users
     */
    fun getStoriesFlow(): Flow<List<Story>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = db.collection("stories")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("CloudRepository", "Error fetching stories: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val stories = snapshot.documents.mapNotNull { doc ->
                        try {
                            val id = doc.id
                            val title = doc.getString("title") ?: ""
                            val authorName = doc.getString("authorName") ?: ""
                            val authorHandle = doc.getString("authorHandle") ?: "@anonymous"
                            val authorBio = doc.getString("authorBio") ?: ""
                            val typeStr = doc.getString("type") ?: "গল্প"
                            val type = when (typeStr) {
                                "কবিতা" -> StoryType.KOBITA
                                "উপন্যাস" -> StoryType.UPONNAS
                                else -> StoryType.GOLPO
                            }
                            val genre = doc.getString("genre") ?: "সামাজিক"
                            val excerpt = doc.getString("excerpt") ?: ""
                            val fullContent = doc.getString("fullContent") ?: ""
                            val likesCount = (doc.getLong("likesCount") ?: 0L).toInt()
                            val lovesCount = (doc.getLong("lovesCount") ?: 0L).toInt()
                            val firesCount = (doc.getLong("firesCount") ?: 0L).toInt()
                            val commentsCount = (doc.getLong("commentsCount") ?: 0L).toInt()
                            val readTimeMinutes = (doc.getLong("readTimeMinutes") ?: 3L).toInt()
                            val publishedDate = doc.getString("publishedDate") ?: "এইমাত্র"

                            Story(
                                id = id,
                                title = title,
                                excerpt = excerpt,
                                fullContent = fullContent,
                                authorName = authorName,
                                authorHandle = authorHandle,
                                authorBio = authorBio,
                                genre = genre,
                                type = type,
                                likesCount = likesCount,
                                lovesCount = lovesCount,
                                firesCount = firesCount,
                                commentsCount = commentsCount,
                                readTimeMinutes = readTimeMinutes,
                                publishedDate = publishedDate
                            )
                        } catch (e: Exception) {
                            Log.e("CloudRepository", "Error parsing story: ${e.message}")
                            null
                        }
                    }
                    trySend(stories)
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Publish a story to the cloud for all users to read
     */
    suspend fun publishStoryToCloud(story: Story): Boolean {
        val db = firestore ?: return false
        return try {
            val storyData = hashMapOf(
                "title" to story.title,
                "authorName" to story.authorName,
                "authorHandle" to story.authorHandle,
                "authorBio" to story.authorBio,
                "type" to story.type.banglaLabel,
                "genre" to story.genre,
                "excerpt" to story.excerpt,
                "fullContent" to story.fullContent,
                "likesCount" to story.likesCount,
                "lovesCount" to story.lovesCount,
                "firesCount" to story.firesCount,
                "commentsCount" to story.commentsCount,
                "readTimeMinutes" to story.readTimeMinutes,
                "publishedDate" to story.publishedDate,
                "timestamp" to System.currentTimeMillis()
            )
            db.collection("stories").document(story.id).set(storyData).await()
            true
        } catch (e: Exception) {
            Log.e("CloudRepository", "Failed to publish story to cloud: ${e.message}")
            false
        }
    }

    /**
     * Real-time stream of comments for a specific story
     */
    fun getCommentsFlow(storyId: String): Flow<List<Comment>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = db.collection("stories")
            .document(storyId)
            .collection("comments")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("CloudRepository", "Error fetching comments: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val comments = snapshot.documents.mapNotNull { doc ->
                        try {
                            Comment(
                                id = doc.id,
                                storyId = storyId,
                                authorName = doc.getString("authorName") ?: "বেনামী পাঠক",
                                authorAvatar = doc.getString("authorAvatar") ?: "",
                                text = doc.getString("text") ?: "",
                                timeAgo = doc.getString("timeAgo") ?: "এইমাত্র",
                                likesCount = (doc.getLong("likesCount") ?: 0L).toInt()
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(comments)
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Add a comment to a story in the cloud
     */
    suspend fun addCommentToCloud(storyId: String, comment: Comment): Boolean {
        val db = firestore ?: return false
        return try {
            val commentData = hashMapOf(
                "authorName" to comment.authorName,
                "authorAvatar" to comment.authorAvatar,
                "text" to comment.text,
                "timeAgo" to comment.timeAgo,
                "likesCount" to comment.likesCount,
                "timestamp" to System.currentTimeMillis()
            )
            val docRef = db.collection("stories").document(storyId)
            docRef.collection("comments").document(comment.id).set(commentData).await()
            docRef.update("commentsCount", FieldValue.increment(1)).await()
            true
        } catch (e: Exception) {
            Log.e("CloudRepository", "Failed to add comment to cloud: ${e.message}")
            false
        }
    }
}
