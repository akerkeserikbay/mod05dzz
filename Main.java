import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//SINGLETON

class ConfigurationManager {

    private static volatile ConfigurationManager instance;
    private ConcurrentHashMap<String, String> settings;

    private ConfigurationManager() {
        settings = new ConcurrentHashMap<>();
        System.out.println("ConfigurationManager создан.");
    }

    public static ConfigurationManager getInstance() {
        if (instance == null) {
            synchronized (ConfigurationManager.class) {
                if (instance == null) {
                    instance = new ConfigurationManager();
                }
            }
        }
        return instance;
    }

    public void setSetting(String key, String value) {
        settings.put(key, value);
    }

    public String getSetting(String key) {
        if (!settings.containsKey(key)) {
            throw new RuntimeException("Настройка не найдена: " + key);
        }
        return settings.get(key);
    }

    public void saveToFile(String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (String key : settings.keySet()) {
            writer.write(key + "=" + settings.get(key));
            writer.newLine();
        }
        writer.close();
    }

    // Загрузка из файла
    public void loadFromFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("=");
            if (parts.length == 2) {
                settings.put(parts[0], parts[1]);
            }
        }
        reader.close();
    }

    // Дополнительное задание — имитация загрузки из БД
    public void loadFromDatabase() {
        settings.put("db_user", "admin");
        settings.put("db_password", "1234");
        System.out.println("Настройки загружены из БД (имитация)");
    }
}

//BUILDER

class Report {
    private String header;
    private String content;
    private String footer;
    private String style;

    public void setHeader(String header) { this.header = header; }
    public void setContent(String content) { this.content = content; }
    public void setFooter(String footer) { this.footer = footer; }
    public void setStyle(String style) { this.style = style; }

    public void show() {
        System.out.println(style);
        System.out.println(header);
        System.out.println(content);
        System.out.println(footer);
    }

    // Дополнительное задание — изменение после создания
    public void updateContent(String newContent) {
        this.content = newContent;
    }
}

interface IReportBuilder {
    void setHeader(String header);
    void setContent(String content);
    void setFooter(String footer);
    void setStyle(String style);
    Report getReport();
}

class TextReportBuilder implements IReportBuilder {
    private Report report = new Report();

    public void setHeader(String header) {
        report.setHeader("TEXT HEADER: " + header);
    }

    public void setContent(String content) {
        report.setContent(content);
    }

    public void setFooter(String footer) {
        report.setFooter("END: " + footer);
    }

    public void setStyle(String style) {
        report.setStyle("Style: " + style);
    }

    public Report getReport() { return report; }
}

class HtmlReportBuilder implements IReportBuilder {
    private Report report = new Report();

    public void setHeader(String header) {
        report.setHeader("<h1>" + header + "</h1>");
    }

    public void setContent(String content) {
        report.setContent("<p>" + content + "</p>");
    }

    public void setFooter(String footer) {
        report.setFooter("<footer>" + footer + "</footer>");
    }

    public void setStyle(String style) {
        report.setStyle("<style>" + style + "</style>");
    }

    public Report getReport() { return report; }
}

// Дополнительный формат — XML
class XMLReportBuilder implements IReportBuilder {
    private Report report = new Report();

    public void setHeader(String header) {
        report.setHeader("<header>" + header + "</header>");
    }

    public void setContent(String content) {
        report.setContent("<content>" + content + "</content>");
    }

    public void setFooter(String footer) {
        report.setFooter("<footer>" + footer + "</footer>");
    }

    public void setStyle(String style) {
        report.setStyle("<style>" + style + "</style>");
    }

    public Report getReport() { return report; }
}

class ReportDirector {
    public void constructReport(IReportBuilder builder) {
        builder.setStyle("Default style");
        builder.setHeader("Отчет 2026");
        builder.setContent("Продажи выросли на 20%");
        builder.setFooter("Конец отчета");
    }
}

//PROTOTYPE

class Product implements Cloneable {
    private String name;
    private double price;
    private int quantity;

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String toString() {
        return name + " x" + quantity + " $" + price;
    }
}

class Discount implements Cloneable {
    private double percent;

    public Discount(double percent) {
        this.percent = percent;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String toString() {
        return percent + "%";
    }
}

class Order implements Cloneable {

    private List<Product> products = new ArrayList<>();
    private double deliveryCost;
    private Discount discount;
    private String paymentMethod;

    public void addProduct(Product p) {
        products.add(p);
    }

    public void setDeliveryCost(double cost) {
        this.deliveryCost = cost;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public void setPaymentMethod(String method) {
        this.paymentMethod = method;
    }

    public Object clone() throws CloneNotSupportedException {
        Order cloned = (Order) super.clone();

        cloned.products = new ArrayList<>();
        for (Product p : products) {
            cloned.products.add((Product) p.clone());
        }

        cloned.discount = (Discount) discount.clone();
        return cloned;
    }

    public void showOrder() {
        for (Product p : products) {
            System.out.println(p);
        }
        System.out.println("Доставка: " + deliveryCost);
        System.out.println("Скидка: " + discount);
        System.out.println("Оплата: " + paymentMethod);
    }
}

public class Main {

    public static void main(String[] args) throws Exception {

        System.out.println("===== SINGLETON TEST =====");

        Runnable task = () -> {
            ConfigurationManager config = ConfigurationManager.getInstance();
            System.out.println(Thread.currentThread().getName() +
                    " hash: " + config.hashCode());
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();

        ConfigurationManager config = ConfigurationManager.getInstance();
        config.setSetting("theme", "dark");
        config.loadFromDatabase();
        config.saveToFile("config.txt");

        System.out.println("\n===== BUILDER TEST =====");

        ReportDirector director = new ReportDirector();

        IReportBuilder textBuilder = new TextReportBuilder();
        director.constructReport(textBuilder);
        Report textReport = textBuilder.getReport();
        textReport.show();

        textReport.updateContent("Обновленное содержимое отчета");
        System.out.println("После изменения:");
        textReport.show();

        System.out.println("\nHTML REPORT:");
        IReportBuilder htmlBuilder = new HtmlReportBuilder();
        director.constructReport(htmlBuilder);
        htmlBuilder.getReport().show();

        System.out.println("\nXML REPORT:");
        IReportBuilder xmlBuilder = new XMLReportBuilder();
        director.constructReport(xmlBuilder);
        xmlBuilder.getReport().show();

        System.out.println("\n===== PROTOTYPE TEST =====");

        Order original = new Order();
        original.addProduct(new Product("Телефон", 500, 1));
        original.setDeliveryCost(30);
        original.setDiscount(new Discount(15));
        original.setPaymentMethod("Карта");

        Order cloned = (Order) original.clone();

        System.out.println("Оригинал:");
        original.showOrder();

        System.out.println("\nКлон:");
        cloned.showOrder();
    }
}