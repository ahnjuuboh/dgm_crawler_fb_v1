package com.betimes.crawler;

import com.betimes.crawler.service.ExcelGenerator;
import com.betimes.crawler.service.FbPostService;
import com.betimes.crawler.service.FbProfileService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service("serviceStart")
public class ServiceStart {
    @Autowired
    private FbProfileService fbProfileService;
    @Autowired
    private FbPostService fbPostService;
    @Autowired
    private ExcelGenerator excelGenerator;


    @Value("${upload.path}")
    private String uploadPath;

    public void queueManagement() {
        try {
            //1523602007857773
            //1698113237105630
            //103481924822988
//            this.fbProfileService.fetchInfo("336666623191863");
//            this.fbPostService.fetchPost("103481924822988", null);
            // this.fbProfileService.fetchInfo("433
            // simpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String fileName = dateFormat.format(new Date()) + ".xlsx";
                String filePath = this.uploadPath + "excel/" + fileName;
                // Write the output to a file
                FileOutputStream outputStream = new FileOutputStream(filePath);
                XSSFWorkbook workbook = this.excelGenerator.export();
                workbook.write(outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}