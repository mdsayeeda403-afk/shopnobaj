package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.Chapter
import com.example.data.SampleData
import com.example.data.StoryType
import com.example.ui.SwapnobajUiState
import com.example.ui.SwapnobajViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteScreen(
    uiState: SwapnobajUiState,
    viewModel: SwapnobajViewModel,
    modifier: Modifier = Modifier,
    onPublishSuccess: () -> Unit = {}
) {
    var selectedType by remember { mutableStateOf(StoryType.GOLPO) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("রোমান্স") }
    var isGenreDropdownExpanded by remember { mutableStateOf(false) }
    var tagsInput by remember { mutableStateOf("#বৃষ্টি, #স্মৃতি, #সাহিত্য") }

    // Novel Chapters state
    val chapters = remember {
        mutableStateListOf(
            Chapter(id = "ch-1", chapterNumber = 1, title = "প্রথম অধ্যায়: সূচনা", content = "", readTimeMinutes = 5)
        )
    }
    var activeChapterIndex by remember { mutableStateOf(0) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .padding(bottom = 90.dp)
    ) {
        // Creative Studio Header Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFFEFF6FF),
                                Color(0xFFDBEAFE).copy(alpha = 0.6f)
                            )
                        )
                    )
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_writer_art),
                    contentDescription = "Writer Quill Art",
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "লেখক স্টুডিও",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "শব্দ ও কল্পনার রঙে সাজান আপনার গল্প, কবিতা ও উপন্যাস",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Segmented Type Selector (গল্প | কবিতা | উপন্যাস)
        Text(
            text = "লেখার ধরন নির্বাচন করুন:",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StoryType.values().forEach { type ->
                val isSelected = selectedType == type
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                        .clickable { selectedType = type }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = type.banglaLabel,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Novel Specific: Chapter-by-chapter tabs & add chapter button
        if (selectedType == StoryType.UPONNAS) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "উপন্যাসের অধ্যায়সমূহ (${chapters.size}টি অধ্যায়)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        OutlinedButton(
                            onClick = {
                                val nextNum = chapters.size + 1
                                chapters.add(
                                    Chapter(
                                        id = "ch-$nextNum",
                                        chapterNumber = nextNum,
                                        title = "অধ্যায় $nextNum",
                                        content = "",
                                        readTimeMinutes = 5
                                    )
                                )
                                activeChapterIndex = chapters.lastIndex
                            },
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("নতুন অধ্যায়", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    ScrollableTabRow(
                        selectedTabIndex = activeChapterIndex,
                        edgePadding = 0.dp,
                        containerColor = Color.Transparent,
                        divider = {}
                    ) {
                        chapters.forEachIndexed { idx, ch ->
                            Tab(
                                selected = activeChapterIndex == idx,
                                onClick = { activeChapterIndex = idx },
                                text = { Text("অধ্যায় ${idx + 1}", fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Title Input
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("শিরোনাম লিখুন") },
            placeholder = { Text("যেমন: বৃষ্টির দিনের এক টুকরো স্মৃতি...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Genre Dropdown & Tags
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Genre Dropdown
            ExposedDropdownMenuBox(
                expanded = isGenreDropdownExpanded,
                onExpandedChange = { isGenreDropdownExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = selectedGenre,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("বিভাগ / ধারা") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGenreDropdownExpanded) },
                    modifier = Modifier.menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )
                ExposedDropdownMenu(
                    expanded = isGenreDropdownExpanded,
                    onDismissRequest = { isGenreDropdownExpanded = false }
                ) {
                    SampleData.genres.filter { it != "সব" }.forEach { genre ->
                        DropdownMenuItem(
                            text = { Text(genre) },
                            onClick = {
                                selectedGenre = genre
                                isGenreDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Tags Field
            OutlinedTextField(
                value = tagsInput,
                onValueChange = { tagsInput = it },
                label = { Text("ট্যাগসমূহ") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Text Formatting Toolbar (Rich Editor simulation)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { /* Bold text */ }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.FormatBold, contentDescription = "Bold", modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = { /* Italic text */ }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.FormatItalic, contentDescription = "Italic", modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = { /* Quote text */ }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.FormatQuote, contentDescription = "Quote", modifier = Modifier.size(18.dp))
                    }
                }

                val currentText = if (selectedType == StoryType.UPONNAS && chapters.isNotEmpty()) {
                    chapters[activeChapterIndex].content
                } else content

                val wordCount = if (currentText.isBlank()) 0 else currentText.trim().split("\\s+".toRegex()).size
                Text(
                    text = "$wordCount শব্দ • আনুমানিক ${(wordCount / 120).coerceAtLeast(1)} মিনিট",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Main Editor TextArea
        val editorContent = if (selectedType == StoryType.UPONNAS && chapters.isNotEmpty()) {
            chapters[activeChapterIndex].content
        } else content

        OutlinedTextField(
            value = editorContent,
            onValueChange = { newText ->
                if (selectedType == StoryType.UPONNAS && chapters.isNotEmpty()) {
                    val current = chapters[activeChapterIndex]
                    chapters[activeChapterIndex] = current.copy(content = newText)
                } else {
                    content = newText
                }
            },
            placeholder = {
                Text(
                    if (selectedType == StoryType.KOBITA) "এখানে আপনার কবিতার ছন্দ ও শব্দগুলো লিখুন..."
                    else if (selectedType == StoryType.UPONNAS) "অধ্যায় ${activeChapterIndex + 1}-এর গল্প এখানে লিখুন..."
                    else "এখানে আপনার গল্পটির বিস্তারিত বিবরণ লিখুন..."
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons: Save Draft & Publish
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    viewModel.publishNewStory(
                        title = if (title.isBlank()) "নামহীন খসড়া" else title,
                        content = if (content.isBlank()) "খসড়া সংরক্ষিত রয়েছে।" else content,
                        type = selectedType,
                        genre = selectedGenre,
                        chapters = if (selectedType == StoryType.UPONNAS) chapters else emptyList()
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("ড্রাফট রাখুন", fontSize = 14.sp)
            }

            Button(
                onClick = {
                    val finalTitle = if (title.isBlank()) "আমার নতুন সৃষ্টি" else title
                    val finalContent = if (selectedType == StoryType.UPONNAS && chapters.isNotEmpty()) {
                        chapters[0].content
                    } else if (content.isBlank()) {
                        "স্বপ্নবাজের পাতায় রচিত এক নতুন অধ্যায়..."
                    } else content

                    viewModel.publishNewStory(
                        title = finalTitle,
                        content = finalContent,
                        type = selectedType,
                        genre = selectedGenre,
                        chapters = if (selectedType == StoryType.UPONNAS) chapters else emptyList()
                    )
                    title = ""
                    content = ""
                    onPublishSuccess()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Publish, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("প্রকাশ করুন", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
