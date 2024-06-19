package com.ust.invoice.extract.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ust.invoice.extract.entity.InvoiceData;

public interface InvoiceDataRepository extends JpaRepository<InvoiceData, Long> {

	@Query("SELECT inv from InvoiceData inv where year(inv.dateOfPurchase) = :dateOfPurchaseYear and month(inv.dateOfPurchase) = :dateOfPurchaseMonth")
	List<InvoiceData> getByMonthAndYear(@Param("dateOfPurchaseYear") int dateOfPurchaseYear,
			@Param("dateOfPurchaseMonth") int dateOfPurchaseMonth);
}
