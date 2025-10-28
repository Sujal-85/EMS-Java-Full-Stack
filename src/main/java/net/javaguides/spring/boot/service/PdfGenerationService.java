package net.javaguides.spring.boot.service;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import net.javaguides.spring.boot.entity.Salary;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfGenerationService {
    
    public byte[] generatePayslip(Salary salary) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Title
            Paragraph title = new Paragraph("PAYSLIP")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
            document.add(title);
            
            // Employee Details
            document.add(new Paragraph("Employee: " + salary.getEmployee().getFirstName() + 
                " " + salary.getEmployee().getLastName()));
            document.add(new Paragraph("Employee Code: " + salary.getEmployee().getEmployeeCode()));
            document.add(new Paragraph("Month/Year: " + salary.getMonth() + "/" + salary.getYear()));
            document.add(new Paragraph("Payment Date: " + salary.getPaymentDate()));
            document.add(new Paragraph("\n"));
            
            // Salary Details Table
            Table table = new Table(2);
            table.setWidth(400);
            
            table.addCell("Basic Salary");
            table.addCell(String.format("%.2f", salary.getBasicSalary()));
            
            if (salary.getHra() != null && salary.getHra() > 0) {
                table.addCell("HRA");
                table.addCell(String.format("%.2f", salary.getHra()));
            }
            
            if (salary.getTransportAllowance() != null && salary.getTransportAllowance() > 0) {
                table.addCell("Transport Allowance");
                table.addCell(String.format("%.2f", salary.getTransportAllowance()));
            }
            
            if (salary.getMedicalAllowance() != null && salary.getMedicalAllowance() > 0) {
                table.addCell("Medical Allowance");
                table.addCell(String.format("%.2f", salary.getMedicalAllowance()));
            }
            
            if (salary.getOtherAllowances() != null && salary.getOtherAllowances() > 0) {
                table.addCell("Other Allowances");
                table.addCell(String.format("%.2f", salary.getOtherAllowances()));
            }
            
            if (salary.getBonus() != null && salary.getBonus() > 0) {
                table.addCell("Bonus");
                table.addCell(String.format("%.2f", salary.getBonus()));
            }
            
            if (salary.getDeductions() != null && salary.getDeductions() > 0) {
                table.addCell("Deductions");
                table.addCell(String.format("-%.2f", salary.getDeductions()));
            }
            
            table.addCell("NET SALARY").setBold();
            table.addCell(String.format("%.2f", salary.getNetSalary())).setBold();
            
            document.add(table);
            
            if (salary.getRemarks() != null) {
                document.add(new Paragraph("\nRemarks: " + salary.getRemarks()));
            }
            
            document.close();
            return baos.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage());
        }
    }
}
