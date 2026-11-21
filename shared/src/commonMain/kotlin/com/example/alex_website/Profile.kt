package com.example.alex_website

import alex_website.shared.generated.resources.Res
import alex_website.shared.generated.resources.ic_email
import alex_website.shared.generated.resources.ic_github
import alex_website.shared.generated.resources.ic_linkedin
import alex_website.shared.generated.resources.ic_telegram
import org.jetbrains.compose.resources.DrawableResource

/** All personal content lives here — edit this file to update the site. */
object Profile {
    const val NAME = "Aleksandr Shefer"
    const val ROLE = "Software Engineer"
    const val COMPANY = "JetBrains"
    const val TEAM = "Kotlin"
    const val COMPANY_URL = "https://www.jetbrains.com"
    const val TEAM_URL = "https://kotlinlang.org"
    const val LOCATION = "Munich, Germany"

    // TODO: write your own description.
    // Markdown: [text](url) is a link; a single line break is just a space, a blank line starts a new paragraph.
    const val ABOUT = """
        I work on programming languages and compilers. Currently, I'm compiling Kotlin to WebAssembly.
        My focus is the performance of the generated binaries and support for new WebAssembly proposals.
        I redesigned core data structures in the Kotlin/Wasm backend, implemented Kotlin coroutines on top of the Stack Switching
        proposal, and worked on Kotlin/JavaScript interop.

        Before that, in JetBrains Research, I applied static and binding-time analysis to speed up
        [partial evaluation](https://www.sci.unich.it/hcvs24/papers/HCVS2024_paper_8.pdf) in Kanren languages family,
        and then designed agentic systems and tools for [code generation](https://arxiv.org/pdf/2503.14183) in verification frameworks. 
        
    """.trimIndent()

//        I'm a software engineer on the Kotlin/Wasm compiler backend team at JetBrains, working on how Kotlin compiles to WebAssembly — coroutines on top of stack switching, faster core data structures, and Kotlin/JavaScript interop.

    const val CV_URL = "cv/cv.pdf"

    val links = listOf(
        SocialLink("GitHub", "https://github.com/alex28sh", Res.drawable.ic_github),
        SocialLink("LinkedIn", "https://www.linkedin.com/in/alex-shefer/", Res.drawable.ic_linkedin),
        SocialLink("Telegram", "https://t.me/alexshefer", Res.drawable.ic_telegram),
        SocialLink("Email", "mailto:alex.shefer.31@gmail.com", Res.drawable.ic_email, copyText = "alex.shefer.31@gmail.com"),
    )
}

/** [copyText], when set, is also shown as selectable text with a copy button. */
data class SocialLink(val label: String, val url: String, val icon: DrawableResource, val copyText: String? = null)
