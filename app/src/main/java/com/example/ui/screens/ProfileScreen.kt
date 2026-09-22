package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SwapnobajUiState
import com.example.ui.SwapnobajViewModel

@Composable
fun ProfileScreen(
    uiState: SwapnobajUiState,
    viewModel: SwapnobajViewModel,
    modifier: Modifier = Modifier
) {
    var selectedProfileTab by remember { mutableStateOf(0) }
    val profileTabs = listOf("প্রকাশিত সৃষ্টি", "লেখক ড্যাশবোর্ড", "ড্রাফট")

    val userStories = uiState.stories.filter { it.authorHandle == uiState.loggedInUserHandle }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        // User Profile Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF1D4ED8), Color(0xFF0284C7))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (uiState.isUserLoggedIn) uiState.loggedInUserName.take(1) else "প",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (uiState.isUserLoggedIn) uiState.loggedInUserName else "স্বপ্নবাজ সাধারণ পাঠক",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (uiState.isUserLoggedIn) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Verified Writer",
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(
                        text = if (uiState.isUserLoggedIn) uiState.loggedInUserHandle else "@guest_reader",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (uiState.isUserLoggedIn) {
                            uiState.loggedInUserBio.ifBlank { "বাংলা সাহিত্যের চিরন্তন মুগ্ধ পাঠক ও স্বাধীন লেখক।" }
                        } else {
                            "আপনি সাধারণ পাঠক হিসেবে সব গল্প, কবিতা ও উপন্যাস পড়ছেন। নিজের লেখা প্রকাশ করতে লেখক হিসেবে লগইন করুন।"
                        },
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Follower & Following Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStatItem(count = "${userStories.size}", label = "প্রকাশিত কাজ")
                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.Gray.copy(alpha = 0.3f)))
                        ProfileStatItem(count = if (uiState.isUserLoggedIn) uiState.writerStats.followersCount else "০", label = "অনুসারী (Followers)")
                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.Gray.copy(alpha = 0.3f)))
                        ProfileStatItem(count = "০", label = "অনুসরণ (Following)")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action buttons: Tip Writer & Edit / Auth
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (uiState.isUserLoggedIn) {
                            Button(
                                onClick = { viewModel.openTipDialog(uiState.loggedInUserName) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                            ) {
                                Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("টিপ / সহায়তা", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = { viewModel.openAuthModal() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = if (!uiState.isUserLoggedIn) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                     else ButtonDefaults.outlinedButtonColors()
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (uiState.isUserLoggedIn) "লেখক প্রোফাইল" else "লেখক লগইন করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Tab Row: Published | Writer Dashboard | Drafts
        item {
            Spacer(modifier = Modifier.height(16.dp))
            ScrollableTabRow(
                selectedTabIndex = selectedProfileTab,
                edgePadding = 0.dp,
                containerColor = Color.Transparent,
                divider = {}
            ) {
                profileTabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedProfileTab == index,
                        onClick = { selectedProfileTab = index },
                        text = {
                            Text(
                                text = tab,
                                fontWeight = if (selectedProfileTab == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Tab Content
        when (selectedProfileTab) {
            1 -> {
                // Writer Dashboard (Views, Likes, Comments, Tips Analytics)
                item {
                    WriterDashboardSection(uiState = uiState)
                }
            }
            2 -> {
                // Drafts
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "কোনো সংরক্ষিত খসড়া নেই",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "আপনার লেখার অসম্পূর্ণ খসড়াগুলো এখানে সংরক্ষিত থাকবে।",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            else -> {
                // Published Stories
                if (userStories.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "এখনো কোনো লেখা প্রকাশিত হয়নি",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "'লিখুন' অপশন থেকে আপনার গল্প বা কবিতা প্রকাশ করুন।",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(userStories, key = { it.id }) { story ->
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
            }
        }
    }
}

@Composable
fun ProfileStatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun WriterDashboardSection(uiState: SwapnobajUiState) {
    Column {
        Text(
            text = "লেখক অ্যানালিটিক্স ও পরিসংখ্যান",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(10.dp))

        // 2x2 Analytics Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AnalyticsCard(
                icon = Icons.Default.Visibility,
                title = "মোট ভিউ",
                value = uiState.writerStats.totalViews,
                subtitle = "আপনার লেখার পাঠক",
                modifier = Modifier.weight(1f)
            )
            AnalyticsCard(
                icon = Icons.Default.FavoriteBorder,
                title = "মোট রিঅ্যাকশন",
                value = uiState.writerStats.totalReactions,
                subtitle = "পাঠকের ভালোবাসা",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AnalyticsCard(
                icon = Icons.Default.ChatBubbleOutline,
                title = "পাঠকের মন্তব্য",
                value = uiState.writerStats.totalComments,
                subtitle = "পাঠকের প্রতিক্রিয়া",
                modifier = Modifier.weight(1f)
            )
            AnalyticsCard(
                icon = Icons.Default.MonetizationOn,
                title = "অর্জিত টিপস",
                value = uiState.writerStats.totalTips,
                subtitle = "সরাসরি লেখক সম্মাননা",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun AnalyticsCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color(0xFF0D9488)
            )
        }
    }
}
