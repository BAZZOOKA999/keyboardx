package com.example.ime.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KeyboardThemeColors

@Composable
fun EmojiPickerView(
    colors: KeyboardThemeColors,
    onEmojiSelected: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf(
        "😃 Expressões",
        "👋 Gestos",
        "🐶 Natureza",
        "🍔 Comida",
        "🚀 Lugares",
        "💡 Objetos",
        "❤️ Símbolos",
        "🎌 Kaomojis"
    )

    val smileys = listOf(
        "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "🥲", "🥹", "😊", "😇",
        "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚", "😋", "😛",
        "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🥸", "🤩", "🥳", "😏", "😒",
        "😞", "😔", "😟", "😕", "🙁", "☹️", "😣", "😖", "😫", "😩", "🥺", "😢",
        "😭", "😮‍💨", "😤", "😠", "😡", "🤬", "🤯", "😳", "🥵", "🥶", "😱", "😨"
    )

    val gestures = listOf(
        "👍", "👎", "👊", "✊", "🤛", "🤜", "👏", "🙌", "👐", "🤲", "🤝", "🙏",
        "✌️", "🤞", "🫰", "🤟", "🤘", "👌", "🤌", "🤏", "👈", "👉", "👆", "👇",
        "☝️", "✋", "🤚", "🖐️", "🖖", "👋", "🤙", "💪", "🦾", "🖕", "✍️", "🤳"
    )

    val animals = listOf(
        "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐻‍❄️", "🐨", "🐯", "🦁",
        "🐮", "🐷", "🐸", "🐵", "🐔", "🐧", "🐦", "🐤", "🦆", "🦅", "🦉", "🦇",
        "🐺", "🐗", "🐴", "🦄", "🐝", "🪱", "🐛", "🦋", "🐌", "🐞", "🐜", "🪰",
        "🐙", "🦑", "🦐", "🦞", "🦀", "🐡", "🐠", "🐟", "🐬", "🐳", "🐋", "🦈"
    )

    val food = listOf(
        "🍏", "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐", "🍈", "🍒",
        "🍑", "🥭", "🍍", "🥥", "🥝", "🍅", "🥑", "🥦", "🥬", "🥒", "🌶️", "🌽",
        "🍕", "🍔", "🍟", "🌭", "🍿", "🧈", "🥞", "🧇", "🥓", "🥩", "🍗", "🍖",
        "🍜", "🍲", "🍣", "🍱", "🥟", "🍤", "🍙", "🍚", "🍘", "🍢", "🥮", "🍧"
    )

    val places = listOf(
        "🚗", "🚕", "🚙", "🚌", "🚎", "🏎️", "🚓", "🚑", "🚒", "🚐", "🛻", "🚚",
        "✈️", "🛫", "🛬", "🚀", "🛸", "🚁", "⛵", "🚤", "🚢", "🛳️", "🚂", "🚆",
        "🏔️", "⛰️", "🌋", "🏖️", "🏝️", "🏜️", "🏕️", "⛺", "🏠", "🏡", "🏢", "🏣"
    )

    val objects = listOf(
        "💻", "🖥️", "🖨️", "⌨️", "🖱️", "📱", "📲", "☎️", "📟", "📠", "🔋", "🔌",
        "💡", "🔦", "🕯️", "🧯", "🛢️", "💸", "💵", "💴", "💶", "💷", "🪙", "💳",
        "💎", "⚖️", "🪜", "🧰", "🪛", "🔧", "🔨", "⚒️", "🛠️", "⛏️", "🔩", "⚙️"
    )

    val symbols = listOf(
        "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔", "❤️‍🔥", "❤️‍🩹",
        "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟", "☮️", "✝️", "☪️",
        "💯", "💢", "💥", "💫", "💦", "💨", "🕳️", "💬", "👁️‍🗨️", "🗨️", "🗯️", "💭",
        "⚠️", "⛔", "🚫", "🚳", "🚭", "🚯", "🚱", "🚷", "📵", "🔞", "☢️", "☣️"
    )

    val kaomojis = listOf(
        "(•‿•)", "(─‿‿─)", "(ಥ﹏ಥ)", "¯\\_(ツ)_/¯", "(╯°□°)╯︵ ┻━┻", "(ง'̀-'́)ง",
        "ʕ•ᴥ•ʔ", "(｡♥‿♥｡)", "(¬_¬)", "(★‿★)", "(ʘ‿ʘ)", "(づ｡◕‿‿◕｡)づ",
        "(ノಠ益ಠ)ノ", "ಠ_ಠ", "(°ロ°)", "(づ￣ ³￣)づ", "(´・ω・`)", "(>_<)",
        "(ヘ･_･)ヘ┳━┳", "ᕙ(⇀‸↼‶)ᕗ", "(✿◠‿◠)", "( ͡° ͜ʖ ͡°)", "ฅ^•ﻌ•^ฅ"
    )

    val displayedItems = when (selectedCategoryIndex) {
        0 -> smileys
        1 -> gestures
        2 -> animals
        3 -> food
        4 -> places
        5 -> objects
        6 -> symbols
        else -> kaomojis
    }.filter {
        searchQuery.isBlank() || it.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(colors.background)
            .padding(4.dp)
    ) {
        // Top Header with Search and Back
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.keyBackground)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = colors.keyText,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Pesquisar...", color = colors.secondaryText, fontSize = 12.sp) },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Busca",
                        tint = colors.secondaryText,
                        modifier = Modifier.size(16.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Limpar",
                                tint = colors.secondaryText,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accent,
                    unfocusedBorderColor = colors.keyBackground,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
            )
        }

        // Category Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedCategoryIndex,
            edgePadding = 4.dp,
            containerColor = colors.background,
            contentColor = colors.accent,
            modifier = Modifier.height(34.dp)
        ) {
            categories.forEachIndexed { index, cat ->
                Tab(
                    selected = selectedCategoryIndex == index,
                    onClick = { selectedCategoryIndex = index },
                    text = {
                        Text(
                            text = cat,
                            fontSize = 11.sp,
                            color = if (selectedCategoryIndex == index) colors.accent else colors.secondaryText
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Grid of Emojis / Kaomojis
        val isKaomoji = selectedCategoryIndex == 7
        LazyVerticalGrid(
            columns = GridCells.Fixed(if (isKaomoji) 3 else 8),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(2.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(displayedItems) { item ->
                Box(
                    modifier = Modifier
                        .height(if (isKaomoji) 38.dp else 40.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isKaomoji) colors.keyBackground else Color.Transparent)
                        .clickable { onEmojiSelected(item) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        fontSize = if (isKaomoji) 12.sp else 22.sp,
                        color = Color.White,
                        fontWeight = if (isKaomoji) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
