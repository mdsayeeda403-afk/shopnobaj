package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleData
import com.example.ui.SwapnobajUiState
import com.example.ui.SwapnobajViewModel

data class GenreCategory(
    val title: String,
    val iconEmoji: String,
    val count: String,
    val color1: Long,
    val color2: Long
)

@Composable
fun SearchScreen(
    uiState: SwapnobajUiState,
    viewModel: SwapnobajViewModel,
    modifier: Modifier = Modifier
) {
    val genreCategories = listOf(
        GenreCategory("রোমান্স", "🌹", "১৪০+ গল্প", 0xFFE11D48, 0xFFBE123C),
        GenreCategory("থ্রিলার ও রহস্য", "🔍", "৯৫+ সৃষ্টি", 0xFF1E293B, 0xFF0F172A),
        GenreCategory("ভৌতিক ও প্যারানরমাল", "👻", "৭২+ গল্প", 0xFF4A044E, 0xFF2E1065),
        GenreCategory("সামাজিক ও জীবনবোধ", "🍃", "১৮০+ লেখা", 0xFF065F46, 0xFF047857),
        GenreCategory("আধুনিক কবিতা", "✍️", "৩১০+ কবিতা", 0xFF92400E, 0xFFB45309),
        GenreCategory("বিজ্ঞান কল্পকাহিনী", "🚀", "৫০+ সৃষ্টি", 0xFF1E40AF, 0xFF1E3A8A)
    )

    val trendingTags = listOf(
        "#বৃষ্টির_চিঠি",
        "#মধ্যরাতের_উপন্যাস",
        "#শৈশব_স্মৃতি",
        "#অনুগল্প_২০২৬",
        "#অমর_একুশে",
        "#একাকীত্ব"
    )

    val searchResults = if (uiState.searchQuery.isNotBlank()) {
        uiState.stories.filter {
            it.title.contains(uiState.searchQuery, ignoreCase = true) ||
            it.authorName.contains(uiState.searchQuery, ignoreCase = true) ||
            it.genre.contains(uiState.searchQuery, ignoreCase = true)
        }
    } else emptyList()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        // Search Header & Input
        item {
            Text(
                text = "অনুসন্ধান ও আবিষ্কার",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "শিরোনাম, লেখক অথবা সাহিত্যের ধরণ দিয়ে খুঁজে নিন",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("গল্প, কবিতা বা লেখকের নাম লিখুন...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                ),
                singleLine = true
            )
        }

        // Search Results Section if query is active
        if (uiState.searchQuery.isNotBlank()) {
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "ফলাফল (${searchResults.size}টি পাওয়া গেছে)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (searchResults.isEmpty()) {
                item {
                    Text(
                        text = "\"${uiState.searchQuery}\"-এর জন্য কোনো লেখা পাওয়া যায়নি।",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            } else {
                items(searchResults, key = { it.id }) { story ->
                    StoryFeedCard(
                        story = story,
                        onCardClick = { viewModel.openReader(story) },
                        onReactionClick = { reaction -> viewModel.toggleReaction(story.id, reaction) },
                        onBookmarkClick = { viewModel.toggleBookmark(story.id) },
                        onCommentsClick = { viewModel.openCommentsSheet(story) },
                        onFollowClick = { viewModel.toggleFollow(story.authorHandle) }
                    )
                }
            }
        } else {
            // Trending Tags Carousel
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ট্রেন্ডিং ট্যাগসমূহ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(trendingTags) { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                                .clickable { viewModel.updateSearchQuery(tag.removePrefix("#")) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Genre Exploration Grid
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "সাহিত্যের শাখা ও ধারা",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(genreCategories.chunked(2)) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pair.forEach { cat ->
                        GenreBannerCard(
                            category = cat,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.updateSearchQuery(cat.title) }
                        )
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // Featured Writers Spotlight
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "শীর্ষ লেখক ও কবি",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                val featuredWriters = listOf(
                    Triple("সুস্মিতা রায়", "@susmita_roy", "৩টি উপন্যাস • ১২টি গল্প"),
                    Triple("ফারহান আহমেদ", "@farhan_thriller", "৫টি থ্রিলার সিরিজ"),
                    Triple("তাহসিন কবির", "@tahsin_kabir", "৮৫টি আধুনিক কবিতা")
                )

                featuredWriters.forEach { (name, handle, stats) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name.take(1),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "$handle • $stats", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Button(
                                onClick = { viewModel.toggleFollow(handle) },
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("অনুসরণ", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GenreBannerCard(
    category: GenreCategory,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(category.color1), Color(category.color2))
                    )
                )
                .padding(12.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Column {
                Text(text = category.iconEmoji, fontSize = 22.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = category.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = category.count,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 10.sp
                )
            }
        }
    }
}
