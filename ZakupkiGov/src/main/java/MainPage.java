import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class MainPage {

    private final Logger LOGGER = LogManager.getLogger(MainPage.class);
    public String url = "https://zakupki.gov.ru/epz/order/extendedsearch/results.html?searchString=&morphology=on&" +
            "search-filter=Дате+размещения&pageNumber=1&sortDirection=false&recordsPerPage=_50&showLotsInfoHidden=false&" +
            "savedSearchSettingsIdHidden=&sortBy=PRICE&fz44=on&af=on&placingWayList=&selectedLaws=&" +
            "priceFromGeneral=&priceFromGWS=Минимальная+цена&priceFromUnitGWS=Минимальная+цена&priceToGeneral=&priceToGWS=Максимальная+цена&priceToUnitGWS=Максимальная+цена&" +
            "currencyIdGeneral=-1&publishDateFrom=06.11.2020&publishDateTo=06.11.2020&applSubmissionCloseDateFrom=&applSubmissionCloseDateTo=&" +
            "customerIdOrg=&customerFz94id=&customerTitle=&customerPlace=9371527&customerPlaceCodes=OKER40&okpd2Ids=&okpd2IdsCodes=&drugsSubjects=on&" +
            "OrderPlacementSmallBusinessSubject=on&OrderPlacementRnpData=on&OrderPlacementExecutionRequirement=on&orderPlacement94_0=0&orderPlacement94_1=0&orderPlacement94_2=0";
    public String path;
    private HashMap<String, Auction> auctions = new HashMap<>();
    private String date = LocalDateTime.now().minusDays(2).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));


    public MainPage(String path) {
        this.path = path;
        filterDate(date);
    }


    public MainPage(String url, String path) {
        this.path = path;
        this.url = url;
    }

    public void parsePurchases() {
        try {

            LOGGER.info("Дата размещения аукционов: " + date);
            parsePage(url);
            ExcelFile excel = new ExcelFile(auctions, date, "D:/");
            excel.createExcelFile();
            LOGGER.info("Количество аукционов добавленное в базу: " + getAuctions().size());

        } catch (Exception ex) {
            LOGGER.debug(ex);
        }

    }

    public void filterDate(String date) {
        this.date = date;
        url = url.replaceAll("(publishDateFrom=)(\\d+\\.\\d+\\.\\d+)(&publishDateTo=)(\\d+\\.\\d+\\.\\d+)", "$1" + date + "$3" + date);
    }

    public void parsePage(String url) {

        try {
            int a = countPage(url).get();
            System.out.println(a);
            for (int i = 1; i <= a; i++) {
                url = url.replaceAll("(pageNumber=)(\\d+)", "$1" + i);
                getDocumentHtml(url).getAllElements().forEach(e -> {
//                    if (e.select("div").attr("class").contains("search-results__total")) {
//                        countRecords = Integer.parseInt(e.select("div.search-results__total").text().replaceAll("[^\\w]", ""));
//                    }
                    if (e.select("div").attr("class").contains("registry-entry__header-mid__number")) {
                        Auction auction = new Auction("https://zakupki.gov.ru" + e.select("a").attr("href"));
                        auction.parseInfoAboutPurchase();
                        auctions.put(auction.getNumber(), auction);
                    }
                });
            }

        } catch (Exception ex) {
            LOGGER.debug(ex);
        }

    }

    private AtomicInteger countPage(String url) {

        AtomicInteger count = new AtomicInteger();
        try {
            getDocumentHtml(url).getElementsByAttributeValue("class", "link-text").forEach(e -> count.set(Integer.parseInt(e.text())));
        } catch (Exception ex) {
            LOGGER.debug(ex);
        }
        if (count.get() == 0) {
            count.set(1);
        }
        return count;
    }

    //Метод получает документ html
    private Document getDocumentHtml(String url) {

        Document document = null;
        try {
            document = Jsoup.connect(url)
                    .userAgent("Opera Chrome")
                    .timeout(0)
                    .referrer("https://zakupki.gov.ru")
                    .get();
        } catch (Exception ex) {
            LOGGER.debug(ex);

        }
        return document;
    }

    @Override
    public String toString() {
        return "Главная страница: " + url + "\n" +
                "Дата размещения аукционов: " + date;
    }
}
