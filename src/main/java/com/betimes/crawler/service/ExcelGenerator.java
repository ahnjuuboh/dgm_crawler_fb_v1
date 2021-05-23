package com.betimes.crawler.service;

import com.betimes.crawler.util.HttpUtil;
import com.betimes.crawler.util.JsonUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class ExcelGenerator {

    public XSSFWorkbook export() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFFont headerFont = ((XSSFWorkbook) workbook).createFont();
        headerFont.setBold(true);

//        this.createKeywordSheet(workbook, headerFont);
        this.createAll(workbook, headerFont);

        return workbook;
    }

    private void createKeywordSheet(XSSFWorkbook workbook, XSSFFont headerFont) {
        XSSFSheet sheet = workbook.createSheet("Keyword");
        String[] columns = {"id", "type", "source", "from.id", "from.name", "parent.id", "parent.message",
        "parent.created_time", "message", "link", "picture", "permalink_url", "likes", "love", "haha", "wow",
        "sorry", "anger", "shares", "comment_count", "created_time"};

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        CellStyle dateCellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMMM d, yyyy"));

        int rowCount = 0;

        // Create Header Row
        Row headerRow = sheet.createRow(rowCount++);
        // Create cells
        for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }


        try {
            String url = "http://192.168.0.90:9200/bt_engagement_bowels/_search";
            String body = "{\n" +
                    "  \"query\": {\n" +
                    "    \"query_string\": {\n" +
                    "      \"query\": \"*\"\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"size\": 10000,\n" +
                    "  \"from\": 0,\n" +
                    "  \"sort\": [{\n" +
                    "      \"created_time\": \"desc\"\n" +
                    "  }]\n" +
                    "}";
            String jsonResponse = HttpUtil.doPost(url, body);
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray dataList = jsonObject.getJSONObject("hits").getJSONArray("hits");
            int size = dataList.length();

            for (int i = 0; i < size; i++) {
                JSONObject dataJson = dataList.getJSONObject(i).getJSONObject("_source");
                Row row = sheet.createRow(rowCount++);
                int colCount = 0;

                this.createCell(row, colCount++, JsonUtil.getString(dataJson, "id"));
                this.createCell(row, colCount++, JsonUtil.getString(dataJson, "type"));
                this.createCell(row, colCount++, JsonUtil.getString(dataJson, "source"));
                if (!dataJson.isNull("from")) {
                    this.createCell(row, colCount++, JsonUtil.getString(dataJson.getJSONObject("from"), "id"));
                    this.createCell(row, colCount++, JsonUtil.getString(dataJson.getJSONObject("from"), "name"));
                } else {
                    this.createCell(row, colCount++, "");
                    this.createCell(row, colCount++, "");
                }
                if (!dataJson.isNull("parent")) {
                    this.createCell(row, colCount++, JsonUtil.getString(dataJson.getJSONObject("parent"), "id"));
                    this.createCell(row, colCount++, JsonUtil.getString(dataJson.getJSONObject("parent"), "message"));
                    this.createCell(row, colCount++, JsonUtil.getString(dataJson.getJSONObject("parent"), "created_time"));
                } else {
                    this.createCell(row, colCount++, "");
                    this.createCell(row, colCount++, "");
                    this.createCell(row, colCount++, "");
                }
                this.createCell(row, colCount++, JsonUtil.getString(dataJson, "message"));
                this.createCell(row, colCount++, JsonUtil.getString(dataJson, "link"));
                this.createCell(row, colCount++, JsonUtil.getString(dataJson, "picture"));
                this.createCell(row, colCount++, JsonUtil.getString(dataJson, "permalink_url"));
                this.createCell(row, colCount++, JsonUtil.getLong(dataJson, "likes"));
                this.createCell(row, colCount++, JsonUtil.getLong(dataJson, "love"));
                this.createCell(row, colCount++, JsonUtil.getLong(dataJson, "haha"));
                this.createCell(row, colCount++, JsonUtil.getLong(dataJson, "wow"));
                this.createCell(row, colCount++, JsonUtil.getLong(dataJson, "sorry"));
                this.createCell(row, colCount++, JsonUtil.getLong(dataJson, "anger"));
                this.createCell(row, colCount++, JsonUtil.getLong(dataJson, "shares"));
                this.createCell(row, colCount++, JsonUtil.getLong(dataJson, "comments"));
                this.createCell(row, colCount++, JsonUtil.getString(dataJson, "created_time"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createAll(XSSFWorkbook workbook, XSSFFont headerFont) {
        XSSFSheet sheet = workbook.createSheet("All Content");
        String[] columns = {"id", "type", "source", "from.id", "from.name", "parent.id", "parent.message",
                "parent.created_time", "message", "link", "picture", "permalink_url", "likes", "love", "haha", "wow",
                "sorry", "anger", "shares", "comment_count", "created_time"};

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        CellStyle dateCellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMMM d, yyyy"));

        int rowCount = 0;

        // Create Header Row
        Row headerRow = sheet.createRow(rowCount++);
        // Create cells
        for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        try {
            String url = "http://192.168.10.90:9200/bt_engagement_bowels/_search";
            String body = "{\n" +
                    "    \"size\": 10000\n" +
                    "}";
            String jsonResponse = HttpUtil.doPost(url, body);
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray dataList = jsonObject.getJSONObject("hits").getJSONArray("hits");
            int size = dataList.length();
            System.out.println(size);
            for (int i = 0; i < size; i++) {
                JSONObject dataJson = dataList.getJSONObject(i).getJSONObject("_source");
                Row row = sheet.createRow(rowCount++);
                int colCount = 0;

                this.createCell(row, colCount++, JsonUtil.getString(dataJson, "id"));
                this.createCell(row, colCount++, JsonUtil.getString(dataJson, "type"));
                this.createCell(row, colCount++, JsonUtil.getString(dataJson, "source"));
                if (!dataJson.isNull("from")) {
                    this.createCell(row, colCount++, JsonUtil.getString(dataJson.getJSONObject("from"), "id"));
                    this.createCell(row, colCount++, JsonUtil.getString(dataJson.getJSONObject("from"), "name"));
                } else {
                    this.createCell(row, colCount++, "");
                    this.createCell(row, colCount++, "");
                }
                if (!dataJson.isNull("parent")) {
                    this.createCell(row, colCount++, JsonUtil.getString(dataJson.getJSONObject("parent"), "id"));
                    this.createCell(row, colCount++, JsonUtil.getString(dataJson.getJSONObject("parent"), "message"));
                    this.createCell(row, colCount++, JsonUtil.getString(dataJson.getJSONObject("parent"), "created_time"));
                } else {
                    this.createCell(row, colCount++, "");
                    this.createCell(row, colCount++, "");
                    this.createCell(row, colCount++, "");
                }
                this.createCell(row, colCount++, JsonUtil.getString(dataJson, "message"));
                this.createCell(row, colCount++, JsonUtil.getString(dataJson, "link"));
                this.createCell(row, colCount++, JsonUtil.getString(dataJson, "picture"));
                this.createCell(row, colCount++, JsonUtil.getString(dataJson, "permalink_url"));
                this.createCell(row, colCount++, JsonUtil.getLong(dataJson, "likes"));
                this.createCell(row, colCount++, JsonUtil.getLong(dataJson, "love"));
                this.createCell(row, colCount++, JsonUtil.getLong(dataJson, "haha"));
                this.createCell(row, colCount++, JsonUtil.getLong(dataJson, "wow"));
                this.createCell(row, colCount++, JsonUtil.getLong(dataJson, "sorry"));
                this.createCell(row, colCount++, JsonUtil.getLong(dataJson, "anger"));
                this.createCell(row, colCount++, JsonUtil.getLong(dataJson, "shares"));
                this.createCell(row, colCount++, JsonUtil.getLong(dataJson, "comments"));
                this.createCell(row, colCount++, JsonUtil.getString(dataJson, "created_time"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Cell createCell(Row row, int columnIndex, Object field) {
        Cell cell = row.createCell(columnIndex);
        if (field instanceof String) {
            cell.setCellValue((String) field);
        } else if (field instanceof Integer) {
            cell.setCellValue((Integer) field);
        } else if (field instanceof Long) {
            cell.setCellValue((Long) field);
        }

        return cell;
    }
}
