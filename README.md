# AquaFlow – Application Android de suivi de la consommation d'eau

AquaFlow est une application Android (Kotlin) qui permet de surveiller la consommation d'eau d'un foyer, visualiser les rapports d'usage, et suivre l'état de différents capteurs.

L'interface est inspirée d'une maquette Figma et construite en XML + Fragments, avec un BottomNavigationView et un menu latéral (DrawerLayout).

---

## 🚀 Fonctionnalités principales

### Accueil – Résumé d'aujourd'hui
- Affichage du volume total consommé aujourd'hui.
- Indication de l'évolution par rapport à la veille, avec un code couleur :
  - Vert : consommation normale / améliorée
  - Orange / Rouge : surconsommation ou anomalie
- Icône d'alerte qui change de couleur selon le niveau de risque.
- Graphique horizontal personnalisé affichant la consommation par tranche horaire.

### Rapports
- Vue dédiée aux rapports de consommation avec trois onglets :
  - **Aujourd'hui** : consommation horaire groupée par tranches de 4 heures
  - **Dans la semaine** : consommation des 7 derniers jours
  - **Ce mois-ci** : consommation mensuelle groupée par semaines
- Cartes de synthèse (moyenne, alertes actives, gaspillage, total de litres).
- Histogrammes et graphes pour visualiser les tendances.

### Capteurs
- Liste des capteurs connectés affichée dans un RecyclerView.
- Pour chaque capteur :
  - Nom et localisation
  - Volume actuel en litres
  - Dernière mise à jour
  - Statut visuel avec code couleur :
    - 🟢 Vert : OK (fonctionnement normal)
    - 🟡 Jaune : WARNING (avertissement)
    - 🔴 Rouge : ERROR (erreur critique)
    - ⚪ Gris : INACTIF

### Navigation
- **BottomNavigationView** avec 3 onglets :
  - Accueil
  - Rapports
  - Capteurs
- **Menu latéral** (Navigation Drawer) accessible via le bouton menu dans la top bar :
  - Accueil
  - Rapports
  - Capteurs

---

## 🏗️ Architecture & Technologies

- **Langage** : Kotlin
- **UI** : XML + Fragments
- **Base de données locale** : Room (SQLite)
- **Navigation** :
  - `FrameLayout` pour les fragments
  - `BottomNavigationView`
  - `DrawerLayout` + `NavigationView`
- **Gestion asynchrone** : Coroutines Kotlin

### Structure du projet

```
app/src/main/java/com/example/aquaflow/
│
├── data/
│   ├── AppDatabase.kt          # Configuration Room
│   ├── SensorDao.kt            # DAO pour les capteurs
│   ├── HourlyUsageDao.kt       # DAO pour la consommation horaire
│   └── DayUsageDao.kt          # DAO pour la consommation journalière
│
├── model/
│   ├── Sensor.kt               # Entité capteur
│   ├── SensorStatus.kt         # Enum des statuts
│   ├── HourlyUsage.kt          # Entité consommation horaire
│   └── DayUsage.kt             # Entité consommation journalière
│
├── ui/
│   ├── home/
│   │   └── HomeFragment.kt
│   ├── reports/
│   │   └── ReportsFragment.kt
│   └── sensors/
│       ├── SensorsFragment.kt
│       └── SensorsAdapter.kt
│
└── MainActivity.kt
```
  
## 💾 Base de données  
  
L'application utilise **Room** pour stocker localement :  
  
### Tables  
- **sensors** : informations sur les capteurs (nom, localisation, volume, statut, dernière mise à jour)  
- **hourly_usage** : consommation horaire par date et heure  
- **day_usage** : consommation totale par jour  
  
### Données de test  
La base de données est pré-remplie avec des données simulées au premier lancement :  
- 4 capteurs avec différents statuts  
- Consommation horaire pour aujourd'hui (24 heures)  
- Consommation journalière pour les 30 derniers jours  
  
---  
  
## 📦 Dépendances principales  
  
```toml  
[versions]  
kotlin = "2.0.21"  
room = "2.6.1"  
ksp = "2.0.21-1.0.28"  
  
[libraries]  
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }  
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }  
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
```

▶️ Lancer le projet
1. Cloner le dépôt :
```bash
git clone https://github.com/brendaolivr/AquaFlow.git
cd AquaFlow
```
2. Ouvrir le projet dans Android Studio :

    File → Open → Sélectionner le dossier AquaFlow

3. Synchroniser Gradle :

    Android Studio va automatiquement télécharger les dépendances
    Attendre la fin de la synchronisation

4. Lancer l'application :

    Cliquer sur le bouton ▶️ (Run)
    Sélectionner un émulateur ou un appareil Android connecté
