package uk.ac.ebi.spot.goci.curation.component;


import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.poi.hssf.usermodel.HSSFFont;

/**
 * Created by cinzia on 07/12/2016.
 */

public class StatsReportExcelView extends AbstractXlsxView
{
    private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.SHORT);


    @Override
    protected void buildExcelDocument(Map<String, Object> model,
                                      Workbook workbook,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        // change the file name
        response.setContentType( "application/ms-excel" );
        String suffixFile = new SimpleDateFormat("yyyyMMddhhmm'.xls'").format(new Date());
        String filename = "GWAS_Stats_Report_"+suffixFile;
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"" );

        CellStyle style = headerCellStyle(workbook);
        CellStyle oddStyle = oddCellStyle(workbook);
        CellStyle evenStyle = evenCellStyle(workbook);


        List<Object> reportWeekly =  (List<Object>) model.get("reportWeekly");
        addReportWeeklySheets(workbook, style, oddStyle, evenStyle, reportWeekly);
        model.remove("reportWeekly");

        ArrayList<Integer[]> progressiveQueues = (ArrayList<Integer[]>) model.get("progressiveQueues");
        addProgressiveQueuesSheet(workbook, style, oddStyle, evenStyle, progressiveQueues);
        model.remove("progressiveQueues");

        List<Object> curatorsStatsByWeek =  (List<Object>) model.get("curatorsStatsByWeek");
        String period = (String) model.get("periodStatsByWeek");
        addCuratorStatsByWeek(workbook,  style, oddStyle, evenStyle, curatorsStatsByWeek, period);
        model.remove("curatorsStatsByWeek");
        model.remove("periodStatsByWeek");

        for (String key : model.keySet()) {
            List<Object> curatorStats = (List<Object>) model.get(key);
            addCuratorSheet(workbook, style, oddStyle, evenStyle, curatorStats, key);
        }
    }

    public void addReportWeeklySheets(Workbook workbook, CellStyle style, CellStyle oddStyle, CellStyle evenStyle, List<Object> reportWeekly) {
        Sheet sheetMain = workbook.createSheet("Main Page");
        Sheet sheetDraft = workbook.createSheet("Week Draft");
        sheetMain.setDefaultColumnWidth(23);
        sheetDraft.setDefaultColumnWidth(20);


        CreationHelper createHelper = workbook.getCreationHelper();
        //cellDateStyle.setDataFormat(createHelper.createDataFormat().getFormat("[$-809]dddd, d mmmm yyyy"));
        CellStyle oddDateStyle = workbook.createCellStyle();
        oddDateStyle.cloneStyleFrom(oddStyle);
        oddDateStyle.setDataFormat(createHelper.createDataFormat().getFormat("[$-809]dddd, d mmm yyyy"));
        CellStyle evenDateStyle = workbook.createCellStyle();
        evenDateStyle.cloneStyleFrom(evenStyle);
        evenDateStyle.setDataFormat(createHelper.createDataFormat().getFormat("[$-809]dddd, d mmm yyyy"));

        Row sheetMainHeader = sheetMain.createRow(0);
        sheetMainHeader.setHeightInPoints((float) 27);
        sheetMainHeader.createCell(0).setCellValue("Week");
        sheetMainHeader.getCell(0).setCellStyle(style);
        sheetMainHeader.createCell(1).setCellValue("Year");
        sheetMainHeader.getCell(1).setCellStyle(style);
        sheetMainHeader.createCell(2).setCellValue("Date");
        sheetMainHeader.getCell(2).setCellStyle(style);
        sheetMainHeader.createCell(3).setCellValue("Created");
        sheetMainHeader.getCell(3).setCellStyle(style);
        sheetMainHeader.createCell(4).setCellValue("Published");
        sheetMainHeader.getCell(4).setCellStyle(style);
        sheetMainHeader.createCell(5).setCellValue("Level 1 Done");
        sheetMainHeader.getCell(5).setCellStyle(style);
        sheetMainHeader.createCell(6).setCellValue("Level 2 Done");
        sheetMainHeader.getCell(6).setCellStyle(style);

        int currentRow = 0;
        short currentColumn = 0;
        Row header = sheetDraft.createRow(currentRow);
        header.setHeightInPoints((float) 27);
        header.createCell(0).setCellValue("Week");
        header.getCell(0).setCellStyle(style);
        header.createCell(1).setCellValue("Year");
        header.getCell(1).setCellStyle(style);
        header.createCell(2).setCellValue("Studies");
        header.getCell(2).setCellStyle(style);
        header.createCell(3).setCellValue("Published");
        header.getCell(3).setCellStyle(style);
        header.createCell(4).setCellValue("Level 1 Done");
        header.getCell(4).setCellStyle(style);
        header.createCell(5).setCellValue("Level 2 Done");
        header.getCell(5).setCellStyle(style);
        header.createCell(6).setCellValue("Pubmed");
        header.getCell(6).setCellStyle(style);
        header.createCell(7).setCellValue("Published");
        header.getCell(7).setCellStyle(style);
        header.createCell(8).setCellValue("Level 1 Done");
        header.getCell(8).setCellStyle(style);
        header.createCell(9).setCellValue("Level 2 Done");
        header.getCell(9).setCellStyle(style);

        currentRow++;


        for (int i=0; i <reportWeekly.size(); i++) {
            {
                Row rowExcel = sheetDraft.createRow(currentRow);
                Row rowMainExcel = sheetMain.createRow(currentRow);
                rowMainExcel.setHeightInPoints((float) 27);
                rowExcel.setHeightInPoints((float) 27);

                currentColumn = 0;
                Object[] item = (Object[]) reportWeekly.get(i);
                for (int j = 0; j < item.length; j++) {
                    Integer value = Integer.valueOf(item[j].toString());
                    rowExcel.createCell(currentColumn).setCellValue(value);
                    if (j>1) {
                        if (j<6) {
                            if (j == 2) {
                                Cell cellFormula = rowMainExcel.createCell(2);
                                String position = String.valueOf(i+2);
                                String formula = "DATE(B"+position+", 1, -3) - WEEKDAY(DATE(B"+position+", 1, 3)) + A"+position+" * 7";
                                cellFormula.setCellFormula(formula);
                                setCellStyle(i,cellFormula, oddDateStyle, evenDateStyle);


                            }
                            String stats = item[j].toString() + " (" +item[j+4].toString()+")";
                            Cell cell = rowMainExcel.createCell(currentColumn+1);
                            cell.setCellValue(stats);
                            setCellStyle(i, cell, oddStyle, evenStyle);
                        }
                    }
                    else {
                        // position 0 and 1 are year and week
                        Cell cell = rowMainExcel.createCell(currentColumn);
                        cell.setCellValue(value);
                        setCellStyle(i, cell, oddStyle, evenStyle);
                    }

                    if ((i%2) == 0) {
                        rowExcel.getCell(currentColumn).setCellStyle(oddStyle);
                    }
                    else {
                        rowExcel.getCell(currentColumn).setCellStyle(evenStyle);
                    }



                    currentColumn++;
                }
                currentRow++;
            }
        }
    }


    public void addProgressiveQueuesSheet(Workbook workbook, CellStyle style, CellStyle oddStyle, CellStyle evenStyle, ArrayList<Integer[]> progressiveQueues) {
        Sheet sheet = workbook.createSheet("Progressive Queues");
        sheet.setDefaultColumnWidth(28);

        Row sheetMainHeader = sheet.createRow(0);
        sheetMainHeader.setHeightInPoints((float) 27);
        sheetMainHeader.createCell(0).setCellValue("Year");
        sheetMainHeader.getCell(0).setCellStyle(style);
        sheetMainHeader.createCell(1).setCellValue("Week");
        sheetMainHeader.getCell(1).setCellStyle(style);
        sheetMainHeader.createCell(2).setCellValue("In level 1 queue");
        sheetMainHeader.getCell(2).setCellStyle(style);
        sheetMainHeader.createCell(3).setCellValue("In level 2 queue");
        sheetMainHeader.getCell(3).setCellStyle(style);
        sheetMainHeader.createCell(4).setCellValue("In level 3 queue");
        sheetMainHeader.getCell(4).setCellStyle(style);
        sheetMainHeader.createCell(5).setCellValue("Published");
        sheetMainHeader.getCell(5).setCellStyle(style);

        int currentRow = 0;
        short currentColumn = 0;
        currentRow++;


        for (int i=0; i <progressiveQueues.size(); i++) {
            {
                Row rowExcel = sheet.createRow(currentRow);
                //rowExcel.setHeight((short) 32);
                rowExcel.setHeightInPoints((float) 27);
                currentColumn = 0;
                Integer[] item = (Integer[]) progressiveQueues.get(i);
                for (int j = 0; j < item.length; j++) {
                    rowExcel.createCell(currentColumn).setCellValue(item[j]);
                    if ((i%2) == 1) {
                        rowExcel.getCell(currentColumn).setCellStyle(oddStyle);
                    }
                    else { rowExcel.getCell(currentColumn).setCellStyle(evenStyle);}
                    currentColumn++;
                }
                currentRow++;
            }
        }
    }

    public void addCuratorStatsByWeek(Workbook workbook,CellStyle style, CellStyle oddStyle, CellStyle evenStyle, List<Object> curatorsStatsByWeek, String period) {
        Sheet sheet = workbook.createSheet("Curators_for_week");
        sheet.setDefaultColumnWidth(20);
        int[] totals = new int[8];

        int currentRow = 0;
        short currentColumn = 0;

        Row sheetMainHeader = sheet.createRow(currentRow);
        sheetMainHeader.setHeightInPoints((float) 27);
        sheetMainHeader.createCell(0).setCellValue("Week "+ period);
        sheetMainHeader.getCell(0).setCellStyle(style);
        sheetMainHeader.createCell(1).setCellValue("Level 1 done");
        sheetMainHeader.getCell(1).setCellStyle(style);
        sheetMainHeader.createCell(2).setCellValue("Level 2 done");
        sheetMainHeader.getCell(2).setCellStyle(style);
        sheetMainHeader.createCell(3).setCellValue("Level 3 done");
        sheetMainHeader.getCell(3).setCellStyle(style);
        sheetMainHeader.createCell(4).setCellValue("Published");
        sheetMainHeader.getCell(4).setCellStyle(style);

        currentRow++;


        for (int i=0; i < curatorsStatsByWeek.size(); i++) {
            {
                Row rowExcel = sheet.createRow(currentRow);
                rowExcel.setHeightInPoints((float) 27);
                currentColumn = 0;
                Object[] item = (Object[]) curatorsStatsByWeek.get(i);
                for (int j = 0; j < item.length; j++) {
                    if (j>0) {
                        if (j<5) {
                            String stats = item[j].toString() + " (" +item[j+4].toString()+")";
                            rowExcel.createCell(currentColumn).setCellValue(stats);
                            setCellStyle(i, rowExcel.getCell(currentColumn), oddStyle, evenStyle);
                        }
                        Integer value = Integer.valueOf(item[j].toString());
                        totals[j-1] = totals[j-1] + value;
                    }
                    else {
                        rowExcel.createCell(currentColumn).setCellValue(item[j].toString());
                        setCellStyle(i, rowExcel.getCell(currentColumn), oddStyle, evenStyle);
                    }
                    currentColumn++;
                }
                currentRow++;
            }

        }
        Row totalExcel = sheet.createRow(currentRow+2);
        totalExcel.setHeightInPoints((float) 27);
        totalExcel.createCell(0).setCellValue("Total");
        totalExcel.createCell(1).setCellValue(totals[0] + " (" +totals[4]+")");
        totalExcel.createCell(2).setCellValue(totals[1] + " (" +totals[5]+")");
        totalExcel.createCell(3).setCellValue(totals[2] + " (" +totals[6]+")");
        totalExcel.createCell(4).setCellValue(totals[3] + " (" +totals[7]+")");
        setCellStyle(0, totalExcel.getCell(0), oddStyle, evenStyle);
        setCellStyle(0, totalExcel.getCell(1), oddStyle, evenStyle);
        setCellStyle(0, totalExcel.getCell(2), oddStyle, evenStyle);
        setCellStyle(0, totalExcel.getCell(3), oddStyle, evenStyle);
        setCellStyle(0, totalExcel.getCell(4), oddStyle, evenStyle);
    }


    public void addCuratorSheet(Workbook workbook,CellStyle style, CellStyle oddStyle, CellStyle evenStyle, List<Object> curatorStats, String curatorName) {
        Sheet sheet = workbook.createSheet(curatorName);
        sheet.setDefaultColumnWidth(20);

        int currentRow = 0;
        short currentColumn = 0;

        Row sheetMainHeader = sheet.createRow(currentRow);
        sheetMainHeader.setHeightInPoints((float) 27);
        sheetMainHeader.createCell(0).setCellValue("Year ");
        sheetMainHeader.getCell(0).setCellStyle(style);
        sheetMainHeader.createCell(1).setCellValue("Week ");
        sheetMainHeader.getCell(1).setCellStyle(style);
        sheetMainHeader.createCell(2).setCellValue("Level 1 done");
        sheetMainHeader.getCell(2).setCellStyle(style);
        sheetMainHeader.createCell(3).setCellValue("Level 2 done");
        sheetMainHeader.getCell(3).setCellStyle(style);
        sheetMainHeader.createCell(4).setCellValue("Level 3 done");
        sheetMainHeader.getCell(4).setCellStyle(style);
        sheetMainHeader.createCell(5).setCellValue("Published");
        sheetMainHeader.getCell(5).setCellStyle(style);

        currentRow++;


        for (int i=0; i < curatorStats.size(); i++) {
            {
                Row rowExcel = sheet.createRow(currentRow);
                rowExcel.setHeightInPoints((float) 27);
                currentColumn = 0;
                Object[] item = (Object[]) curatorStats.get(i);
                for (int j = 0; j < item.length-4; j++) {
                    if (j>1) {
                        if (j<6) {
                            String stats = item[j].toString() + " (" +item[j+4].toString()+")";
                            rowExcel.createCell(currentColumn).setCellValue(stats);
                        }
                    }
                    else {
                        Integer value = Integer.valueOf(item[j].toString());
                        rowExcel.createCell(currentColumn).setCellValue(value);
                    }
                    if ((i%2) == 0) {
                        rowExcel.getCell(currentColumn).setCellStyle(oddStyle);
                    }
                    else { rowExcel.getCell(currentColumn).setCellStyle(evenStyle);}
                    currentColumn++;
                }
                currentRow++;
            }
        }
    }

    public void setCellStyle(int index, Cell cell, CellStyle oddStyle, CellStyle evenStyle ) {
        if ((index%2) == 1) {
            cell.setCellStyle(oddStyle);
        }
        else {
            cell.setCellStyle(evenStyle);
        }
    }

    public CellStyle headerCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 13);
        style.setFillForegroundColor(HSSFColor.DARK_TEAL.index);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setColor(HSSFColor.WHITE.index);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setFont(font);

        return style;
    }

    public CellStyle oddCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 13);
        style.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setBorderBottom(CellStyle.BORDER_DOTTED);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setFont(font);

        return style;
    }

    public CellStyle evenCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 13);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setBorderBottom(CellStyle.BORDER_DOTTED);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setFont(font);

        return style;
    }

}
