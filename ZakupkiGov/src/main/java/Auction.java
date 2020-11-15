import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Auction {

    private String number;
    private String purchaseMethod;
    private String digitalPlatform;
    private String addressDigitalPlatform;
    private String purchaseObject;
    private String customer;
    private String deadlineDateTime;
    private String purchaseDateTime;
    private double maxContractPrice;
    private String timeDelivery;
    private double costApplication;
    private String smallBusiness = "НЕТ";
    private List<String> listINN = new ArrayList<>();
    private double commissionPlatform;

    private final Logger LOGGER = LogManager.getLogger(Auction.class);
    private String url = "https://zakupki.gov.ru/epz/order/notice/ea44/view/common-info.html?regNumber=";

    public Auction(String url) {
        this.url = url;
    }


    private ArrayList<Element> parseSections(String url) {

        ArrayList<Element> sections = new ArrayList<>();
        try {

            getDocumentHtml(url).getElementsByTag("span").forEach(span -> {
                if (span.attr("class").contains("navBreadcrumb__text")) {
                    setNumber(span.text());
                }
            });

            getDocumentHtml(url).getElementsByTag("section").forEach(section -> {
                if (section.attr("class").contains("blockInfo__section")) {
                    sections.add(section);
                }
            });
        } catch (Exception ex) {
            LOGGER.debug(number + " " + ex);
        }
        return sections;
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

    public void filterNumber(String number) {
        url = url + number.replaceAll("№", "").trim();
    }

    public void parseInfoAboutPurchase() {

        try {
            parseSections(url).forEach(section -> {

                String sectionTitle = section.select("span.section__title").text();
                String sectionInfo = section.select("span.section__info").text();

                if (sectionTitle.contains("Способ определения поставщика (подрядчика, исполнителя)")) {
                    setPurchaseMethod(sectionInfo);
                }
                if (sectionTitle.contains("Наименование электронной площадки в информационно-телекоммуникационной сети \"Интернет\"")) {
                    setDigitalPlatform(sectionInfo);
                }
                if (sectionTitle.contains("Адрес электронной площадки в информационно-телекоммуникационной сети \"Интернет\"")) {
                    setAddressDigitalPlatform(sectionInfo);
                }
                if (sectionTitle.contains("Организация, осуществляющая размещение")) {
                    setCustomer(sectionInfo);
                }
                if (sectionTitle.contains("Наименование объекта закупки")) {
                    setPurchaseObject(sectionInfo);
                }
                if (sectionTitle.contains("Ограничения и запреты") && sectionInfo.contains("субъектов малого предпринимательства")) {
                    setSmallBusiness("ДА");
                }
                if (sectionTitle.contains("Начальная (максимальная) цена контракта")) {
                    setMaxContractPrice(Double.parseDouble(sectionInfo.replaceAll("\\s", "").replaceAll(",", ".").trim()));
                }
                if (sectionTitle.contains("Дата и время окончания срока подачи заявок")) {
                    setDeadlineDateTime(sectionInfo.replaceAll("в", ""));
                }
                if (sectionTitle.contains("Дата проведения аукциона в электронной форме")) {
                    setPurchaseDateTime(sectionInfo);
                }
                if (sectionTitle.contains("Время проведения аукциона")) {
                    setPurchaseDateTime(getPurchaseDateTime() + " " + sectionInfo);
                }
                if (sectionTitle.contains("Размер обеспечения заявки")) {
                    setCostApplication(Double.parseDouble(sectionInfo.replaceAll("\\D+\\s\\D+", "").replaceAll("\\s", "").replaceAll(",", ".").trim()));
                }
                if (sectionTitle.contains("Сроки поставки товара или завершения работы либо график оказания услуг")) {
                    setTimeDelivery(sectionInfo);
                }
                if (sectionTitle.contains("Международное непатентованное (химическое, группировочное) наименование лекарственного препарата")) {
                    listINN.add(sectionInfo);
                }
            });
            calculateCommissionPlatform();

        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.debug(number + " " + ex);
        }

    }

    public void calculateCommissionPlatform() {
        int num = smallBusiness.equals("ДА") ? 2000 : 5000;
        if (maxContractPrice <= num * 100) {
            commissionPlatform = maxContractPrice * 0.01;
        } else
            commissionPlatform = num;
    }

    @Override
    public String toString() {
        return " Номер закупки: " + number + '\n' +
                " Способ определения поставщика (подрядчика, исполнителя): " + purchaseMethod + '\n' +
                " Наименование электронной площадки: " + digitalPlatform + '\n' +
                " Адрес электронной площадки: " + addressDigitalPlatform + '\n' +
                " Наименование объекта закупки: " + purchaseObject + '\n' +
                " Сведения о наименовании лекарственного препарата(МНН): " + listINN + '\n' +
                " Организация, осуществляющая размещение: " + customer + '\n' +
                " Закупка у субъектов малого предпринимательства: " + smallBusiness + '\n' +
                " Дата и время окончания срока подачи заявок на участие: " + deadlineDateTime + '\n' +
                " Время проведения аукциона: " + purchaseDateTime + '\n' +
                " Начальная (максимальная) цена контракта: " + maxContractPrice + '\n' +
                " Размер обеспечения заявки: " + costApplication + '\n' +
                " Комиссия электронной площадки: " + commissionPlatform + '\n' +
                " Сроки поставки товара: " + timeDelivery + '\n';
    }
}
