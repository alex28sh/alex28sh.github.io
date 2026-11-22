import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

/**
 * Turns every Markdown file in `blog/` (repo root) into a generated `BlogPosts.kt`, so a post is just a Markdown file.
 * Files starting with `_` (templates) and posts with `draft: true` are skipped.
 */
abstract class GenerateBlogIndex : DefaultTask() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val postsDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    /** Post images (`blog/images/`) are copied here as Compose resources, readable as `files/blog/images/...`. */
    @get:OutputDirectory
    abstract val resourcesDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val imagesOut = resourcesDir.get().asFile.resolve("files/blog/images")
        imagesOut.deleteRecursively()
        postsDir.get().asFile.resolve("images").takeIf { it.isDirectory }?.copyRecursively(imagesOut)

        val dateRegex = Regex("""\d{4}-\d{2}-\d{2}""")
        val posts = postsDir.get().asFile.listFiles { f -> f.extension == "md" && !f.name.startsWith("_") }
            .orEmpty()
            .map { file ->
                val text = file.readText().replace("\r\n", "\n")
                require(text.startsWith("---\n")) { "${file.name}: missing front matter (--- ... ---) at the top" }
                val end = text.indexOf("\n---", 4)
                require(end >= 0) { "${file.name}: front matter is not closed with ---" }
                // `key: value` lines. Indented lines continue the previous value (joined with spaces),
                // so long values can be wrapped YAML-style, optionally after a `>` marker:
                //   summary: >
                //     first line
                //     second line
                val meta = linkedMapOf<String, String>()
                var lastKey: String? = null
                for (line in text.substring(4, end).lines().filter { it.isNotBlank() }) {
                    if (line[0].isWhitespace()) {
                        val key = lastKey ?: error("${file.name}: indented line '$line' has no key above it")
                        meta[key] = (meta.getValue(key) + " " + line.trim()).trim()
                        continue
                    }
                    val parts = line.split(":", limit = 2)
                    require(parts.size == 2) { "${file.name}: bad front matter line '$line' (expected 'key: value')" }
                    val key = parts[0].trim()
                    meta[key] = parts[1].trim().let { if (it == ">") "" else it }
                    lastKey = key
                }
                meta.replaceAll { _, v -> v.removeSurrounding("\"") }
                val date = meta["date"] ?: error("${file.name}: 'date' is required")
                require(dateRegex.matches(date)) { "${file.name}: date must be YYYY-MM-DD, got '$date'" }
                mapOf(
                    "slug" to file.nameWithoutExtension,
                    "title" to (meta["title"] ?: error("${file.name}: 'title' is required")),
                    "date" to date,
                    "summary" to meta["summary"].orEmpty(),
                    "draft" to meta["draft"].orEmpty(),
                    "body" to text.substring(text.indexOf('\n', end + 1).let { if (it < 0) text.length else it + 1 }).trim(),
                )
            }
            .filter { it["draft"] != "true" }
            .sortedByDescending { it["date"] }

        fun lit(s: String) = buildString {
            append('"')
            for (c in s) when (c) {
                '\\' -> append("\\\\"); '"' -> append("\\\""); '$' -> append("\\$")
                '\n' -> append("\\n"); '\t' -> append("\\t"); else -> append(c)
            }
            append('"')
        }
        val out = outputDir.get().asFile.resolve("com/example/alex_website/BlogPosts.kt")
        out.parentFile.mkdirs()
        out.writeText(buildString {
            appendLine("// Generated from blog/ by :shared:generateBlogIndex — do not edit.")
            appendLine("package com.example.alex_website")
            appendLine()
            appendLine("internal val blogPosts: List<BlogPost> = listOf(")
            posts.forEach { p ->
                appendLine("    BlogPost(")
                for (k in listOf("slug", "title", "date", "summary", "body")) appendLine("        $k = ${lit(p.getValue(k))},")
                appendLine("    ),")
            }
            appendLine(")")
        })
    }
}

val generateBlogIndex = tasks.register<GenerateBlogIndex>("generateBlogIndex") {
    postsDir.set(rootProject.layout.projectDirectory.dir("blog"))
    outputDir.set(layout.buildDirectory.dir("generated/blog/kotlin"))
    resourcesDir.set(layout.buildDirectory.dir("generated/blog/composeResources"))
}

// Post images live in a separate source set's resources so commonMain keeps its default composeResources dir.
compose.resources {
    customDirectory(sourceSetName = "webMain", directoryProvider = generateBlogIndex.flatMap { it.resourcesDir })
}

kotlin {
    js {
        browser()
        binaries.executable()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }
    sourceSets {
        commonMain {
            kotlin.srcDir(generateBlogIndex.flatMap { it.outputDir })
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.markdown.renderer.m3)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jsMain.dependencies {
            implementation(libs.wrappers.browser)
        }
    }
}
