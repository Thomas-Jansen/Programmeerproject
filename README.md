# Programmeerproject door Thomas Jansen 11008938

# PlantBase Project Proposal

### Probleemschets
Verse kruiden en groenten, gegroeid in eigen tuin of op de vensterbank, smaken altijd beter dan van de supermarkt. Veel mensen missen echter de groene vingers om de plantjes in leven te houden en tot een succesvolle oogst te brengen. Dit kan komen door een gebrek aan informatie over de juiste groeicondities van de plant, of simpelweg het vergeten de plant te verzorgen.

### Voorgestelde oplossing
PlantBase is een app waarin de beste groeiomstandigheden van planten opgezocht kunnen worden. De app geeft daarmee inzicht over hoeveel licht de plant nodig heeft, de juiste grond en hoe vaak water gegeven moet worden. Deze informatie kan uitgebreid worden met user input over de voortgang van het groeiproces. Gebruikers kunnen de groei van hun planten documenteren en dit delen met andere gebruikers om elkaar zo te helpen. 
Daarnaast kan de app gelinked worden aan een klein kastje dat een arduino bevat met verschillende sensoren. Zo houdt de arduino bij hoeveel licht de plant krijgt, wat de temperatuur een luchtvochtigheid zijn en hoe vochtig de grond is. Deze informatie wordt naar de app getsuurd en inzichtelijk weergegeven aan de gebruiker. Ook krijgt de gebruiker een berichtje wanneer de plant water moet krijgen.

## Belangrijke functies
- Account maken en beheren 
- Planten zoeken en toevoegen aan eigen database van planten
- Notities maken bij plant om groei te documenteren
- Foto's toevoegen bij een plant
- Bevindingen en foto's delen met anderen en raten
- Toevoegen nieuwe planten

## Optionele functies
- Herinnering instellen water te geven
- Arduino prototype met sensoren voor datacollectie
- Overzichtelijke grafieken van data afkosmstig van Arduino
- Pushberichten wanneer potgrond te droog is
- Engelse en Nederlandse versie

## Databronnen
Allereerst zal een dataset aangemaakt moeten worden met ±20 planten. Deze informatie zal van o.a. de volgende sites gehaald worden:
- https://www.dpi.nsw.gov.au/agriculture
- https://www.thekitchn.com/everything-you-need-to-know-about-growing-basil-221272
Deze informatie zal handmatig worden ingevoerd. Gebrukikers kunnen nieuwe planten toevoegen om de dataset uit te breiden.
Eventueel kan gezocht worden naar een website waar de informatie van gescraped kan worden.

Alle informatie moet worden opgeslagen in een database, hiervoor zal Firebase gebruikt worden.

## Vergelijkbare apps
#### GrowBuddy
Growbuddy is een iOS app waarin men bij kan houden hoe de plant groeit. Men kan foto's toevoegen en alle stadia van de plant documenteren. De app is echter alleen voor gebruik met iPads.

#### iGarden
iGarden is een iOS app, speciaal ontwikkeld voor de VS, waar men informatie kan opzoeken over het groeien van de planten. Ook kan men informatie delen met andere gebruikers over problemen tijdens het groeien en hoe men deze kan oplossen. 

## Grootste obstakel
Bij het implementeren is het grootste obstakel het verzamelen van de informatie over de planten.
Er zal nog verder gezocht moeten worden naar websites waar deze informatie overzichtelijk wordt weeregegeven.


Wanneer het integreren van de Arduino wordt goedgekeurd, zal dit extra complexiteit toevoegen omdat de arduino en app met elkaar moeten kunnen comminuceren.


# Ontwerpvoorstel PlantBase
![Alt text](https://github.com/Thomas-Jansen/Programmeerproject/blob/master/doc/PlantBase_design.png)





### Bronnen
- https://material.io/tools/icons/?style=baseline
- http://www.thesocialmediatoday.com/android-app-templates-with-google-play-demo/
- http://veggieharvest.com/
- https://www.pexels.com
