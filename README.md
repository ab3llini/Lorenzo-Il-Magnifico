**GRUPPO DI LAVORO:** 
LM32

**PARTECIPANTI:**
Alberto Mario Bellini - 827520
Lorenzo Barcella - 827703
Federico Di Dio - 827796


**IMPLEMENTAZIONE:**
- Regole complete del gioco
- RMI
- Socket
- GUI
- CLI
- 5 giocatore
- Persistenza
- Gestione disconnessione, riconnessione.
- Configurazione completa paramentri gioco, carte ecc tramite file JSON

**AVVIAMENTO:**
Nel package client c'è il launcher completo che permette di avviare GUI / CLI.
Per avviare il server, navigare al package server, contrller, game e avviare GameEngine.

**FUNZIONAMENTO:**
Dopo essersi registrati o loggati, in automatico si entra in una lobby in attesa di altri giocatori.
Se ci si logga con uno username di un giocatore che era in una partita non terminata, si entra in una lobby speciale detta persistente:
In questo caso non partono timeout automatici quando si connette il secondo giocatore ma si attende che tutti gli altri giocatori della partita si riconnettano
per portarla a termine.
Di default ogni giocatore a 120s per fare una mossa, dopo i quali verrà sospeso e dovrà riconnettersi.
Sono state gestite eventuali disconnessioni da parte di giocatori: in questo caso al login si rientrerà immediatamente in partita.
Se la connessione cade mentre si ha il turno, si cede il turno.
Se il server va offline si riprende immediatamente dall'inizio del turno dell'ultimo giocatore.


**REGOLAMENTO AGGIUNTIVO 5 GIOCATORE :**
Nel draft si utilizzano tutte le 20 carte leader e anche la bonus tile di 'default' delle regole semplificate.
Le risorse iniziali del quinto giocatore sono in linea con quelle degli altri (9 monete,2 pietre,2 legni,3 servi).
La board di gioco non cambia.
L'algoritmo del lancio dei dadi non è più casuale ma garantisce una somma totale dei 3 dadi che sia almeno di 14, questo pochè il problema principale non è tanto il non saper dove
mettere i familiari quanto piuttosto il poterli mettere anche in posizioni alte delle torri. Inoltre col quinto giocatore non vale più la regola per la quale in una torre non
ci possono essere due familiari di uno stesso giocatore(resta tuttavia attivo il prezzo aggiuntivo di 3 monete nel caso di torre occupata). Posizionando tutti e 5 i familiari i giocatori
'occupano' 20 action place sulla board. Con le nostre regole i posti disponibili in cui metterei familiari anche escludendo i posti azione composti (council palace, harvest & production)
sono 22 e quindi perfetti per rendere il gioco 'giocabile' (16 posti sulle torri + 4 nel mercato + 2 nei singoli posti azione).

**PERSISTENZA:**
Database con 2 tabelle, una per i giocatori e una per le partite.
Un giocatore quando si connette viene automaticamente aggiunto nella lobby persistent in caso abbia una partita nel database NON terminata.
Per questo motivo non possono esistere due partite non terminate per un singolo giocatore.

**LIBRERIE ESTERNE:**
Abbiamo usato GSON per il parsing JSON e JFoenix per un bottone e uno spinner nella GUI.
Il resto, compreso styling CSS su GUI, Styiling CLI e altro, è stato realizzato a mano.
