package com.ust.invoice.extract.read;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;

public class PdfInvoiceExtractionStrategy extends LocationTextExtractionStrategy {

	private static final String CURRENCY = "CURRENCY";
	private static final String TOTAL_ORDEN = "TOTAL ORDEN";
	
	private final Map<String, String> extractedData = new LinkedHashMap<>();
	private String lastHeader = null;
	private StringBuilder valueBuilder = new StringBuilder();
	private boolean captureValue = false;

	@Override
	public void eventOccurred(IEventData data, EventType type) {
		if (type == EventType.RENDER_TEXT) {
			TextRenderInfo renderInfo = (TextRenderInfo) data;
			String text = renderInfo.getText();
			if (isHeader(renderInfo, text)) {
				if (lastHeader != null) {
					addRowData();
					valueBuilder.setLength(0); // Clear the builder for the next value
				}
				lastHeader = text;
				captureValue = true;
			} else if (captureValue) {
				if (text.trim().isEmpty()) {
					// End of multi-line value
					captureValue = false;
					addRowData();
					lastHeader = null;
					valueBuilder.setLength(0); // Clear the builder
				} else {
					valueBuilder.append(text).append("\n");
				}
			} else {
				// Handle case where value is on the same line as header
				if (lastHeader != null) {
					valueBuilder.append(text).append(" ");
					addRowData();
					lastHeader = null;
					valueBuilder.setLength(0); // Clear the builder
				}
			}
		}
	}

	private void addRowData() {
		extractedData.put(lastHeader.replace(":", "").trim().strip(), valueBuilder.toString().trim());
	}

	private boolean isHeader(TextRenderInfo renderInfo, String text) {
		// Check if the text is bold and ends with a colon
		boolean isBold = false;

		String fontName = renderInfo.getFont().getFontProgram().getFontNames().getFontName();
		if (fontName.toLowerCase().contains("bold") || fontName.toLowerCase().contains("black")) {
			isBold = true;
		}

		return isBold || text.endsWith(":");
	}

	public void formatData() {
		handleLastHeader();
		List<String> headerData = List.of(TOTAL_ORDEN, CURRENCY);
		Map<String, List<String>> headers = Map.of(TOTAL_ORDEN, headerData);
		for (Map.Entry<String, List<String>> headerEntry : headers.entrySet()) {
			String data = extractedData.get(headerEntry.getKey());
			String[] split = data.split("\\r?\\n");
			List<String> valuesList = headerEntry.getValue();
			for (int i = 0; i < valuesList.size(); i++) {
				extractedData.put(valuesList.get(i), split[i].trim().strip());
			}
		}
	}

	private void handleLastHeader() {
		if (lastHeader != null) { // Add the last header-value pair if any
			addRowData();
			lastHeader = null;
			valueBuilder.setLength(0); // Clear the builder
		}
	}

	public Map<String, String> getExtractedData() {
		return extractedData;
	}
}
