# PlayForward – Programsko inženjerstvo

PlayForward je web platforma koja povezuje donatore igračaka s korisnicima kojima su igračke potrebne te nudi pregled humanitarnih kampanja, recenzija i statusa donacija. Projekt je nastao u sklopu kolegija [Programsko inženjerstvo](https://www.fer.unizg.hr/predmet/proinz) na Fakultetu elektrotehnike i računarstva Sveučilišta u Zagrebu.

# Opis projekta

Ideja je objediniti sve korake doniranja igračaka: od registracije donatora i primatelja, objave dostupnih igračaka, vođenja informacija kampanja, lakok pretrazivanja i slicno. Backend servis (Spring Boot) izlaže REST API koji upravlja korisnicima, igračkama, kampanjama i recenzijama, dok frontend (React + Vite) osigurava moderno sučelje s heroj sekcijom, listama igračaka, obrascima za donacije i integriranom Google prijavom. Google OAuth2 i verifikacija putem e-mail tokena pokrivaju različite scenarije autentifikacije, a baza u PostgreSQL-u čuva sve entitete (korisnici, donatori, primatelji, kampanje, popisi igračaka, recenzije).

## Deploy (Render)
- **Frontend**: https://playforward-ee8c.onrender.com (Render servis URL).
- **Backend API**: https://playforward-backend-e5bg.onrender.com (base URL za `/api/*`).

# Funkcijski zahtjevi

- **Registracija i verifikacija korisnika** – klasična registracija (`/api/register`) + verifikacija e-mail tokenom te automatsko kreiranje korisnika preko Google OAuth2 prijave (CustomOAuth2UserService).
- **Autentifikacija i autorizacija** – Google Sign-In, provjera sesije preko `/api/auth/me`, odjava `/api/auth/logout`, prilagođeni CORS prema `APP_FRONTEND_URL` i sigurnosna pravila definirana u `SecurityConfig` (zaštićeni privatni API-jevi).
- **Upravljanje korisnicima i ulogama** – entiteti `Korisnik`, `Donator`, `Primatelj` i `Admin` određuju kontekst objave donacija, primanja pomoći i administracije.
- **Objava i pregled igračaka** – model `Igracka` sprema naziv, fotografiju, stanje, kategoriju, uvjete i status donacije (dostupno/rezervirano), a frontend nudi mock listu, pretraživanje i kartice s detaljima te obrazac za unos nove igračke (upload slike + API poziv prema `/api/toys`).
- **Upravljanje kampanjama i popisima potreba** – `Kampanja` prati rok trajanja i napredak, dok `PopisIgracaka` definira količine i status svake tražene igračke (potrebno/donirano) za konkretnog primatelja.
- **Recenzije i reputacija** – entitet `Recenzija` povezuje primatelja i donatora uz ocjenu i komentar, čime se gradi povjerenje i daje povratna informacija o iskustvu doniranja.
- **UX značajke** – responzivna naslovna stranica (Hero, RecentToys, HowItWorks), stranice za prijavu/registraciju, dashboard s informacijama o Google korisniku, priprema za chat i kampanje te jednostavno pretraživanje u navigaciji.

# Tehnologije

## Backend
- Java 17 + Spring Boot 3.5.7 (Web, Validation, Data JPA, Actuator)
- Spring Security OAuth2 client/resource server i prilagođeni `CustomOAuth2UserService`
- PostgreSQL baza (`backend/demo/baza_podataka.sql` definira tipove, tablice i inicijalne zapise)
- Maven build + Maven Wrapper, Dockerfile i `docker.compose.yml` za lokalni deployment

## Frontend
- React 19.1 + Vite 7.1 + React Router 7.9
- Tailwind CSS 4.1, Radix UI/shadcn komponentni sustav, Lucide ikone
- Fetch helper (`src/lib/api.js`) za HTTP pozive, Context API (`AuthContext`) za stanje autentifikacije
- Modularne komponente (Hero, RecentToys, HowItWorks, CampaignsTeaser, FinalCTA, Navbar), mock podaci (`src/data/myFakeData.js`) i API helper (`src/api/toys.js`)

## DevOps i ostalo
- ESLint 9, jsconfig i Tailwind konfiguracija
- Docker + Docker Compose (Spring aplikacija + PostgreSQL)
- Prezentacijski materijali (`PROINZ_TG01.4_PlayForward1*.pptx`) i primjer Google OAuth tajne (`client_secret_jbga.json`)

# Instalacija

## Preduvjeti
- Node.js ≥ 20 i npm ≥ 10
- Java 17 (JDK) + Maven (dostupan i `./mvnw`)
- PostgreSQL 15+ ili Docker Desktop
- Google Cloud projekt s omogućenom OAuth2 prijavom (Authorized redirect URI mora odgovarati backendu)

## Backend (lokalno)
1. `cd backend/demo`
2. Ažurirajte `src/main/resources/application.properties` ili `aplication.yml` s lokalnim DB podacima. Po želji uključite Flyway (`spring.flyway.enabled=true`).
3. Kreirajte bazu `playforward` i izvršite `psql -f baza_podataka.sql` kako biste dobili tipove, tablice i inicijalne podatke.
4. Postavite potrebne varijable okruženja (ako želite zamijeniti default vrijednosti):
   ```bash
   export GOOGLE_CLIENT_ID=xxx
   export GOOGLE_CLIENT_SECRET=yyy
   export APP_FRONTEND_URL=http://localhost:5173
   export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/playforward
   export SPRING_DATASOURCE_USERNAME=postgres
   export SPRING_DATASOURCE_PASSWORD=devpass
   ```
5. Pokrenite servis: `./mvnw spring-boot:run` (ili `mvn spring-boot:run`). API je na `http://localhost:8080`, Swagger UI na `/swagger-ui.html`, Actuator na `/actuator`, a Google login na `/oauth2/authorization/google`.

## Backend putem Docker Compose
1. `cd backend/demo`
2. Prema potrebi uredite `docker.compose.yml` (naziv baze, korisnik, lozinka, `SPRING_*` varijable).
3. Pokrenite `docker compose up --build`. PostgreSQL radi na portu 5432, aplikacija na 8080. Compose postavke nadjačavaju konfiguraciju iz `application.properties`.

## Frontend
1. `cd frontend/playforward`
2. Instalirajte ovisnosti: `npm install`
3. Kreirajte `.env` ili `.env.local` i definirajte `VITE_API_BASE_URL=http://localhost:8080` (frontend koristi ovaj URL za AuthContext i API pozive; default je Render backend).
4. Pokrenite razvojni server: `npm run dev` (Vite podiže aplikaciju na `http://localhost:5173`).
5. Za produkciju: `npm run build`, a dobiveni `dist/` direktorij poslužite preko Nginxa (osiguran je `nginx.conf` primjer) ili neke druge platforme.

## Konfiguracija okruženja
Za lokalni razvoj već postoje zadane vrijednosti u `backend/demo/src/main/resources/application.properties`.  
Za produkciju (npr. Render, Railway, Docker) postavite:

- `GOOGLE_CLIENT_ID` – OAuth2 Client ID iz Google Cloud Consolea.
- `GOOGLE_CLIENT_SECRET` – pripadajući Client Secret.
- `APP_FRONTEND_URL` – javni URL frontend aplikacije (koristi se za CORS, redirect i logout).
- `DB_URL`, `DB_USERNAME`, `DB_PASS`, `DB_DRIVER` – prema `application-deploy.properties`.

Ako varijable nisu zadane, aplikacija koristi lokalne vrijednosti iz konfiguracijskih datoteka. Frontend i backend razmjenjuju kolačiće pa razvojni URL-ovi moraju koristiti isti protokol/domen (npr. `http://localhost`).

# Struktura repozitorija
- `backend/demo` – Spring Boot aplikacija, Dockerfile, Compose konfiguracija i SQL shema (`baza_podataka.sql`).
- `frontend/playforward` – React + Vite frontend s komponentama, stranicama, AuthContextom i Tailwind konfiguracijom.
- `client_secret_jbga.json` – primjer Google OAuth konfiguracije.
- `PROINZ_TG01.4_*.pptx` – prezentacija ideje / statusa projekta.

# Članovi tima 
- [Ante Boban](https://github.com/ab010404)
- [Filip Radanović](https://github.com/ficoki)
- [Filip Tojčić](https://github.com/filiptojcic)
- [Hrvoje Juračić](https://github.com/hrvojejuracic)
- [Martin Vrbovčan](https://github.com/MartinVrbovcan)
- [Patrik Modrić](https://github.com/papi-5)
- [Albert Maršić](https://github.com/Albert-Marsic)

# 📝 Kodeks ponašanja [![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-2.1-4baaaa.svg)](CODE_OF_CONDUCT.md)
Kao studenti sigurno ste upoznati s minimumom prihvatljivog ponašanja definiran u [KODEKS PONAŠANJA STUDENATA FAKULTETA ELEKTROTEHNIKE I RAČUNARSTVA SVEUČILIŠTA U ZAGREBU](https://www.fer.hr/_download/repository/Kodeks_ponasanja_studenata_FER-a_procisceni_tekst_2016%5B1%5D.pdf), te dodatnim naputcima za timski rad na predmetu [Programsko inženjerstvo](https://wwww.fer.hr).
Očekujemo da ćete poštovati [etički kodeks IEEE-a](https://www.ieee.org/about/corporate/governance/p7-8.html) koji ima važnu obrazovnu funkciju sa svrhom postavljanja najviših standarda integriteta, odgovornog ponašanja i etičkog ponašanja u profesionalnim aktivnosti. Time profesionalna zajednica programskih inženjera definira opća načela koja definiranju  moralni karakter, donošenje važnih poslovnih odluka i uspostavljanje jasnih moralnih očekivanja za sve pripadnike zajenice.

Kodeks ponašanja skup je provedivih pravila koja služe za jasnu komunikaciju očekivanja i zahtjeva za rad zajednice/tima. Njime se jasno definiraju obaveze, prava, neprihvatljiva ponašanja te odgovarajuće posljedice. U ovom repozitoriju dan je jedan od široko prihvaćenih kodeksa ponašanja za rad u zajednici otvorenog koda – prilagodite ga potrebama projektnog tima.


# 📝 Licenca
Važeća (1)
[![CC BY-NC-SA 4.0][cc-by-nc-sa-shield]][cc-by-nc-sa]

Ovaj repozitorij sadrži otvorene obrazovne sadržaje i licenciran je prema pravilima Creative Commons licence koja omogućava preuzimanje i dijeljenje djela uz uvjet navođenja autora, nekorištenja u komercijalne svrhe te dijeljenja pod istim uvjetima ([Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License HR][cc-by-nc-sa]).

[![CC BY-NC-SA 4.0][cc-by-nc-sa-image]][cc-by-nc-sa]

[cc-by-nc-sa]: https://creativecommons.org/licenses/by-nc/4.0/deed.hr 
[cc-by-nc-sa-image]: https://licensebuttons.net/l/by-nc-sa/4.0/88x31.png
[cc-by-nc-sa-shield]: https://img.shields.io/badge/License-CC%20BY--NC--SA%204.0-lightgrey.svg

Original [![cc0-1.0][cc0-1.0-shield]][cc0-1.0]
>
>COPYING: All the content within this repository is dedicated to the public domain under the CC0 1.0 Universal (CC0 1.0) Public Domain Dedication.
>
[![CC0-1.0][cc0-1.0-image]][cc0-1.0]

[cc0-1.0]: https://creativecommons.org/licenses/by/1.0/deed.en
[cc0-1.0-image]: https://licensebuttons.net/l/by/1.0/88x31.png
[cc0-1.0-shield]: https://img.shields.io/badge/License-CC0--1.0-lightgrey.svg

### Reference na licenciranje repozitorija
- Primarna licenca repozitorija definirana je u [LICENSE](LICENSE) (CC BY-NC-SA 4.0) te se odnosi na cjelokupni obrazovni materijal.
- Pojedini prilozi (slike, modeli, prezentacije) i vanjske ovisnosti zadržavaju vlastite licence – provjerite podatke u izvornim datotekama ili dokumentaciji paketa.
- Izvorni autori zadržavaju moralna prava na kod i dokumentaciju, a treće strane smiju dijeliti i prilagođavati sadržaj uz navođenje izvora, nekorištenje u komercijalne svrhe i dijeljenje pod istim uvjetima.
