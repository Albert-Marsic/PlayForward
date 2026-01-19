DO $$
BEGIN
    CREATE TYPE stanje_igracke AS ENUM ('novo', 'korišteno');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$
BEGIN
    CREATE TYPE status_igracke AS ENUM ('dostupno', 'rezervirano');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$
BEGIN
    CREATE TYPE status_popisa_igracaka AS ENUM ('potrebno', 'donirano');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$
BEGIN
    CREATE TYPE status_zahtjeva AS ENUM ('PENDING', 'APPROVED', 'COMPLETED', 'REJECTED', 'WITHDRAWN');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

CREATE TABLE IF NOT EXISTS KORISNIK (
    IDKorisnik INT GENERATED ALWAYS AS IDENTITY NOT NULL,
    imeKorisnik VARCHAR(20) NOT NULL,
    email VARCHAR(50) NOT NULL,
    PRIMARY KEY (IDKorisnik),
    UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS ADMIN (
    IDAdmin INT NOT NULL,
    PRIMARY KEY (IDAdmin),
    FOREIGN KEY (IDAdmin) REFERENCES KORISNIK(IDKorisnik)
);

CREATE TABLE IF NOT EXISTS PRIMATELJ (
    IDPrimatelj INT NOT NULL,
    PRIMARY KEY (IDPrimatelj),
    FOREIGN KEY (IDPrimatelj) REFERENCES KORISNIK(IDKorisnik)
);

CREATE TABLE IF NOT EXISTS DONATOR (
    IDDonator INT NOT NULL,
    PRIMARY KEY (IDDonator),
    FOREIGN KEY (IDDonator) REFERENCES KORISNIK(IDKorisnik)
);

CREATE TABLE IF NOT EXISTS IGRACKA (
    IDIgracka INT GENERATED ALWAYS AS IDENTITY NOT NULL,
    naziv VARCHAR(20) NOT NULL,
    kategorija VARCHAR(20) NOT NULL,
    stanje stanje_igracke NOT NULL,
    fotografija VARCHAR(50) NOT NULL,
    status status_igracke NOT NULL,
    uvjeti VARCHAR(100),
    IDDonator INT NOT NULL,
    IDPrimatelj INT,
    PRIMARY KEY (IDIgracka),
    FOREIGN KEY (IDDonator) REFERENCES DONATOR(IDDonator),
    FOREIGN KEY (IDPrimatelj) REFERENCES PRIMATELJ(IDPrimatelj)
);

CREATE TABLE IF NOT EXISTS ZAHTJEV (
    IDZahtjev INT GENERATED ALWAYS AS IDENTITY NOT NULL,
    status status_zahtjeva NOT NULL,
    datumZahtjeva TIMESTAMP NOT NULL,
    napomena VARCHAR(200),
    IDIgracka INT NOT NULL,
    IDPrimatelj INT NOT NULL,
    IDDonator INT NOT NULL,
    PRIMARY KEY (IDZahtjev),
    FOREIGN KEY (IDIgracka) REFERENCES IGRACKA(IDIgracka),
    FOREIGN KEY (IDPrimatelj) REFERENCES PRIMATELJ(IDPrimatelj),
    FOREIGN KEY (IDDonator) REFERENCES DONATOR(IDDonator)
);

CREATE TABLE IF NOT EXISTS KAMPANJA (
    IDKampanja INT GENERATED ALWAYS AS IDENTITY NOT NULL,
    rokTrajanja DATE NOT NULL,
    napredak VARCHAR(100) NOT NULL,
    IDPrimatelj INT NOT NULL,
    PRIMARY KEY (IDKampanja),
    FOREIGN KEY (IDPrimatelj) REFERENCES PRIMATELJ(IDPrimatelj)
);

CREATE TABLE IF NOT EXISTS POPIS_IGRACAKA (
    nazivIgracke VARCHAR(20) NOT NULL,
    kolicina INT NOT NULL,
    status status_popisa_igracaka NOT NULL,
    IDKampanja INT NOT NULL,
    PRIMARY KEY (nazivIgracke, IDKampanja),
    FOREIGN KEY (IDKampanja) REFERENCES KAMPANJA(IDKampanja)
);

CREATE TABLE IF NOT EXISTS RECENZIJA (
    IDRecenzija INT GENERATED ALWAYS AS IDENTITY NOT NULL,
    ocjena INT NOT NULL CHECK (ocjena BETWEEN 1 AND 5),
    tekst VARCHAR(500) NOT NULL,
    IDZahtjev INT NOT NULL,
    IDPrimatelj INT NOT NULL,
    IDDonator INT NOT NULL,
    PRIMARY KEY (IDRecenzija),
    UNIQUE (IDZahtjev),
    FOREIGN KEY (IDZahtjev) REFERENCES ZAHTJEV(IDZahtjev),
    FOREIGN KEY (IDPrimatelj) REFERENCES PRIMATELJ(IDPrimatelj),
    FOREIGN KEY (IDDonator) REFERENCES DONATOR(IDDonator)
);

INSERT INTO KORISNIK (imeKorisnik, email)
VALUES ('Albert', 'abi@mail.com')
ON CONFLICT (email) DO NOTHING;
