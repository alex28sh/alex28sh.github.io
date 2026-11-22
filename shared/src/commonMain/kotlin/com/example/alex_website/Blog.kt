package com.example.alex_website

/**
 * A blog post. Posts are not written here: each one is a Markdown file in `blog/` at the repo root,
 * and the build generates the `blogPosts` list from them (see `generateBlogIndex` in shared/build.gradle.kts).
 */
data class BlogPost(
    val slug: String,
    val title: String,
    /** ISO date, YYYY-MM-DD. */
    val date: String,
    val summary: String,
    val body: String,
)

private val MONTHS = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

/** "2026-11-21" → "21 Nov 2026". */
internal fun formatDate(iso: String): String {
    val (y, m, d) = iso.split("-")
    return "${d.toInt()} ${MONTHS[m.toInt() - 1]} $y"
}
