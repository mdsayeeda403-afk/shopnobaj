package com.example.ui

import androidx.lifecycle.ViewModel
import com.example.data.Chapter
import com.example.data.Comment
import com.example.data.CommentReply
import com.example.data.ReactionType
import com.example.data.ReadingTheme
import com.example.data.SampleData
import com.example.data.Story
import com.example.data.StoryType
import com.example.data.WriterStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class HomeTab(val label: String) {
    FOR_YOU("আপনার জন্য"),
    TRENDING("ট্রেন্ডিং"),
    FOLLOWING("অনুসরণ")
}

data class SwapnobajUiState(
    val currentTab: HomeTab = HomeTab.FOR_YOU,
    val selectedGenre: String = "সব",
    val searchQuery: String = "",
    val stories: List<Story> = SampleData.sampleStories,
    val currentReaderStory: Story? = null,
    val currentChapterIndex: Int = 0,
    val readingTheme: ReadingTheme = ReadingTheme.SEPIA,
    val readingFontSizeSp: Float = 18f,
    val isCommentsSheetOpen: Boolean = false,
    val currentComments: List<Comment> = SampleData.sampleComments,
    val isTipDialogOpen: Boolean = false,
    val tipTargetAuthor: String? = null,
    val isAuthModalOpen: Boolean = false,
    val isBookshelfGridView: Boolean = true,
    val bookshelfFilter: String = "সব",
    val writerStats: WriterStats = WriterStats(),
    val isUserLoggedIn: Boolean = true,
    val loggedInUserName: String = "স্বপ্নবাজ পাঠক",
    val loggedInUserHandle: String = "@swapnobaj_reader",
    val successSnackbarMessage: String? = null
)

class SwapnobajViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SwapnobajUiState())
    val uiState: StateFlow<SwapnobajUiState> = _uiState.asStateFlow()

    fun selectHomeTab(tab: HomeTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun selectGenre(genre: String) {
        _uiState.update { it.copy(selectedGenre = genre) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun openReader(story: Story, chapterIndex: Int = 0) {
        _uiState.update {
            it.copy(
                currentReaderStory = story,
                currentChapterIndex = chapterIndex
            )
        }
    }

    fun closeReader() {
        _uiState.update { it.copy(currentReaderStory = null) }
    }

    fun selectChapter(index: Int) {
        _uiState.update { it.copy(currentChapterIndex = index) }
    }

    fun setReadingTheme(theme: ReadingTheme) {
        _uiState.update { it.copy(readingTheme = theme) }
    }

    fun increaseFontSize() {
        _uiState.update {
            if (it.readingFontSizeSp < 26f) it.copy(readingFontSizeSp = it.readingFontSizeSp + 2f) else it
        }
    }

    fun decreaseFontSize() {
        _uiState.update {
            if (it.readingFontSizeSp > 14f) it.copy(readingFontSizeSp = it.readingFontSizeSp - 2f) else it
        }
    }

    fun toggleReaction(storyId: String, reaction: ReactionType) {
        _uiState.update { state ->
            val updated = state.stories.map { story ->
                if (story.id == storyId) {
                    if (story.userReaction == reaction) {
                        // Undo reaction
                        val (l, lo, f, d) = when (reaction) {
                            ReactionType.LIKE -> listOf(story.likesCount - 1, story.lovesCount, story.firesCount, story.dislikesCount)
                            ReactionType.LOVE -> listOf(story.likesCount, story.lovesCount - 1, story.firesCount, story.dislikesCount)
                            ReactionType.FIRE -> listOf(story.likesCount, story.lovesCount, story.firesCount - 1, story.dislikesCount)
                            ReactionType.DISLIKE -> listOf(story.likesCount, story.lovesCount, story.firesCount, story.dislikesCount - 1)
                        }
                        story.copy(
                            userReaction = null,
                            likesCount = maxOf(0, l),
                            lovesCount = maxOf(0, lo),
                            firesCount = maxOf(0, f),
                            dislikesCount = maxOf(0, d)
                        )
                    } else {
                        // Switch or new reaction
                        var newL = story.likesCount
                        var newLo = story.lovesCount
                        var newF = story.firesCount
                        var newD = story.dislikesCount

                        // Decrement previous if existed
                        story.userReaction?.let { prev ->
                            when (prev) {
                                ReactionType.LIKE -> newL--
                                ReactionType.LOVE -> newLo--
                                ReactionType.FIRE -> newF--
                                ReactionType.DISLIKE -> newD--
                            }
                        }

                        // Increment new
                        when (reaction) {
                            ReactionType.LIKE -> newL++
                            ReactionType.LOVE -> newLo++
                            ReactionType.FIRE -> newF++
                            ReactionType.DISLIKE -> newD++
                        }

                        story.copy(
                            userReaction = reaction,
                            likesCount = maxOf(0, newL),
                            lovesCount = maxOf(0, newLo),
                            firesCount = maxOf(0, newF),
                            dislikesCount = maxOf(0, newD)
                        )
                    }
                } else {
                    story
                }
            }

            val updatedCurrent = if (state.currentReaderStory?.id == storyId) {
                updated.find { it.id == storyId }
            } else state.currentReaderStory

            state.copy(stories = updated, currentReaderStory = updatedCurrent)
        }
    }

    fun toggleBookmark(storyId: String) {
        _uiState.update { state ->
            val updated = state.stories.map { story ->
                if (story.id == storyId) story.copy(isBookmarked = !story.isBookmarked) else story
            }
            val current = if (state.currentReaderStory?.id == storyId) {
                updated.find { it.id == storyId }
            } else state.currentReaderStory
            val msg = if (updated.find { it.id == storyId }?.isBookmarked == true) "বইয়ের তাকে সংরক্ষণ করা হয়েছে" else "সংরক্ষণ বাতিল করা হয়েছে"
            state.copy(stories = updated, currentReaderStory = current, successSnackbarMessage = msg)
        }
    }

    fun toggleFollow(authorHandle: String) {
        _uiState.update { state ->
            val isNowFollowing = state.stories.find { it.authorHandle == authorHandle }?.isFollowing == false
            val updated = state.stories.map { story ->
                if (story.authorHandle == authorHandle) story.copy(isFollowing = !story.isFollowing) else story
            }
            val msg = if (isNowFollowing) "$authorHandle কে অনুসরণ করা হচ্ছে" else "অনুসরণ বাতিল করা হয়েছে"
            state.copy(stories = updated, successSnackbarMessage = msg)
        }
    }

    fun openCommentsSheet(story: Story) {
        _uiState.update { it.copy(isCommentsSheetOpen = true) }
    }

    fun closeCommentsSheet() {
        _uiState.update { it.copy(isCommentsSheetOpen = false) }
    }

    fun addComment(text: String) {
        if (text.isBlank()) return
        val newComment = Comment(
            id = "comment-${System.currentTimeMillis()}",
            authorName = _uiState.value.loggedInUserName,
            authorAvatar = "",
            text = text,
            timeAgo = "এইমাত্র",
            likesCount = 0
        )
        _uiState.update {
            it.copy(
                currentComments = listOf(newComment) + it.currentComments,
                successSnackbarMessage = "আপনার মন্তব্যটি প্রকাশিত হয়েছে"
            )
        }
    }

    fun openTipDialog(authorName: String) {
        _uiState.update { it.copy(isTipDialogOpen = true, tipTargetAuthor = authorName) }
    }

    fun closeTipDialog() {
        _uiState.update { it.copy(isTipDialogOpen = false, tipTargetAuthor = null) }
    }

    fun confirmTip(amount: Int) {
        val author = _uiState.value.tipTargetAuthor ?: "লেখক"
        _uiState.update {
            it.copy(
                isTipDialogOpen = false,
                tipTargetAuthor = null,
                successSnackbarMessage = "ধন্যবাদ! $author কে ৳$amount সহায়তা পাঠানো হয়েছে 🎉"
            )
        }
    }

    fun toggleBookshelfLayout() {
        _uiState.update { it.copy(isBookshelfGridView = !it.isBookshelfGridView) }
    }

    fun setBookshelfFilter(filter: String) {
        _uiState.update { it.copy(bookshelfFilter = filter) }
    }

    fun publishNewStory(
        title: String,
        content: String,
        type: StoryType,
        genre: String,
        chapters: List<Chapter>
    ) {
        val newStory = Story(
            id = "story-${System.currentTimeMillis()}",
            title = title,
            excerpt = if (content.length > 120) content.take(120) + "..." else content,
            fullContent = content,
            authorName = _uiState.value.loggedInUserName,
            authorHandle = _uiState.value.loggedInUserHandle,
            authorBio = "স্বপ্নবাজ পরিবারের নতুন কলমসেনানী।",
            genre = genre,
            type = type,
            coverGradientStart = 0xFF1E3A8A,
            coverGradientEnd = 0xFF0D9488,
            likesCount = 1,
            lovesCount = 1,
            firesCount = 0,
            dislikesCount = 0,
            commentsCount = 0,
            readTimeMinutes = maxOf(1, content.split(" ").size / 150),
            publishedDate = "এইমাত্র",
            chapters = chapters
        )

        _uiState.update {
            it.copy(
                stories = listOf(newStory) + it.stories,
                successSnackbarMessage = "অভিনন্দন! আপনার লেখাটি সফলভাবে প্রকাশিত হয়েছে ✨"
            )
        }
    }

    fun openAuthModal() {
        _uiState.update { it.copy(isAuthModalOpen = true) }
    }

    fun closeAuthModal() {
        _uiState.update { it.copy(isAuthModalOpen = false) }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(successSnackbarMessage = null) }
    }
}
