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

@Composable
fun App() {
    val systemDark = isSystemInDarkTheme()
    var darkOverride by remember { mutableStateOf<Boolean?>(null) }
    val dark = darkOverride ?: systemDark
    var route by remember { mutableStateOf<Route>(Route.About) }
    val scroll = rememberScrollState()
    // Every navigation starts at the top of the page.
    LaunchedEffect(route) { scroll.scrollTo(0) }
    val uriHandler = LocalUriHandler.current

    SiteTheme(dark) {
        val colors = MaterialTheme.colorScheme
        Column(Modifier.fillMaxSize().background(colors.background)) {
            Header(
                current = route.tab,
                dark = dark,
                onToggleTheme = { darkOverride = !dark },
                onNavigate = { page ->
                    when (val target = page.route) {
                        null -> uriHandler.openUri(Profile.CV_URL)  // CV is a PDF, not a page
                        else -> route = target
                    }
                },
            )
            HorizontalDivider(color = colors.outlineVariant)
            Box(
                Modifier.fillMaxSize().verticalScroll(scroll),
                contentAlignment = Alignment.TopCenter,
            ) {
                SelectionContainer {
                    Column(
                        Modifier.widthIn(max = 860.dp).fillMaxWidth().padding(horizontal = 20.dp, vertical = 40.dp),
                    ) {
                        when (val r = route) {
                            Route.About -> AboutPage()
                            Route.Research -> ResearchPage()
                            Route.BlogList -> BlogPage(onOpen = { route = Route.Post(it.slug) })
                            is Route.Post -> BlogPostPage(r.slug, onBack = { route = Route.BlogList })
                        }
                    }
                }
            }
        }
    }
}
