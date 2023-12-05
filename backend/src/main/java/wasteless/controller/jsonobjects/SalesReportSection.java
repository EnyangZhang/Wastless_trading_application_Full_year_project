package wasteless.controller.jsonobjects;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SalesReportSection {

    private final String name;

    private final String sectionStart;

    private final String sectionEnd;

    private final int numberOfSales;

    private final double valueOfSales;

    public SalesReportSection(String name,
                              LocalDate sectionStart,
                              LocalDate sectionEnd,
                              int numberOfSales,
                              double valueOfSales) {
        this.name = name;
        this.sectionStart = sectionStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.sectionEnd = sectionEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.numberOfSales = numberOfSales;
        this.valueOfSales = valueOfSales;
    }

    public String toString() {
        return String.format(
                "{"
                        + " \"name\": \"%s\","
                        + " \"sectionStart\": \"%s\","
                        + " \"sectionEnd\": \"%s\","
                        + " \"numberOfSales\": %d,"
                        + " \"valueOfSales\": %.2f"
                        + "}",
                name,
                sectionStart,
                sectionEnd,
                numberOfSales,
                valueOfSales);
    }
}
