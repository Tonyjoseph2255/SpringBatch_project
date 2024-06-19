package com.ust.invoice.extract.read;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ust.invoice.extract.exceptions.NoPdfFilesFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ust.invoice.extract.constants.AppConstants;
import com.ust.invoice.extract.entity.InvoiceConfig;
import com.ust.invoice.extract.util.ExcelUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PdfInvoiceProcessor {

	@Value("${app.headers}")
	private List<String> headers;

	@Autowired
	private PdfDataExtractor pdfDataExtractor;

	public void execute(InvoiceConfig invoiceConfig) {
		List<String> cellHeaders = new ArrayList<>(headers);
		cellHeaders.add(0, "PDF Name");
		String excelPath = invoiceConfig.getExcelPath();
		ExcelUtil.createExcelFileIfNotExists(excelPath, cellHeaders);
		String invoicePath = invoiceConfig.getInvoicePath();
		File inputDirectory = new File(invoicePath);
		File[] files = inputDirectory.listFiles();
		int numOfPdfs = 0;
		if (files != null) {
			for (File file : files) {
				if (file.isFile() && file.getName().toLowerCase().endsWith(AppConstants.PDF_EXTENSION)) {
					pdfDataExtractor.processPDF(file, invoiceConfig.getArchivePath(), excelPath);
					numOfPdfs++;
				}
			}

		}
		if (numOfPdfs > 0) {
			log.info("{} PDF documents processed.", numOfPdfs);
		} else {
			log.info("No PDF files found in the {} directory.", invoicePath);
			String errorMessage = "No PDF files found in the " + invoicePath + " directory.";
			log.error(errorMessage);
			throw new NoPdfFilesFoundException( errorMessage);
		}

	}

}
