package com.example.data

enum class StoryType(val banglaLabel: String) {
    GOLPO("গল্প"),
    KOBITA("কবিতা"),
    UPONNAS("উপন্যাস")
}

enum class ReactionType(val banglaLabel: String, val emoji: String) {
    LIKE("লাইক", "👍"),
    LOVE("ভালোবাসা", "❤️"),
    FIRE("আগুন", "🔥"),
    DISLIKE("অপছন্দ", "👎")
}

enum class ReadingTheme {
    LIGHT,
    SEPIA,
    DARK
}

data class Chapter(
    val id: String,
    val chapterNumber: Int,
    val title: String,
    val content: String,
    val readTimeMinutes: Int
)

data class CommentReply(
    val id: String,
    val authorName: String,
    val authorAvatar: String,
    val text: String,
    val timeAgo: String,
    val likesCount: Int = 0
)

data class Comment(
    val id: String,
    val storyId: String = "",
    val authorName: String,
    val authorAvatar: String,
    val text: String,
    val timeAgo: String,
    val likesCount: Int = 0,
    val replies: List<CommentReply> = emptyList()
)

data class Story(
    val id: String,
    val title: String,
    val excerpt: String,
    val fullContent: String,
    val authorName: String,
    val authorHandle: String,
    val authorBio: String = "",
    val authorAvatar: String = "",
    val genre: String,
    val type: StoryType,
    val coverGradientStart: Long = 0xFF1E3A8A,
    val coverGradientEnd: Long = 0xFF0F172A,
    val likesCount: Int = 120,
    val lovesCount: Int = 85,
    val firesCount: Int = 42,
    val dislikesCount: Int = 3,
    val commentsCount: Int = 18,
    val readTimeMinutes: Int = 4,
    val publishedDate: String = "আজ দুপুর ২:১৫",
    val isBookmarked: Boolean = false,
    val userReaction: ReactionType? = null,
    val isFollowing: Boolean = false,
    val readingProgressPercent: Int = 0,
    val chapters: List<Chapter> = emptyList()
)

data class WriterStats(
    val totalViews: String = "০",
    val totalReads: String = "০",
    val totalReactions: String = "০",
    val totalComments: String = "০",
    val followersCount: String = "০",
    val totalTips: String = "৳ ০"
)
