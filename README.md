# Projet IDM : Stade Rennais Football Club - VidéoGenerator

## Description

Ce projet est le résultat de l'ensemble des TP et du projet, de la matière Igénierie des Modèles, du MASTER 2 MIAGE 17/18 à l'unviersité de Rennes 1.

L’objectif du projet est d’offrir une solution logicielle pour déployer un générateur ou configurateur Web de vidéos à partir d’une spécification textuelle.


## Architecture du projet

### Générateur

Le générateur, écrit en Java et Xtend, qui, à partir d'une spécification VideoGen.XTEXT aura généré un méta-model .ecore, pourra reconnaître des fichiers spécifiés
.videogen et effectuer des traitements afin de générer différentes ressources : 
* Un ensemble de vidéos, correspondant à l'ensemble des variantes de vidéo (concaténé, filtré, pobabilisé), possiblement générable à partir de la spécification videogen,
* Les gifs, correspondants à ces variantes,
* Des vignettes correspondant aux vidéo "unitaires" disponibles,
* Des fichiers .csv permettant d'analyser l'ensemble des variantes (analyse sur la durée et la taille)

### Serveur

Le serveur, correspond à une application basée sur Node.js, et permettant grâce au framework Express.js de déployer un serveur pouvant recevoir des requêtes GET/POST, il pourra ainsi :
* Appeller une execution du generateur, précédemment décris, grâce au module Node.js "child_process",
* Délivrer des fichiers statiques, précédemment générés.

### Client

Le client, ici créé avec le framework Angular v5, pourra permettre :
* D'afficher l'ensemble des vignettes présentes dans la spécification videogen tournant dans le serveur, 
* De générer et d'afficher une vidéo aléatiore à partir de la spécification videogen tournant dans le serveur, 
* De générer et de récupérer cette vidéo sous format .gif

## Procédure de déploiement

### Générateur

Le générateur est executable, soit en générant un .jar et en déployant celui-ci sur un serveur (comme effctué pour l'application Node.js Server),
soit directement dans le projet Java "VideoGenToolSuite" : 
* En executant la classe Main.java avec en argument :
    * fichier : un nom de fichier .videogen (example: example1.videogen)
    * action : 
        * "init" pour initialiser et générer les vignettes
        * "generate" pour généner une variante de vidéo dans le dossier ./ressources/gen/videos
* En executant les cas de test JUnit, présents dans la classe VideoGenText.xtend, qui à partir d'un ensemble de spécification videogen, générera et 
mettera à contribution l'ensemble des fonctionnalités créées dans ce générateur.

### Serveur

Le serveur est déployable en local  via  : 
* La commande 'npm install', dans le répertoire './NodeApp'
* La commande 'node App.js', dans le répertoire './NodeApp/app'

### Client

Le serveur est déployable en local  via  : 
* La commande 'npm install', dans le répertoire './VideoGeneratorFront'
* La commande 'ng serve', dans le répertoire './VideoGeneratorFront'

Et accessible à l'url "http://localhost:4200/"


## Concours

La vidéo ./Concours/static_generated.mkv a été généré à partir de la spécification "./Councours_Screencast/total2.videogen".

## ScreenCast

Un screencast démo du site web est disponible dans le répertoire "./Councours_Screencast/Screencast" :
* vignettes.png  --> écran d'accueil avec l'ensemble des vignettes générées et récupérées dans le serveur
* express.png --> fonctionnalité de récupération express (fichier non générée) de la vidéo présentée pour le concours
* rapide.png --> fonctionnalité de récupération d'une vidéo générée
* full_feature.png --> fonctionnalité de récupération d'une vidéo full feature générée (assez long lors de l'execution dû aux nombreux appels ffmpeg)
* gif.png --> fonctionnalité de la récupération du gif généré 

## Fonctionnalité d'analyse

A partir des fichiers CSV, il a été possible d'analyser la corrélation entre la taille rééle et la taille attendue d'une variante, qui se trouvent fortemment corrélées
(cf: "./Councours_Screencast/correlation.png")
   