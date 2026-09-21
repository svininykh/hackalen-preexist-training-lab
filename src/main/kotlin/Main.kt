import java.io.File

private val wordRegex = Regex("[\\p{L}\\p{N}]+")
private val stopWords = setOf("а", "и", "в", "во", "на", "по", "с", "как", "какая", "какие", "какой", "у", "ли")

data class FaqItem(val question: String, val answer: String)

fun loadFaq(file: File): List<FaqItem> {
    val items = file.readText().split(Regex("\\n\\s*\\n"))
        .filter { it.isNotBlank() }
        .map { block ->
            val question = block.lines().firstOrNull { it.startsWith("Вопрос: ") }
                ?.removePrefix("Вопрос: ") ?: error("Некорректный вопрос в faq.txt")
            val answer = block.lines().firstOrNull { it.startsWith("Ответ: ") }
                ?.removePrefix("Ответ: ") ?: error("Некорректный ответ в faq.txt")
            FaqItem(question, answer)
        }
    require(items.size == 5) { "В faq.txt должно быть ровно 5 пар вопрос–ответ" }
    return items
}

fun findAnswer(userQuestion: String, faq: List<FaqItem>): String? {
    val userWords = words(userQuestion)
    if (userWords.isEmpty()) return null

    return faq.map { item -> item to userWords.intersect(words(item.question)).size }
        .filter { (_, matches) -> matches > 0 }
        .maxByOrNull { (_, matches) -> matches }
        ?.first?.answer
}

private fun words(text: String): Set<String> = wordRegex.findAll(text.lowercase())
    .map { it.value }.filterNot { it in stopWords }.toSet()

fun main() {
    val faq = loadFaq(File("faq.txt"))
    println("FAQ HackAlem")
    println("Введите вопрос (или «выход» для завершения):")
    while (true) {
        print("> ")
        val question = readlnOrNull()?.trim() ?: break
        if (question.lowercase() in setOf("выход", "exit", "quit")) break
        println(findAnswer(question, faq) ?: "не знаю")
    }
}
