package com.ust.invoice.extract.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ExcelUtil {

	private ExcelUtil() {
	}

	public static void createExcelFileIfNotExists(String excelFilePath, List<String> cellHeaders) {
		File file = new File(excelFilePath);
		ZipSecureFile.setMinInflateRatio(0);
		if (!file.exists()) {
			log.info("Excel does not exist. Creating a new one");
			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("Invoice Data");
			Row headerRow = sheet.createRow(0);
			Font font = workbook.createFont();
			font.setBold(true);
			CellStyle headerCellStyle = getCellStyle(workbook);
			headerCellStyle.setFont(font);
			for (int i = 0; i < cellHeaders.size(); i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(cellHeaders.get(i));
				cell.setCellStyle(headerCellStyle);
			}

			try (FileOutputStream fos = new FileOutputStream(excelFilePath)) {
				workbook.write(fos);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			log.info("Excel {} already exists", excelFilePath);
		}
	}

	public static void appendDataToExcel(String excelFilePath, String pdfFileName, List<String> headers,
			Map<String, String> data) {
		try (FileInputStream fis = new FileInputStream(excelFilePath); Workbook workbook = new XSSFWorkbook(fis)) {
			Sheet sheet = workbook.getSheetAt(0);
			int lastRowNum = sheet.getLastRowNum();
			CellStyle cellStyle = getCellStyle(workbook);
			Row row = sheet.createRow(lastRowNum + 1);
			Cell nameCell = row.createCell(0);
			nameCell.setCellValue(pdfFileName);
			nameCell.setCellStyle(cellStyle);
			for (int i = 0; i < headers.size(); i++) {
				String value = data.get(headers.get(i));
				Cell cell = row.createCell(i + 1);
				cell.setCellValue(value != null ? value : "");
				cell.setCellStyle(cellStyle);
			}

			// Adjust column widths
			for (int i = 0; i < headers.size(); i++) {
				sheet.autoSizeColumn(i);
			}
			try (FileOutputStream fos = new FileOutputStream(excelFilePath)) {
				workbook.write(fos);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static CellStyle getCellStyle(Workbook workbook) {
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setWrapText(true);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		return cellStyle;
	}
}
