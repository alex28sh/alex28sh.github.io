package com.example.alex_website

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp

enum class Page(val title: String) {
    About("About me"),
    Cv("CV"),
    Blog("Blog"),
    Research("Research"),
}

@Composable
fun App() {
    val systemDark = isSystemInDarkTheme()
    var darkOverride by remember { mutableStateOf<Boolean?>(null) }
    val dark = darkOverride ?: systemDark
    var page by remember { mutableStateOf(Page.About) }
    val uriHandler = LocalUriHandler.current

    SiteTheme(dark) {
        val colors = MaterialTheme.colorScheme
        Column(Modifier.fillMaxSize().background(colors.background)) {
            Header(
                current = page,
                dark = dark,
                onToggleTheme = { darkOverride = !dark },
                onNavigate = {
                    if (it == Page.Cv) uriHandler.openUri(Profile.CV_URL) else page = it
                },
            )
            HorizontalDivider(color = colors.outlineVariant)
            Box(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.TopCenter,
            ) {
                SelectionContainer {
                    Column(
                        Modifier.widthIn(max = 860.dp).fillMaxWidth().padding(horizontal = 20.dp, vertical = 40.dp),
                    ) {
                        when (page) {
                            Page.About, Page.Cv -> AboutPage()
                            Page.Blog -> EmptyPage("Blog", "No posts yet.")
                            Page.Research -> ResearchPage()
                        }
                    }
                }
            }
        }
    }
}
