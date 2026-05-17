package com.example.companysearch.service;

import com.example.companysearch.model.Company;
import com.example.companysearch.model.Officer;
import com.example.companysearch.model.Person;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompaniesHouseScraper {

    private static final String BASE_SEARCH =
            "https://find-and-update.company-information.service.gov.uk/search?q=";

    private static final String BASE_URL =
            "https://find-and-update.company-information.service.gov.uk";

    private static final int LIMIT = 100;
    private static final int DELAY_MS = 300;

    public List<Company> scrape(String query) {

        List<Company> companies = new ArrayList<>();

        try {
            Elements results = connect(BASE_SEARCH + query)
                    .select("a[href*='/company/']");

            int count = 0;

            for (Element el : results) {

                if (count++ >= LIMIT) break;

                String href = el.attr("href");
                if (href.isBlank()) continue;

                companies.add(scrapeCompanyPage(BASE_URL + href));

                Thread.sleep(DELAY_MS);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return companies;
    }

    private Company scrapeCompanyPage(String url) throws Exception {

        Document doc = connect(url);

        Company company = new Company();
        company.setName(getText(doc, "h1"));
        company.setCompanyNumber(getField(doc, "Company number"));
        company.setStatus(getField(doc, "Status"));
        company.setAddress(getField(doc, "Registered office address"));

        company.setType("unknown");
        company.setIncorporationDate("unknown");

        company.setOfficers(scrapeOfficers(doc));
        company.setPersons(scrapePersons(doc));

        return company;
    }

    private List<Officer> scrapeOfficers(Document doc) {

        List<Officer> list = new ArrayList<>();

        for (Element el : doc.select("li")) {

            String text = el.text().toLowerCase();

            if (text.contains("director") || text.contains("secretary")) {
                Officer o = new Officer();
                o.setName(el.text());
                o.setRole("unknown");
                o.setAppointmentDate("unknown");
                list.add(o);
            }
        }

        return list;
    }

    private List<Person> scrapePersons(Document doc) {

        List<Person> list = new ArrayList<>();

        for (Element el : doc.select("li")) {

            if (el.text().toLowerCase().contains("control")) {
                Person p = new Person();
                p.setName(el.text());
                p.setNatureOfControl("unknown");
                list.add(p);
            }
        }

        return list;
    }

    private Document connect(String url) throws Exception {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (educational project)")
                .timeout(10000)
                .get();
    }

    private String getText(Document doc, String selector) {
        return doc.select(selector).text();
    }

    private String getField(Document doc, String fieldName) {
        for (Element dt : doc.select("dt")) {
            if (dt.text().equalsIgnoreCase(fieldName)) {
                Element dd = dt.nextElementSibling();
                return dd != null ? dd.text().trim() : "unknown";
            }
        }
        return "unknown";
    }
}