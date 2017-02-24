# Rakennekuvaus

**MainLogic**: Ohjelman päälogiikkana toimii MainLogic -luokka. MainLogic välittää tietoa eri luokkien ja GUI:n välillä, ja kutsuu eri luokkien metodeja.

**MainGUI**: MainGUI on ohjelman käyttöliittymä, ja toimii MainLogicin alaisuudessa. MainGUI välittää MainLogicille mm. nappien painallukset. Vain MainLogic käyttää MainGUIta.

**WebSite**: Luokka jonka tehtävänä on kirjautua Spotifyn nettisivulle ja palauttaa MainLogicille Spotifylta saatava tunnuskoodi, jota puolestaan käytetään ohjelman valtuuttamiseen ja api-toimintoihin.

**ApiFunctionHandler**: Hoitaa kaikki spotify web apiin liittyvät asiat, kuten soittolistojen haun. Käyttää hyväkseen virallista java spotify web api wrapperia.

**AuthTimer**: Ajastin, jonka ainoa tehtävä on käskeä ApiFunctionHandleria uudelleenvaltuuttamaan ohjelma ennen kuin koodin käyttöikä loppuu.

**Config**: Tallentaa ja hakee käyttäjän asetuksen tiedostosta.

**Playlist**: Playlist-olio, joka tallentaa spotify-playlistin eri tietoja.
