package com.example.companysearch.service;

import com.example.companysearch.model.Company;
import com.example.companysearch.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompaniesHouseScraper scraper;

    private static class CacheEntry {
        List<Company> data;
        long timestamp;
    }

    private final Map<String, CacheEntry> cache = new HashMap<>();
    private static final long TTL = 60 * 60 * 1000;

    public CompanyService(CompanyRepository companyRepository,
                          CompaniesHouseScraper scraper) {
        this.companyRepository = companyRepository;
        this.scraper = scraper;
    }

    public List<Company> search(String query) {

        String q = normalize(query);

        List<Company> cached = getFromCache(q);
        if (cached != null) return cached;

        List<Company> dbResult =
                companyRepository.findByNameContainingIgnoreCase(q);

        if (!dbResult.isEmpty()) {
            return storeAndReturn(q, dbResult);
        }

        List<Company> scraped = scraper.scrape(query);
        companyRepository.saveAll(scraped);

        return storeAndReturn(q, scraped);
    }

    private String normalize(String q) {
        return q == null ? "" : q.trim().toLowerCase();
    }

    private List<Company> getFromCache(String q) {

        CacheEntry entry = cache.get(q);
        if (entry == null) return null;

        if (System.currentTimeMillis() - entry.timestamp > TTL) {
            cache.remove(q);
            return null;
        }

        return entry.data;
    }

    private List<Company> storeAndReturn(String q, List<Company> data) {

        cache.put(q, createEntry(data));
        return data;
    }

    private CacheEntry createEntry(List<Company> data) {
        CacheEntry entry = new CacheEntry();
        entry.data = data;
        entry.timestamp = System.currentTimeMillis();
        return entry;
    }
}