package com.jtk.ps.api.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jtk.ps.api.dto.SeminarTotalValueDto;
import com.jtk.ps.api.dto.SeminarValueParticipantDto;
import com.jtk.ps.api.model.SeminarCriteria;
import com.jtk.ps.api.model.Tutorial;

public class ExcelHelper {
    
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = { "Id", "Title", "Description", "Published" };
    static String SHEET = "Tutorials";
  
    public static ByteArrayInputStream tutorialsToExcel(List<Tutorial> tutorials) {
  
      try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
        Sheet sheet = workbook.createSheet(SHEET);
  
        // Header
        Row headerRow = sheet.createRow(0);
  
        for (int col = 0; col < HEADERs.length; col++) {
          Cell cell = headerRow.createCell(col);
          cell.setCellValue(HEADERs[col]);
          sheet.addMergedRegion(new CellRangeAddress(0, 1, col,col));
        }
  
        int rowIdx = 2;
        for (Tutorial tutorial : tutorials) {
          Row row = sheet.createRow(rowIdx++);
  
          row.createCell(0).setCellValue(tutorial.getId());
          row.createCell(1).setCellValue(tutorial.getTitle());
          row.createCell(2).setCellValue(tutorial.getDescription());
          row.createCell(3).setCellValue(tutorial.isPublished());
        }
  
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
      } catch (IOException e) {
        throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
      }
    }

    public static ByteArrayInputStream recapSeminartoExcelByType(List<SeminarValueParticipantDto> list,String sheetName, List<SeminarCriteria> listCriteria){
        String[] headerNames = { "No", "NIM", "Nama", "Rekapitulasi Nilai Seminar", "Nilai Total" };
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
          Sheet sheet = workbook.createSheet(sheetName);
          // style
          CellStyle boldCellStyle = workbook.createCellStyle();
          Font boldFont = workbook.createFont();
          boldFont.setBold(true);
          boldCellStyle.setFont(boldFont);

          // Header
          Row headerRow = sheet.createRow(0);
    
          for (int col = 0; col < headerNames.length; col++) {
              if(col < 3){
                  Cell cell = headerRow.createCell(col);
                  cell.setCellValue(headerNames[col]);
                  sheet.addMergedRegion(new CellRangeAddress(0, 1, col,col));
                  cell.setCellStyle(boldCellStyle);
              }else if(col == 3) {
                  Cell cell = headerRow.createCell(col);
                  cell.setCellValue(headerNames[col]);
                  sheet.addMergedRegion(new CellRangeAddress(0, 0, col,(2+listCriteria.size())));
                  cell.setCellStyle(boldCellStyle);
              }else{
                  Cell cell = headerRow.createCell(col+listCriteria.size()-1);
                  cell.setCellValue(headerNames[col]);
                  sheet.addMergedRegion(new CellRangeAddress(0, 1, col+listCriteria.size()-1,col+listCriteria.size()-1));
                  cell.setCellStyle(boldCellStyle);
              }
          }

          Row subHeader = sheet.createRow(1);
          int colSub = 3;
          for (int col = 0; col < headerNames.length; col++){
            Cell cell = subHeader.createCell(colSub + col);
            cell.setCellValue(col+1);
            cell.setCellStyle(boldCellStyle);
          }
    
          int rowIdx = 2;
          for (int i = 0; i < list.size(); i++) {
            Row row = sheet.createRow(rowIdx++);

            row.createCell(0).setCellValue(i+1);
            row.createCell(1).setCellValue(list.get(i).getPeserta().getName());
            row.createCell(2).setCellValue(list.get(i).getPeserta().getNim());
            for(int j = 0; j < list.get(i).getNilai().size(); j++){
              row.createCell(3+j).setCellValue(list.get(i).getNilai().get(j).getValue());
            }
            row.createCell(3+list.get(i).getNilai().size()).setCellValue(list.get(i).getNilaiTotal());
          }
    
          workbook.write(out);
          return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
          throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }
    
    public static ByteArrayInputStream recapSeminartoExcel(List<SeminarCriteria> listCriteria, List<List<SeminarValueParticipantDto>> listValues, List<SeminarTotalValueDto> listTotal){
      String sheetName;
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
          // style
          CellStyle boldCellStyle = workbook.createCellStyle();
          Font boldFont = workbook.createFont();
          boldFont.setBold(true);
          boldCellStyle.setFont(boldFont);
          
          // sheetname
          for(int i = 0; i < 4; i++){
            if(i <= 1){
              sheetName = "penguji " + (i+1);
            }else if(i == 2){
              sheetName = "pembimbing";
            }else{
              sheetName = "total";
            }
            
            Sheet sheet = workbook.createSheet(sheetName);

            if(i<=2){
              rowHeaderPenguji(sheet, boldCellStyle, listCriteria, listValues.get(i));
            }else{
              rowHeaderTotal(sheet, boldCellStyle, listTotal);
            }
          }
          workbook.write(out);
          return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
          throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }

    public static void rowHeaderPenguji(Sheet sheet, CellStyle boldCellStyle, List<SeminarCriteria> listCriteria, List<SeminarValueParticipantDto> listValues){
      String[] headerPenguji = { "No", "NIM", "Nama", "Rekapitulasi Nilai Seminar", "Nilai Total" };
      Row headerRow = sheet.createRow(0);
            
      for (int col = 0; col < headerPenguji.length; col++) {
        if(col < 3){
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(headerPenguji[col]);
            sheet.addMergedRegion(new CellRangeAddress(0, 1, col,col));
            cell.setCellStyle(boldCellStyle);
        }else if(col == 3) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(headerPenguji[col]);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, col,(2+listCriteria.size())));
            cell.setCellStyle(boldCellStyle);
        }else{
            Cell cell = headerRow.createCell(col+listCriteria.size()-1);
            cell.setCellValue(headerPenguji[col]);
            sheet.addMergedRegion(new CellRangeAddress(0, 1, col+listCriteria.size()-1,col+listCriteria.size()-1));
            cell.setCellStyle(boldCellStyle);
        }
      }

      Row subHeader = sheet.createRow(1);
      int colSub = 3;
      for (int col = 0; col < headerPenguji.length; col++){
        Cell cell = subHeader.createCell(colSub + col);
        cell.setCellValue(col+1);
        cell.setCellStyle(boldCellStyle);
      }

      int rowIdx = 2;
      for (int i = 0; i < listValues.size(); i++) {
        Row row = sheet.createRow(rowIdx++);

        row.createCell(0).setCellValue(i+1);
        row.createCell(1).setCellValue(listValues.get(i).getPeserta().getName());
        row.createCell(2).setCellValue(listValues.get(i).getPeserta().getNim());
        for(int j = 0; j < listValues.get(i).getNilai().size(); j++){
          row.createCell(3+j).setCellValue(listValues.get(i).getNilai().get(j).getValue());
        }
        row.createCell(3+listValues.get(i).getNilai().size()).setCellValue(listValues.get(i).getNilaiTotal());
      }

    }

    public static void rowHeaderTotal(Sheet sheet, CellStyle boldCellStyle,List<SeminarTotalValueDto> listTotal){
      String[] headerTotal = { "No", "NIM", "Nama", "Nilai Total" };
      Row headerRow = sheet.createRow(0);
            
      for (int col = 0; col < headerTotal.length; col++) {
        Cell cell = headerRow.createCell(col);
        cell.setCellValue(headerTotal[col]);
        cell.setCellStyle(boldCellStyle);
      }

      int rowIdx = 1;
      for (int i = 0; i < listTotal.size(); i++) {
        Row row = sheet.createRow(rowIdx++);

        row.createCell(0).setCellValue(i+1);
        row.createCell(1).setCellValue(listTotal.get(i).getParticipant().getName());
        row.createCell(2).setCellValue(listTotal.get(i).getParticipant().getNim());
        row.createCell(3).setCellValue(listTotal.get(i).getNilaiTotal());
      }
    }
  
  }

