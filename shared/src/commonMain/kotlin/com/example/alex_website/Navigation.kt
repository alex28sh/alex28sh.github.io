package com.example.alex_website

/** Header navigation items. */
enum class Page(val title: String) {
    About("About me"),
    Cv("CV"),
    Blog("Blog"),
    Research("Research"),
}

/** Where the visitor is — the single source of truth for what the site shows. */
sealed interface Route {
    /** The header item highlighted for this route. */
    val tab: Page

    data object About : Route { override val tab = Page.About }
    data object Research : Route { override val tab = Page.Research }
    data object BlogList : Route { override val tab = Page.Blog }
    data class Post(val slug: String) : Route { override val tab = Page.Blog }
}

/** The route a header item leads to; null for items that open an external link instead. */
internal val Page.route: Route?
    get() = when (this) {
        Page.About -> Route.About
        Page.Research -> Route.Research
        Page.Blog -> Route.BlogList
        Page.Cv -> null
    }
