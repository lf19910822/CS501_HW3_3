package com.example.hw3_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hw3_3.ui.theme.HW3_3Theme
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HW3_3Theme {
                ContactListScreen()
            }
        }
    }
}

data class Contact(val id: Int, val name: String)

private fun generateSampleContacts(n: Int = 80): List<Contact> {
    val firstNames = listOf(
        "Alice","Aaron","Ava","Ben","Bella","Bob","Cathy","Chris","Clara","David","Dylan","Dora",
        "Evan","Elsa","Ethan","Fiona","Frank","Grace","Gavin","Hank","Helen","Iris","Ian","Jack",
        "Judy","Ken","Kara","Leo","Lily","Liam","Mia","Mason","Nina","Noah","Olivia","Oscar",
        "Paul","Piper","Quinn","Ryan","Ruby","Sara","Sam","Tony","Tina","Uma","Uri","Victor",
        "Vera","Wade","Wendy","Xander","Yara","Zack","Zoe"
    )
    val lastNames = listOf(
        "Anderson","Brown","Clark","Davis","Evans","Ford","Green","Harris","Iverson","Johnson",
        "King","Lopez","Miller","Nelson","Owens","Parker","Quinn","Roberts","Smith","Taylor",
        "Underwood","Vasquez","Walker","Xu","Young","Zimmer"
    )

    val rnd = Random(42)
    return (0 until n).map { i ->
        val fn = firstNames[rnd.nextInt(firstNames.size)]
        val ln = lastNames[rnd.nextInt(lastNames.size)]
        val name = "$fn $ln"
        Contact(i, name)
    }.sortedBy { it.name }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactListScreen() {
    val contacts = remember { generateSampleContacts(80) } // ≥ 50 条

    val grouped = remember(contacts) {
        contacts.groupBy { it.name.first().uppercaseChar() }.toSortedMap()
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val showFab by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 10 }
    }

    Scaffold(
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(0)
                        }
                    }
                ) { Text("Top") }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            state = listState
        ) {
            grouped.forEach { (letter, list) ->
                stickyHeader {
                    Header(letter)
                }
                items(
                    items = list,
                    key = { it.id }           // 提供稳定 key 提升复用
                ) { contact ->
                    ContactRow(contact)
                }
            }
        }
    }
}

@Composable
private fun Header(letter: Char) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1F1F1))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = letter.toString(),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF333333)
        )
    }
}

@Composable
private fun ContactRow(c: Contact) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(text = c.name, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(3.dp))
    }
    HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Color(0xFFE6E6E6))
}

@Preview(showBackground = true)
@Composable
fun PreviewContactList() {
    HW3_3Theme {
        ContactListScreen()
    }
}
