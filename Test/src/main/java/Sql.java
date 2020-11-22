import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Sql {

    private static final Logger LOGGER = LogManager.getLogger(Sql.class);
    private String url;
    private String user;
    private String pass;
    private int count;

    public Sql(String url, String user, String pass) {
        this.url = url;
        this.user = user;
        this.pass = pass;
    }

    //Удаляем таблицу
    public void dropTable(String nameTable) {
        try (Connection connection = DriverManager.getConnection(url, user, pass)) {
            connection.createStatement().executeUpdate("DROP TABLE " + nameTable);
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }

    //Очищаем таблицу
    public void clearTable(String nameTable) {
        try (Connection connection = DriverManager.getConnection(url, user, pass)) {
            connection.createStatement().executeUpdate("DELETE FROM " + nameTable);
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }


    //Создаем таблицу
    public void createTable(String nameTable) {
        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE " + nameTable + " (Num int)")) {
            preparedStatement.executeUpdate();

        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }

    //Заполняем таблицу
    public void fillColumnSql(int count) {

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO numbers(Num) VALUES (?)")) {

            connection.setAutoCommit(false);
            for (int i = 1; i <= count; i++) {

                preparedStatement.setInt(1, i);
                preparedStatement.addBatch();

                if (i % 100000 == 0 || i == count) {
                    preparedStatement.executeBatch();
                    connection.commit();
                    preparedStatement.clearBatch();
                }
            }

        } catch (Exception ex) {
            LOGGER.error(ex);
        }

    }

    //Формируем список записей из таблицы
    public List<String> listFromSql(String nameTable) {
        List<String> data = new ArrayList<String>();

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT Num FROM " + nameTable);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                data.add(resultSet.getString("Num"));
            }

        } catch (Exception ex) {
            LOGGER.error(ex);
        }
        return data;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
