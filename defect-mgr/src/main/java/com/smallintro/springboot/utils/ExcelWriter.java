package com.smallintro.springboot.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smallintro.springboot.entity.DefectInfo;

@Service
public class ExcelWriter {

	private static String[] defectSheetHeader = { "Defect ID", "Description", "Feature", "Found Date", "Fix Date",
			"Tester", "Developer", "Status", "Dev Remark", "Test Remark", "Phase" };

	@Autowired
	ApplicationProperties appProperties;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void writeDataToFile(List contentForFile, String contentType) {

		Workbook workbook = new XSSFWorkbook();

		/*
		 * CreationHelper helps us create instances for various things like DataFormat,
		 * Hyperlink, RichTextString etc in a format (HSSF, XSSF) independent way
		 */
		CreationHelper createHelper = workbook.getCreationHelper();

		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 14);
		headerFont.setColor(IndexedColors.BLUE.getIndex());

		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);

		CellStyle dateCellStyle = workbook.createCellStyle();
		dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat(appProperties.getFileDateFormat()));

		if (ProjectConstants.DATA_TYPE_DEFECTS.equals(contentType)) {
			buildDefectData(contentForFile, workbook, headerCellStyle, dateCellStyle);

		}

		FileOutputStream fileOut;
		try {
			String fileAbsolutePath = appProperties.getFileExportPath() + appProperties.getFileExportName()
					+ contentType + ProjectConstants.EXCEL_FILE_SUFFIX;
			fileOut = new FileOutputStream(fileAbsolutePath);

			workbook.write(fileOut);
			fileOut.close();
			workbook.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void buildDefectData(List<DefectInfo> defectList, Workbook workbook, CellStyle headerCellStyle,
			CellStyle dateCellStyle) {

		Sheet sheet = workbook.createSheet(ProjectConstants.DATA_TYPE_DEFECTS);

		Row headerRow = sheet.createRow(0);

		for (int i = 0; i < defectSheetHeader.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(defectSheetHeader[i]);
			cell.setCellStyle(headerCellStyle);
		}

		int rowNum = 1;
		for (DefectInfo defect : defectList) {
			int colIndex = 0;
			Row row = sheet.createRow(rowNum++);
			row.createCell(colIndex++).setCellValue(defect.getDefectId());
			row.createCell(colIndex++).setCellValue(defect.getDefectDesc());
			row.createCell(colIndex++).setCellValue(defect.getFeature());

			row.createCell(colIndex++).setCellStyle(dateCellStyle);
			row.getCell(colIndex - 1).setCellValue(defect.getDefectDate());

			row.createCell(colIndex++).setCellStyle(dateCellStyle);
			row.getCell(colIndex - 1).setCellValue(defect.getSolutionDate());

			row.createCell(colIndex++).setCellValue(defect.getTesterName());
			row.createCell(colIndex++).setCellValue(defect.getDeveloperName());
			row.createCell(colIndex++).setCellValue(defect.getDefectStatus());
			row.createCell(colIndex++).setCellValue(defect.getDevRemark());
			row.createCell(colIndex++).setCellValue(defect.getTestRemark());
		}

		for (int i = 0; i < defectSheetHeader.length; i++) {
			sheet.autoSizeColumn(i);
		}
	}

}
