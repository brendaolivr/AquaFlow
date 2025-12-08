# AquaFlow – Application Android de suivi de la consommation d’eau

AquaFlow est une application Android (Kotlin) qui permet de **surveiller la consommation d’eau** d’un foyer, visualiser les **rapports d’usage**, et suivre l’état de différents **capteurs** (chaudière, jardin, salle de bain, etc.).

L’interface est inspirée d’une maquette Figma et construite en **XML + Fragments**, avec un **BottomNavigationView** et un **menu latéral (DrawerLayout)**.

---

## 🚀 Fonctionnalités principales

### Accueil – Résumé d’aujourd’hui
- Affichage du **volume total consommé** aujourd’hui (en litres).
- Indication de l’**évolution par rapport à la veille** (en %), avec un code couleur :
  - Vert : consommation normale / améliorée
  - Orange / Rouge : surconsommation ou anomalie
- Icône d’alerte qui change de couleur selon le niveau de risque.
- **Graphique horizontal personnalisé** :
  - Axe **Y = heures** de la journée.
  - Axe **X = volume** : barres horizontales qui partent de 0 et s’allongent proportionnellement à la consommation.
  - Légende en bas exprimée en **litres (0L, 5L, 10L, …)**.

### Rapports
- Vue dédiée aux **rapports de consommation** avec trois onglets :
  - **Aujourd’hui**
  - **Dans la semaine**
  - **Ce mois-ci**
- Cartes de synthèse (moyenne, alertes actives, gaspillage, etc.).
- **Histogrammes** et graphes pour visualiser :
  - La consommation horaire de la journée.
  - Les tendances hebdomadaires et mensuelles.

### Capteurs
- Liste des capteurs connectés (chaudière, jardin, cuisine, salle de bain…).
- Pour chaque capteur :
  - Nom et localisation.
  - **Volume actuel** (ex : `Volume : 15L`).
  - **Dernière mise à jour** du volume (`Mise à jour : 15:30`, `il y a 2 min`, etc.).
  - **Statut visuel** :
    - Cercle **vert** : actif.
    - Cercle **jaune** : avertissement.
    - Cercle **rouge** : erreur.
  - Icône d’alerte visible pour les états *avertissement* ou *erreur*.

### Navigation
- **BottomNavigationView** avec 3 onglets :
  - Accueil
  - Rapports
  - Capteurs
- **Menu latéral (Navigation Drawer)** ouvert par le bouton menu dans la top bar :
  - Accueil
  - Rapports
  - Capteurs
  - Paramètres (placeholder pour de futurs écrans)
- Gestion du bouton **Retour** :
  - Si le drawer est ouvert, il se ferme.
  - Sinon, navigation arrière normale entre les fragments.

---

## 🏗️ Architecture & Tech

- **Langage** : Kotlin
- **UI** : XML + Fragments
- **Navigation** :
  - `FrameLayout` pour les fragments
  - `BottomNavigationView`
  - `DrawerLayout` + `NavigationView`
- **Structure** :
  - `model/` : modèles (Sensor, SensorStatus, HourlyUsage, DayUsage…)
  - `data/` : services simulés (`FakeSensorApiService`, `FakeUsageRepository`…)
  - `ui/` : fragments (`HomeFragment`, `ReportsFragment`, `SensorsFragment`) et adapters (`SensorsAdapter`).

Les données sont pour l’instant **simulées** (fake services) mais la structure est prête à être connectée à une vraie API.

---

## ▶️ Lancer le projet

1. Cloner le dépôt :
```bash
git clone https://github.com/brendaolivr/AquaFlow.git
cd AquaFlow
````
2. Ouvrir le dossier dans Android Studio.
3. Laisser Gradle synchroniser le projet et télécharger les dépendances.
4. Lancer l’application sur un émulateur ou un appareil Android (bouton ▶️ dans Android Studio).
