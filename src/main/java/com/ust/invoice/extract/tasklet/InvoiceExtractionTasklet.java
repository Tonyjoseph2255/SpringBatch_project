package com.ust.invoice.extract.tasklet;

import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.ust.invoice.extract.entity.InvoiceConfig;
import com.ust.invoice.extract.read.PdfInvoiceProcessor;
import com.ust.invoice.extract.repository.InvoiceConfigRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@AllArgsConstructor
public class InvoiceExtractionTasklet implements Tasklet {

	private InvoiceConfigRepository invoiceConfigRepository;

	private PdfInvoiceProcessor pdfInvoiceProcessor;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		log.info("Starting job");
		List<InvoiceConfig> invoiceConfigs = invoiceConfigRepository.findAll();
		log.info("Got invoice configs {}", invoiceConfigs.size());
		for (InvoiceConfig invoiceConfig : invoiceConfigs) {
			pdfInvoiceProcessor.execute(invoiceConfig);
		}
		log.info("Completed batch processing");
		return RepeatStatus.FINISHED;
	}
}
