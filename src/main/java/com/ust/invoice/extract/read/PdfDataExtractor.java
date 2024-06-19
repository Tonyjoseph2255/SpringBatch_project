package com.ust.invoice.extract.read;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.ust.invoice.extract.constants.AppConstants;
import com.ust.invoice.extract.entity.InvoiceData;
import com.ust.invoice.extract.repository.InvoiceDataRepository;
import com.ust.invoice.extract.util.DateTimeUtil;
import com.ust.invoice.extract.util.ExcelUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PdfDataExtractor {

	@Value("${app.headers}")
	private List<String> headers;

	@Autowired
	private InvoiceDataRepository invoiceDataRepository;

	@Transactional
	public void processPDF(File pdfFile, String processedPath, String excelFilePath) {
		try {
			PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfFile));
			PdfInvoiceExtractionStrategy strategy = new PdfInvoiceExtractionStrategy();
			PdfCanvasProcessor processor = new PdfCanvasProcessor(strategy);
			for (int pageNum = 1; pageNum <= pdfDoc.getNumberOfPages(); pageNum++) {
				processor.processPageContent(pdfDoc.getPage(pageNum));
			}
			strategy.formatData();
			Map<String, String> extractedData = strategy.getExtractedData();
			String pdfFileName = pdfFile.getName().replace(AppConstants.PDF_EXTENSION, "");
			pdfDoc.close(); // Close the PDF document
			saveData(pdfFileName, extractedData);
			ExcelUtil.appendDataToExcel(excelFilePath, pdfFileName, headers, extractedData);
			archiveProcessed(pdfFile, processedPath);
		} catch (IOException e) {
			log.error("Got error while parsing invoice", e);
		}
	}

	private void saveData(String pdfFileName, Map<String, String> extractedData) {
		InvoiceData invoiceData = setInvoiceData(pdfFileName, extractedData);
		invoiceDataRepository.save(invoiceData);
		log.info("Invoice Data saved to DB");
	}

	private InvoiceData setInvoiceData(String pdfFileName, Map<String, String> extractedData) {
		InvoiceData invoiceData = new InvoiceData();
		invoiceData.setPdfName(pdfFileName);
		for (Map.Entry<String, String> data : extractedData.entrySet()) {
			String trimmedValue = getTrimmedValue(data);
			switch (data.getKey()) {
			case AppConstants.ORDEN_DE_COMPRA_NO:
				invoiceData.setPurchaseOrderNumber(trimmedValue);
				break;
			case AppConstants.FECHA_DE_COMPRA:
				invoiceData.setDateOfPurchase(DateTimeUtil.getDate(trimmedValue));
				break;
			case AppConstants.ORDEN_ELAB_POR:
				invoiceData.setProcessingOrderBy(trimmedValue);
				break;
			case AppConstants.EMAIL_ELAB_POR:
				invoiceData.setProcessingOrderByEmail(trimmedValue);
				break;
			case AppConstants.FECHA_ENTREGA:
				invoiceData.setDeliveryDate(DateTimeUtil.getDate(trimmedValue));
				break;
			case AppConstants.NO_DE_PROYECTO:
				invoiceData.setProjectNumber(trimmedValue);
				break;
			case AppConstants.PLAZO_DE_PAGO:
				invoiceData.setPaymentDeadline(trimmedValue);
				break;
			case AppConstants.INFORMACION_DE_PROVEEDOR:
				invoiceData.setSupplierInformation(trimmedValue);
				break;
			case AppConstants.PROVEEDOR_SAP_LEGACY:
				invoiceData.setSapLegacySupplier(trimmedValue);
				break;
			case AppConstants.TELEFONO:
				invoiceData.setTelephoneNumber(trimmedValue);
				break;
			case AppConstants.FAX:
				invoiceData.setFaxNumber(trimmedValue);
				break;
			case AppConstants.REFERENCIA:
				invoiceData.setReferenceNumber(trimmedValue);
				break;
			case AppConstants.GRUPO_DE_COMPRAS:
				invoiceData.setShoppingGroup(trimmedValue);
				break;
			case AppConstants.CONTACTO_PROVEEDOR:
				invoiceData.setSupplierContact(trimmedValue);
				break;
			case AppConstants.EMAIL_CONTACTO_PROV:
				invoiceData.setSupplierContactEmail(trimmedValue);
				break;
			case AppConstants.LUGAR_DE_ENTREGA:
				invoiceData.setPlaceOfDelivery(trimmedValue);
				break;
			case AppConstants.CONTACTO_EN_ENTREGA:
				invoiceData.setContactOnDelivery(trimmedValue);
				break;
			case AppConstants.TOTAL_ORDEN:
				setTotalDelivery(invoiceData, trimmedValue);
				break;
			case AppConstants.CURRENCY:
				invoiceData.setCurrency(trimmedValue);
				break;
			case AppConstants.CONDICIONES:
				invoiceData.setConditions(trimmedValue);
				break;
			case AppConstants.FACTURAR_A:
				invoiceData.setInvoiceTo(trimmedValue);
				break;
			case AppConstants.COMENTARIOS:
				invoiceData.setComments(trimmedValue);
				break;
			case AppConstants.INSTRUCCIONES_DE_EMBARQUE:
				invoiceData.setBoardingInstructions(trimmedValue);
				break;
			default:
				break;
			}
		}
		return invoiceData;
	}

	private void setTotalDelivery(InvoiceData invoiceData, String trimmedValue) {
		String pattern = "#,###.0#";
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setGroupingSeparator(',');
		symbols.setDecimalSeparator('.');
		DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
		decimalFormat.setParseBigDecimal(true);
		try {
			BigDecimal totalDelivery = (BigDecimal) decimalFormat.parse(trimmedValue);
			invoiceData.setTotalDelivery(totalDelivery);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private String getTrimmedValue(Map.Entry<String, String> data) {
		return data.getValue().trim().strip();
	}

	private void archiveProcessed(File pdfFile, String processedPath) throws IOException {
		// Move processed file to the output directory
		File outputFile = new File(processedPath, pdfFile.getName());
		FileUtils.moveFile(pdfFile, outputFile);
		log.info("Moved processed file: " + pdfFile.getName());
	}
}
