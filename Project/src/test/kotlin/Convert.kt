import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.usermodel.DataFormatter
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import javax.imageio.ImageIO

/**
 * Конвертирует файл в PDF.
 * Поддерживаемые форматы: TXT, CSV, JPG, PNG, DOCX, XLSX.
 * Для других форматов (PPTX, HTML и т. д.) используйте внешние инструменты.
 *
 * @param inputPath Путь к исходному файлу.
 * @param outputPdfPath Путь для сохранения PDF.
 * @throws IOException Если конвертация невозможна.
 */
fun convertToPdf(inputPath: String, outputPdfPath: String) {
    val file = File(inputPath)
    if (!file.exists()) throw IOException("Файл не найден: $inputPath")

    when (file.extension.lowercase()) {
        "txt", "csv", "log" -> convertTextToPdf(file, outputPdfPath)
        "jpg", "jpeg", "png", "bmp" -> convertImageToPdf(file, outputPdfPath)
        "docx", "doc" -> convertWordToPdf(file, outputPdfPath)
        "xlsx", "xls" -> convertExcelToPdf(file, outputPdfPath)
        else -> throw IOException("Формат файла не поддерживается: ${file.extension}")
    }
}

// Конвертация текстовых файлов
private fun convertTextToPdf(file: File, outputPdfPath: String) {
    val document = PDDocument()
    try {
        val page = PDPage(PDRectangle.A4)
        document.addPage(page)

        val contentStream = PDPageContentStream(document, page)
        val text = file.readText(Charsets.UTF_8)

        contentStream.beginText()
        contentStream.setFont(PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN), 12f)
        contentStream.newLineAtOffset(50f, 750f)
        contentStream.showText(text)
        contentStream.endText()
        contentStream.close()

        document.save(outputPdfPath)
    } finally {
        document.close()
    }
}

// Конвертация изображений
private fun convertImageToPdf(file: File, outputPdfPath: String) {
    val document = PDDocument()
    try {
        val page = PDPage(PDRectangle.A4)
        document.addPage(page)

        val image = PDImageXObject.createFromFile(file.path, document)
        val contentStream = PDPageContentStream(document, page)

        val scale = minOf(
            PDRectangle.A4.width / image.width,
            PDRectangle.A4.height / image.height
        ) * 0.9f

        contentStream.drawImage(
            image,
            50f, 50f,
            image.width * scale,
            image.height * scale
        )
        contentStream.close()

        document.save(outputPdfPath)
    } finally {
        document.close()
    }
}

// Конвертация Word (DOCX/DOC)
private fun convertWordToPdf(file: File, outputPdfPath: String) {
    val wordDoc = FileInputStream(file).use { fis -> XWPFDocument(fis) }
    val document = PDDocument()
    try {
        val page = PDPage(PDRectangle.A4)
        document.addPage(page)

        val contentStream = PDPageContentStream(document, page)
        val text = wordDoc.paragraphs.joinToString("\n") { it.text }

        contentStream.beginText()
        contentStream.setFont(PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN), 12f)
        contentStream.newLineAtOffset(50f, 750f)
        contentStream.showText(text)
        contentStream.endText()
        contentStream.close()

        document.save(outputPdfPath)
    } finally {
        document.close()
        wordDoc.close()
    }
}

// Конвертация Excel (XLSX/XLS)
private fun convertExcelToPdf(file: File, outputPdfPath: String) {
    val workbook = FileInputStream(file).use { fis -> WorkbookFactory.create(fis) }
    val document = PDDocument()
    try {
        val sheet = workbook.getSheetAt(0)
        val page = PDPage(PDRectangle.A4)
        document.addPage(page)

        val contentStream = PDPageContentStream(document, page)
        contentStream.beginText()
        contentStream.setFont(PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN), 10f)
        contentStream.newLineAtOffset(50f, 750f)

        val dataFormatter = DataFormatter()
        for (row in sheet) {
            val rowText = row.joinToString(" | ") { cell ->
                dataFormatter.formatCellValue(cell).take(30)
            }
            contentStream.showText(rowText)
            contentStream.newLineAtOffset(0f, -15f)
        }

        contentStream.endText()
        contentStream.close()
        document.save(outputPdfPath)
    } finally {
        document.close()
        workbook.close()
    }
}

// Пример использования
fun main() {
    try {
        convertToPdf("Project/src/test/kotlin/3.docx", "3(2).pdf")
        println("Конвертация завершена успешно!")
    } catch (e: IOException) {
        println("Ошибка: ${e.message}")
    }
}