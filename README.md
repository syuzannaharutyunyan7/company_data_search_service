# Company Data Search Service

A simple Spring Boot application that searches UK companies from the Companies House website, stores results in an H2 database, and returns structured JSON data.

---

## How to Run

### Requirements

* Java 17+
* Maven

### Run the project

```bash
mvn spring-boot:run
```

The application will start at:

```
http://localhost:8080
```

---

## H2 Database

This project uses an in-memory H2 database and You can open the database console here:

```
http://localhost:8080/h2-console
```

**Connection details:**

* JDBC URL: `jdbc:h2:mem:companydb`
* Username: `sa`
* Password: *(empty)*

---

## API

### Search companies

```
GET /api/companies/search?query={query}
```

---

### Example request

```bash
curl "http://localhost:8080/api/companies/search?query=tesco"
```

---

### Example response

```json
[
  {
    "id": 10,
    "companyNumber": "unknown",
    "name": "TESCO PLC",
    "status": "unknown",
    "type": "unknown",
    "address": "Tesco House, Shire Park, Kestrel Way, Welwyn Garden City, United Kingdom, AL7 1GA",
    "incorporationDate": "unknown",
    "officers": [],
    "persons": []
  },
  {
    "id": 11,
    "companyNumber": "unknown",
    "name": "15680114 LTD",
    "status": "unknown",
    "type": "unknown",
    "address": "Unity Building, 20 Chapel Street, Liverpool, England, L3 9AG",
    "incorporationDate": "unknown",
    "officers": [],
    "persons": []
  }
]
```

---

## Example data stored in H2

After a search, data is stored in the database like this:

```
ID | NAME                              | COMPANY_NUMBER | STATUS  | ADDRESS
---|-----------------------------------|----------------|---------|--------------------------------
10 | TESCO PLC                         | unknown        | unknown | Tesco House, Welwyn Garden City
11 | 15680114 LTD                      | unknown        | unknown | Unity Building, Liverpool
12 | TESCO AQUA (FINCO2) LIMITED       | unknown        | unknown | 1 More London Place, London
13 | TESCO AQUA (1LP) LIMITED          | FC026996       | Closed  | unknown
```

---

## Caching

The application uses simple database caching.

* First, it checks if results already exist in the database
* If yes → returns them directly
* If no → it scrapes Companies House, saves results, and returns them

This avoids repeated scraping for the same query.

---

## What was hard

The hardest part was scraping the Companies House website because:

* HTML structure is not always consistent
* I have done only the core requirements part and have not done the possible optional extensions
* The database selection was hard because I wanted to use database PostreSQL and Docker as I have experience with them, but the bulding part caused error so I use H2 database, which stores everything without any installation.
* Some data is missing or incomplete, because after getting results I have got missing parts in the data.
* Parsing company details needs careful handling
* I have difficulty with scraping because officers and persons list is not scraped correctly and even in the H2 database writing the query and selecting them nothing is printed.
* My program first get the user search then check cache and database if it is not found then it scrapes the websute convert the html into object and saves into database. The problem that I got that it was not getting anything, then I have noticed that officers and persons in the code are not scraped added this part to make them to loop over them,
            for (Element el : doc.select("li")) {
            String text = el.text().toLowerCase();
  but no response, and I think that something in the logic is not correct. 
The part I’m unsure about is whether the issue is in my parsing logic, or if the website is blocking HTML scraping, which prevents me from retrieving the correct information and I get unknown in fields.

---

## What is not finished / improvements

* Officers and persons are not scraped yet (empty lists)
* No cache expiration (data may become outdated)
* No deduplication of results
* No unit tests

---
