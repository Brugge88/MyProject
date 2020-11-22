import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.util.List;

public class Main {

    //Отредактировать файл настроек "parameters.properties" для подключения к базе данных
    private static final String PATH_TO_PROPERTIES_FILE = "parameters.properties";
    private static final String NAME_TABLE = "numbers";
    private static final Logger LOGGER = LogManager.getLogger(Main.class);


    public static void main(String[] args) {


        try {
            double start = System.currentTimeMillis();
            int value = new ConsoleInput().getMaxValue();

            Configuration configuration = new Configuration();
            configuration.setConfiguration(PATH_TO_PROPERTIES_FILE);

            Sql sql = new Sql(
                    configuration.getValue("jdbc.url"),
                    configuration.getValue("jdbc.login"),
                    configuration.getValue("jdbc.password")
            );
            sql.dropTable(NAME_TABLE);
            sql.createTable(NAME_TABLE);
            sql.fillColumnSql(value);
            List<String> list = sql.listFromSql(NAME_TABLE);

            Xml xml = new Xml(list);
            xml.createXml();
            xml.xslTransform();

            System.out.println("Сумма значений таблицы: " + xml.getSumXml());
            double end = System.currentTimeMillis() - start;
            System.out.println("Время выполнения программы: " + new DecimalFormat("#0.00").format(end / 60000) + " Минут");
        } catch (Exception ex) {
            LOGGER.error(ex);
        }

    }

}

