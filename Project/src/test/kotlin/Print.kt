import javax.print.*
import javax.print.attribute.HashPrintRequestAttributeSet
import java.io.FileInputStream

fun printFile(filePath: String) {
    val inputStream = FileInputStream(filePath)
    val flavor = DocFlavor.INPUT_STREAM.AUTOSENSE
    val doc = SimpleDoc(inputStream, flavor, null)

    val printers = PrintServiceLookup.lookupPrintServices(flavor, null)
    if (printers.isEmpty()) {
        println("Нет доступных принтеров!")
        return
    }

    val printer = printers[2] // Берем первый принтер
    println("Печать на: ${printer.name}")

    printer.createPrintJob().print(doc, null)
    Thread.sleep(1000) // Даем время на отправку

    inputStream.close()
    println("Файл должен быть в очереди печати.")
}

// Пример использования
fun main() {
    printFile("Project/src/test/kotlin/1.png")
}