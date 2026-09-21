import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class FaqBotTest {
    private val faq = loadFaq(File("faq.txt"))

    @Test fun `returns answer for known question`() {
        assertEquals("Обучение начинается в 10:00 по местному времени.", findAnswer("Во сколько начинается обучение?", faq))
    }

    @Test fun `finds answer for another wording`() {
        assertEquals("Обучение начинается в 10:00 по местному времени.", findAnswer("Когда начинается обучение?", faq))
    }

    @Test fun `returns null for unknown question`() {
        assertEquals(null, findAnswer("Какая сегодня погода?", faq))
    }
}
