#!/usr/bin/env kscript

import java.io.File
import java.io.FileFilter
import java.io.FilenameFilter

object LinesStats {
    var java = 0L
    var kotlin = 0L

    val total
        get() = java + kotlin

    val javaPercent
        get() = (java * 100_00 / total).toFloat() / 100

    val kotlinPercent
        get() = (kotlin * 100_00 / total).toFloat() / 100
}

fun countLinesInFile(file: File) = file.inputStream().bufferedReader()
        .lines().filter { it.isNotBlank() }.count()

fun countLines(dir: File) {
    val javaFilter = FilenameFilter { _, name -> name.endsWith(".java") }
    val kotlinFilter = FilenameFilter { _, name -> name.endsWith(".kt") }

    dir.listFiles(javaFilter).forEach { LinesStats.java += countLinesInFile(it) }
    dir.listFiles(kotlinFilter).forEach { LinesStats.kotlin += countLinesInFile(it) }

    dir.listFiles(FileFilter { it.isDirectory }).forEach {
        countLines(it)
    }
}

fun run() {
    val dir = File("core/src/com/gdxjam/magellan")

    countLines(dir)

    println("""
    Total non-empty lines: ${LinesStats.total}

    In Java code: ${LinesStats.java} (${LinesStats.javaPercent}%)
    In Kotlin code: ${LinesStats.kotlin} (${LinesStats.kotlinPercent}%)
""".trimIndent())
}

run()