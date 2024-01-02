package com.adriantache.gptassistant.presentation.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.font.FontStyle.Companion.Normal
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.adriantache.gptassistant.R
import com.adriantache.gptassistant.domain.model.Conversation
import com.adriantache.gptassistant.domain.model.ui.ConversationUi
import com.adriantache.gptassistant.domain.model.ui.ConversationUi.Companion.toUi
import com.adriantache.gptassistant.domain.model.ui.ConversationsUi
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private var newConversation = Conversation(title = "New conversation").toUi(false)

// TODO: clean this up, split into multiple composables, move logic to use case
// TODO: move search function to use case
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviousConversationsDialog(
    getConversationHistory: suspend (Long?, Long?, String?) -> ConversationsUi,
    onLoadConversation: (ConversationUi) -> Unit,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    var conversations by remember { mutableStateOf(listOf(newConversation)) }
    lateinit var onDelete: suspend (String) -> Unit

    var isLoading: Boolean by remember { mutableStateOf(true) }
    var showDatePicker: Boolean by remember { mutableStateOf(false) }
    var deleteConversationId: String? by remember { mutableStateOf(null) }

    var dateRangeStart: Long? by remember { mutableStateOf(null) }
    var dateRangeEnd: Long? by remember { mutableStateOf(null) }
    var searchText: String? by remember { mutableStateOf(null) }

    LaunchedEffect(dateRangeStart, dateRangeEnd, searchText) {
        isLoading = true

        val conversationsUi = getConversationHistory(dateRangeStart, dateRangeEnd, searchText)

        conversations = listOf(newConversation) + conversationsUi.conversations
        onDelete = conversationsUi.onDeleteConversation

        isLoading = false
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Column {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Pick a saved conversation",
                        style = MaterialTheme.typography.headlineSmall,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                    ) {
                        var isSearchVisible by remember { mutableStateOf(false) }

                        AnimatedVisibility(visible = isSearchVisible) {
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = searchText.orEmpty(),
                                onValueChange = { searchText = it },
                                trailingIcon = {
                                    IconButton(onClick = {
                                        isSearchVisible = false
                                        searchText = null
                                    }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_delete_forever_24),
                                            contentDescription = "clear"
                                        )
                                    }
                                }
                            )
                        }

                        AnimatedVisibility(visible = !isSearchVisible) {
                            IconButton(onClick = { isSearchVisible = true }) {
                                Icon(painter = painterResource(id = R.drawable.baseline_search_24), contentDescription = "search")
                            }
                        }

                        AnimatedVisibility(visible = !isSearchVisible) {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(painter = painterResource(id = R.drawable.baseline_filter_alt_24), contentDescription = "filter")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            items(conversations, key = { it.id }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max)
                        .requiredHeightIn(min = 48.dp)
                        .clickable {
                            if (it == newConversation) {
                                onDismiss()
                            } else {
                                onLoadConversation(it)
                                onDismiss()
                            }
                        }
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.width(8.dp))

                    Column(
                        Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                    ) {
                        Text(
                            text = it.title ?: it.messages.first().content.take(50),
                            fontStyle = if (it == newConversation) Italic else Normal,
                        )

                        if (it != newConversation && it.startedAt != 0L) {
                            val date = remember {
                                Instant.ofEpochMilli(it.startedAt).atZone(ZoneId.systemDefault())
                            }
                            val formatter = DateTimeFormatter.RFC_1123_DATE_TIME
                            val formattedDate by remember {
                                derivedStateOf {
                                    formatter.format(date)
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.labelSmall,
                                fontStyle = Italic,
                                color = LocalContentColor.current.copy(alpha = 0.7f)
                            )
                        }
                    }

                    if (it != newConversation) {
                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .requiredWidth(32.dp)
                                .background(MaterialTheme.colorScheme.error.copy(0.75f))
                                .clickable { deleteConversationId = it.id },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_delete_forever_24),
                                contentDescription = "Delete conversation",
                                tint = MaterialTheme.colorScheme.onError,
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                item {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        }

        if (showDatePicker) {
            val datePickerState = remember {
                DateRangePickerState(
                    locale = CalendarLocale.getDefault(),
                    yearRange = 2023..LocalDateTime.now().year,
                )
            }

            BasicAlertDialog(
                onDismissRequest = { showDatePicker = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Column(
                    Modifier
                        .padding(16.dp)
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    DateRangePicker(
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        state = datePickerState
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        if (dateRangeStart != null || dateRangeEnd != null) {
                            Button(
                                onClick = {
                                    dateRangeStart = null
                                    dateRangeEnd = null
                                    showDatePicker = false
                                }
                            ) {
                                Text("Clear filter")
                            }

                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Button(
                            onClick = {
                                dateRangeStart = datePickerState.selectedStartDateMillis
                                dateRangeEnd = datePickerState.selectedEndDateMillis
                                showDatePicker = false
                            }
                        ) {
                            Text("Apply filter")
                        }
                    }
                }
            }
        }

        if (deleteConversationId != null) {
            AlertDialog(
                onDismissRequest = { deleteConversationId = null },
                title = {
                    Text("Delete conversation")
                },
                text = {
                    Text("Are you sure you want to delete this conversation?")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            deleteConversationId?.let {
                                scope.launch {
                                    onDelete(it)
                                    deleteConversationId = null
                                    conversations =
                                        listOf(newConversation) + getConversationHistory(
                                            dateRangeStart,
                                            dateRangeEnd,
                                            searchText,
                                        ).conversations
                                }
                            }
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    Button(onClick = { deleteConversationId = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Preview
@Composable
private fun PreviousConversationsDialogPreview() {
    PreviousConversationsDialog(
        getConversationHistory = { _, _, _ ->
            ConversationsUi(
                conversations = emptyList(),
                onDeleteConversation = {},
            )
        },
        onLoadConversation = {},
        onDismiss = {},
    )
}
