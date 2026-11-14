# Sporyx

Jeu idle mobile sous Godot 4.4 : survivre dans un monde fongique où les mutations sont partout. Projet archivé.

Scènes principales : `Main`, `Explore`, `Arena`, `Inventory`. Le script `arena.gd` gère les combats.

## Lancer

Ouvrir le dossier dans Godot 4.4 et lancer la scène principale (`Main.tscn`).

## Serveur Java

Le dossier `server/` contient un petit serveur HTTP (port 8080) pour les combats et l'exploration. Les fichiers `.class` ne sont plus versionnés, il faut compiler :

```bash
mkdir -p bin && javac -d bin server/*.java
java -cp bin HttpServer
```
