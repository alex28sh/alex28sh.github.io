package com.example.alex_website

import alex_website.shared.generated.resources.Res
import alex_website.shared.generated.resources.paper_canren
import alex_website.shared.generated.resources.paper_verification
import org.jetbrains.compose.resources.DrawableResource

/** Publications shown on the Research page — edit this file to add a paper. */
object Research {
    /** Newest first. Your own name in [Paper.authors] is highlighted automatically. */
    val papers = listOf(
        Paper(
            title = "Can LLMs Enable Verification in Mainstream Programming?",
            authors = listOf(Profile.NAME, "Igor Engel", "Stanislav Alekseev", "Daniil Berezun", "Ekaterina Verbitskaia", "Anton Podkopaev"),
            venue = "arXiv preprint, 2025",
            summary =
                "Verifiers give LLMs precise feedback, so their output can be proven correct rather than just tested. " +
                "We studied how well LLMs and agentic systems generate verified code in Dafny, Nagini, and Verus, " +
                "using collected and existing benchmarks.",
            url = "https://arxiv.org/abs/2503.14183",
            pdfUrl = "https://arxiv.org/pdf/2503.14183",
            preview = Res.drawable.paper_verification,
        ),
        Paper(
            title = "Integration of Offline Partial Deduction and Functional Conversion for miniKanren",
            authors = listOf(Profile.NAME, "Ekaterina Verbitskaia"),
            venue = "HCVS 2024 (extended abstract)",
            summary = "For many problems, verifying a solution is much easier than finding one. In a relational" +
                "language, a verifier can be run backwards as a solver, but naively this is slow. We combined mode " +
                "and binding-time analyses with partial deduction and other program transformations to make such" +
                "solvers fast.",
            url = "https://www.sci.unich.it/hcvs24/papers/HCVS2024_paper_8.pdf",
            pdfUrl = "https://www.sci.unich.it/hcvs24/papers/HCVS2024_paper_8.pdf",
            preview = Res.drawable.paper_canren,
        ),
    )
}

data class Paper(
    val title: String,
    val authors: List<String>,
    val venue: String,
    val summary: String,
    val url: String,
    val pdfUrl: String?,
    val preview: DrawableResource?,
)
