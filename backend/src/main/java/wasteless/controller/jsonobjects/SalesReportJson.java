package wasteless.controller.jsonobjects;

import wasteless.model.Business;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SalesReportJson {

    private final String periodStart;

    private final String periodEnd;

    private final List<SalesReportSection> sections;

    private final Business business;

    public SalesReportJson(LocalDate periodStart,
                           LocalDate periodEnd,
                           List<SalesReportSection> sections,
                           Business business) {
        this.periodStart = periodStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.periodEnd = periodEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.sections = sections;
        this.business = business;
    }

    @Override
    public String toString() {
        ArrayList<String> sectionStringList = new ArrayList<>();
        for (SalesReportSection section : sections) {
            sectionStringList.add(section.toString());
        }
        String sectionsString = String.join(", ", sectionStringList);
        return String.format(
                "{"
                        + " \"periodStart\": \"%s\","
                        + " \"periodEnd\": \"%s\","
                        + " \"sections\": [%s],"
                        + " \"business\": %s"
                        + "}",
                periodStart,
                periodEnd,
                sectionsString,
                business.getJson());
    }
}
