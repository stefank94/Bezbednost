# Bezbednost
Projekat iz Bezbednosti u sistemima elektronskog poslovanja
Aplikacija za kreiranje i upravljanje digitalnim sertifikatima

## Kratko uputstvo
- CertAuth - Spring aplikacija, glavni server za upravljanje sertifikatima. Pristupa mu se preko localhost:8080 ili localhost:9000.
- CSRGenerator - Swing aplikacija koja sluzi za pomoc pri generisanju CSR fajlova. Cuva CSR fajlove i generisani privatni kljuc u Java Key Store u fajl sistemu. CSR fajl se moze onda koristiti za upload na CertAuth server.

U sistemu trenutno postoji jedan automatski registrovan korisnik, admin. Kredencijali za logovanje: 
  - username: admin@admin.com
  - password: 123
