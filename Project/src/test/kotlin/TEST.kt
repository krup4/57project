import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.printing.PDFPageable
import java.awt.print.PrinterJob
import java.io.File

fun printPdf(filePath: String) {
    try {
        val document = Loader.loadPDF(File(filePath))
        val job = PrinterJob.getPrinterJob()
        job.setPageable(PDFPageable(document))

        job.print()
        document.close()
    } catch (e: Exception) {
        println("Ошибка печати PDF: ${e.message}")
    }
}

fun main() {
    printPdf("Project/src/test/kotlin/3.pdf")
}

