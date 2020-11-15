import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

@Getter
@Setter
public class ExcelFile {
    public HashMap<String, Auction> auctions = new HashMap<>();
    private final Logger LOGGER = LogManager.getLogger(ExcelFile.class);
    private final String DATE;
    private Workbook BOOK = new HSSFWorkbook();
    private Sheet SHEET;
    private final String path;

    public ExcelFile(HashMap<String, Auction> auctions, String date) {
        this.DATE = date;
        this.auctions = auctions;
        SHEET = BOOK.createSheet("Аукционы");
        path = "data/Список аукционов_" + DATE + ".xls";
    }
    public ExcelFile(HashMap<String, Auction> auctions, String date, String path) {
        this.DATE = date;
        this.auctions = auctions;
        SHEET = BOOK.createSheet("Аукционы");
        this.path = path + "/Список аукционов_" + DATE + ".xls";
    }

    public ExcelFile(Auction auction, String date) {
        this.DATE = date;
        auctions.put(auction.getNumber(), auction);
        SHEET = BOOK.createSheet("Аукционы");
        path = "data/Список аукционов_" + DATE + ".xls";
    }
    public ExcelFile(Auction auction, String date, String path) {
        this.DATE = date;
        auctions.put(auction.getNumber(), auction);
        SHEET = BOOK.createSheet("Аукционы");
        this.path = path + "/Список аукционов_" + DATE + ".xls";
    }

    public void createExcelFile() {
        try {
            headerTable();
            addRow();
            BOOK.write(new FileOutputStream(path));
            BOOK.close();

        } catch (Exception ex) {
            LOGGER.debug(ex);
        }
    }

    private void headerTable() {
        Row row = SHEET.createRow(0); //Ряд
        CellStyle style = BOOK.createCellStyle();
        Font font = BOOK.createFont();
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setBold(true);
        style.setFont(font);
        row.createCell(0).setCellValue("Номер закупки");
        row.createCell(1).setCellValue("Способ определения поставщика (подрядчика, исполнителя)");
        row.createCell(2).setCellValue("Наименование электронной площадки");
        row.createCell(3).setCellValue("Адрес электронной площадки");
        row.createCell(4).setCellValue("Наименование объекта закупки");
        row.createCell(5).setCellValue("Сведения о наименовании лекарственного препарата(МНН)");
        row.createCell(6).setCellValue("Организация, осуществляющая размещение");
        row.createCell(7).setCellValue("Закупка у субъектов малого предпринимательства");
        row.createCell(8).setCellValue("Дата и время окончания срока подачи заявок на участие");
        row.createCell(9).setCellValue("Время проведения аукциона");
        row.createCell(10).setCellValue("Начальная (максимальная) цена контракта");
        row.createCell(11).setCellValue("Размер обеспечения заявки");
        row.createCell(12).setCellValue("Комиссия электронной площадки");
        row.createCell(13).setCellValue("Сроки поставки товара");
        for (int j = 0; j <= 13; j++)
            row.getCell(j).setCellStyle(style);
    }

    public void addRow() {

        auctions.forEach((k, v) -> {
            Row row = SHEET.createRow(SHEET.getLastRowNum() + 1);
            row.createCell(0).setCellValue(k);
            row.createCell(1).setCellValue(v.getPurchaseMethod());
            row.createCell(2).setCellValue(v.getDigitalPlatform());
            row.createCell(3).setCellValue(v.getAddressDigitalPlatform());
            row.createCell(4).setCellValue(v.getPurchaseObject());
            row.createCell(5).setCellValue(v.getListINN().toString());
            row.createCell(6).setCellValue(v.getCustomer());
            row.createCell(7).setCellValue(v.getSmallBusiness());

            DataFormat format = BOOK.createDataFormat();
            CellStyle dateStyle = BOOK.createCellStyle();

            dateStyle.setDataFormat(format.getFormat("m/d/yy"));
            row.createCell(8).setCellValue(v.getDeadlineDateTime());
            row.getCell(8).setCellStyle(dateStyle);


            dateStyle.setDataFormat(format.getFormat("m/d/yy"));
            row.createCell(9).setCellValue(v.getPurchaseDateTime());
            row.getCell(9).setCellStyle(dateStyle);

            row.createCell(10).setCellValue(v.getMaxContractPrice());
            row.createCell(11).setCellValue(v.getCostApplication());
            row.createCell(12).setCellValue(v.getCommissionPlatform());
            row.createCell(13).setCellValue(v.getTimeDelivery());

            for (int j = 0; j <= 13; j++) {
                SHEET.autoSizeColumn(j);
            }

        });
    }

    public void addRecordTable() {

        try  {
            FileInputStream inputStream = new FileInputStream(path);
            BOOK = new HSSFWorkbook(inputStream);
            SHEET = BOOK.getSheet("Аукционы");
            addRow();

            FileOutputStream outputStream = new FileOutputStream(path);
            BOOK.write(outputStream);
            BOOK.close();

        } catch (Exception ex) {
            LOGGER.debug(ex);
        }
    }
}
