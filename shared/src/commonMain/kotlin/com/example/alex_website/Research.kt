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
            summary = "We study whether LLMs can generate formally verified code in three verification languages — " +
                "Dafny, Nagini, and Verus — using curated benchmarks derived from HumanEval, and what information " +
                "the model needs to succeed.",
            url = "https://arxiv.org/abs/2503.14183",
            pdfUrl = "https://arxiv.org/pdf/2503.14183",
            preview = Res.drawable.paper_verification,
        ),
        Paper(
            title = "Integration of Offline Partial Deduction and Functional Conversion for miniKanren",
            authors = listOf(Profile.NAME, "Ekaterina Verbitskaia"),
            venue = "HCVS 2024 (extended abstract)",
            summary = "Partial deduction and functional conversion speed up relational programs when the direction " +
                "of a query is known. We integrate offline partial deduction with functional conversion and obtain " +
                "larger speedups than either transformation alone.",
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
