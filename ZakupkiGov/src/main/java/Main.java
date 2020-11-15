import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class Main {

    public static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            long a = System.currentTimeMillis();
            MainPage mainPage = new MainPage("D:/");
            mainPage.filterDate("12.11.2020");
            mainPage.parsePurchases();
//            System.out.println(mainPage.toString());
//            mainPage.getAuctions().forEach((k,v) -> {
//                System.out.println(k + " " + v.getCommissionPlatform());
//            });
//            Auction auction = new Auction();
//            auction.filterNumber("0358200019720000334");
//            auction.parseInfoAboutPurchase();
//            ExcelFile excel = new ExcelFile(auction, "06.11.2020");
//            excel.addRecordTable();
//            System.out.println(auction.toString());

            System.out.println("Выполнение программы: " + (double)(System.currentTimeMillis() - a) / 60000 + " минут");

        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error(ex);
        }
    }
}


