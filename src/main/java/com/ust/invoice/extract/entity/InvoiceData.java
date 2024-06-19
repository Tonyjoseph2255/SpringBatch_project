package com.ust.invoice.extract.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.ust.invoice.extract.annotations.FieldAlias;
import com.ust.invoice.extract.constants.AppConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 2000, nullable = false)
	private String pdfName;

	// ORDEN DE COMPRA NO
	@FieldAlias(AppConstants.ORDEN_DE_COMPRA_NO)
	@Column(length = 2000)
	private String purchaseOrderNumber;

	// FECHA DE COMPRA
	@FieldAlias(AppConstants.FECHA_DE_COMPRA)
	@Column
	private LocalDate dateOfPurchase;

	// ORDEN ELAB. POR
	@FieldAlias(AppConstants.ORDEN_ELAB_POR)
	@Column(length = 2000)
	private String processingOrderBy;

	// EMAIL ELAB POR
	@FieldAlias(AppConstants.EMAIL_ELAB_POR)
	@Column(length = 2000)
	private String processingOrderByEmail;

	// FECHA ENTREGA
	@FieldAlias(AppConstants.FECHA_ENTREGA)
	@Column
	private LocalDate deliveryDate;

	// NO. DE PROYECTO
	@FieldAlias(AppConstants.NO_DE_PROYECTO)
	@Column(length = 2000)
	private String projectNumber;

	// PLAZO DE PAGO
	@FieldAlias(AppConstants.PLAZO_DE_PAGO)
	@Column(length = 2000)
	private String paymentDeadline;

	// INFORMACION DE PROVEEDOR
	@FieldAlias(AppConstants.INFORMACION_DE_PROVEEDOR)
	@Column(length = 2000)
	private String supplierInformation;

	// PROVEEDOR SAP\LEGACY
	@FieldAlias(AppConstants.PROVEEDOR_SAP_LEGACY)
	@Column(length = 2000)
	private String sapLegacySupplier;

	// TELEFONO
	@FieldAlias(AppConstants.TELEFONO)
	@Column(length = 2000)
	private String telephoneNumber;

	// FAX
	@FieldAlias(AppConstants.FAX)
	@Column(length = 2000)
	private String faxNumber;

	// REFERENCIA #
	@FieldAlias(AppConstants.REFERENCIA)
	@Column(length = 2000)
	private String referenceNumber;

	// GRUPO DE COMPRAS
	@FieldAlias(AppConstants.GRUPO_DE_COMPRAS)
	@Column(length = 2000)
	private String shoppingGroup;

	// CONTACTO PROVEEDOR
	@FieldAlias(AppConstants.CONTACTO_PROVEEDOR)
	@Column(length = 2000)
	private String supplierContact;

	// EMAIL CONTACTO PROV
	@FieldAlias(AppConstants.EMAIL_CONTACTO_PROV)
	@Column(length = 2000)
	private String supplierContactEmail;

	// LUGAR DE ENTREGA
	@FieldAlias(AppConstants.LUGAR_DE_ENTREGA)
	@Column(length = 2000)
	private String placeOfDelivery;

	// CONTACTO EN ENTREGA
	@FieldAlias(AppConstants.CONTACTO_EN_ENTREGA)
	@Column(length = 2000)
	private String contactOnDelivery;

	// TOTAL ORDEN
	@FieldAlias(AppConstants.TOTAL_ORDEN)
	@Column
	private BigDecimal totalDelivery;

	// CURRENCY
	@FieldAlias(AppConstants.CURRENCY)
	@Column(length = 2000)
	private String currency;

	// CONDICIONES
	@FieldAlias(AppConstants.CONDICIONES)
	@Column(length = 2000)
	private String conditions;

	// FACTURAR A.
	@FieldAlias(AppConstants.FACTURAR_A)
	@Column(length = 2000)
	private String invoiceTo;

	// COMENTARIOS
	@FieldAlias(AppConstants.COMENTARIOS)
	@Column(length = 2000)
	private String comments;

	// INSTRUCCIONES DE EMBARQUE
	@FieldAlias(AppConstants.INSTRUCCIONES_DE_EMBARQUE)
	@Column(length = 2000)
	private String boardingInstructions;

}
