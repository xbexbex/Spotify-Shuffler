# Testausdokumentaatio

**Testit**: Testejä on vähän, sillä automaattinen testaus ei ole ohjelman tapauksessa erityisen mielekästä, useista syistä.
* Ensinnäkin ohjelmalla on tällä hetkellä vain yksi ja ainut funktio: Kirjautua sisään ja
sekoittaa soittolista. Tämän tehtävän onnistuminen on loppujen lopuksi todennettavissa vain ja ainoastaan omin silmin katsomalla kyseistä soittolistaa Spotifyn avulla.
* Ohjelma käyttää Spotifyn nettirajapintaa. Tämän takia testit voivat epäonnistua monista ulkoisista syistä - nettiyhteys voi pettää hetkeksi, Spotify voi kaatua tai olla hidas, tai estää requesteja
* Testaus vaatii Spotifyn käyttäjän. Käyttäjä saattaa olla samalla hetkellä käytössä (esim. testattavana) tai väärin konfiguroitu. Sitä ei myöskään voi alustaa tai resetoida testien jälkeen.
* Luokat riippuvat niin paljon toisistaan, että niiden yksittäinen testaaminen on mahdotonta. Lisäksi yksittäisten vaiheiden onnistumista on vaikea todentaa.
* Ohjelma pitäisi sulkea ja uudelleenkäynnistää testien välissä, ja luokkatestit olisi ajettava tietyssä järjestyksessä. Ymmärtääkseni molemmat näistä ovat mahdotonta junitissa, tai en itse ainakaan löytänyt tapaa tehdä niin.

**Käsin testaus**: Itse tyydyin vain testaamaan ohjelmaa käsin, kirjautumalla aina sisään ja sekoittamalla listoja, samalla tarkistaen spotifyn ohjelmasta että sekoitus todellakin toimii.

**Bugit**: 
* Tiettyjä kappaleita ei voi tällä hetkellä poistaa, eikä paikallisia kappaleita voi lisätät soittolistoihin. Tästä johtuen on mahdotonta sekoittaa tai luoda listoja, joissa on paikallisia kappaleita.
* Viestipalkki näyttää virheitä välillä, johtuen siitä että Swing ei päivity kunnolla kun ohjelma suorittaa jotain tehtävää.
* Useita soittolistoja kerralla luodessa joskus tulee samannimisiä listoja. Nimenetsimisalgoritmi ei toimi oikein, enkä ole vielä korjannut sitä. Ei vaikuta funktionalisuuteen, sillä Spotify tunnistaa listat niiden ID:n perusteella.

