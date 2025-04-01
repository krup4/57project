import javax.print.PrintServiceLookup
import javax.print.attribute.HashPrintRequestAttributeSet
import java.io.FileInputStream
import javax.print.DocFlavor
import javax.print.PrintService
import javax.print.SimpleDoc

fun printTextFile(filePath: String, printerName: String? = null) {
    val printService = if (printerName != null) {
        PrintServiceLookup.lookupPrintServices(null, null)
            .firstOrNull { it.name == printerName }
    } else {
        PrintServiceLookup.lookupDefaultPrintService()
    }

    if (printService == null) {
        println("Принтер не найден")
        return
    }

    FileInputStream(filePath).use { fis ->
        val doc = SimpleDoc(fis, DocFlavor.INPUT_STREAM.AUTOSENSE, null)
        val job = printService.createPrintJob()
        job.print(doc, HashPrintRequestAttributeSet())
    }
}

// Использование:
fun main() {
//    printTextFile("126103650.png") // или без имени для принтера по умолчанию
    val printers: Array<PrintService> = PrintServiceLookup.lookupPrintServices(null, null)

    if (printers.isEmpty()) {
        println("Нет доступных принтеров.")
        return
    }

    println("Доступные принтеры:")
    printers.forEachIndexed { index, printer ->
        println("${index + 1}. ${printer.name}")
    }
}