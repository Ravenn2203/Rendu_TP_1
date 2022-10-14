package org.example;

import java.util.Scanner;

public class InterfaceUtilisateur {

    public void interfaceUtilisateur(){}

    private int choixExercice;
    private int choixAnalyse= -1;

    public void printCLI(Scanner scanner){
        System.out.println("Bienvenue dans le TP1 de Léa Garcia !");
        System.out.println("Quel exercice voulez vous exécuter ?");
        System.out.println("\nTapez 1 pour l'exercice 1");
        System.out.println("Tapez 2 pour l'exercice 2");
        scanner = new Scanner(System.in);
        choixExercice = scanner.nextInt();

        while(choixExercice!=1 && choixExercice !=2){
            System.out.println("Veuillez uniquement choisir entre 1 ou 2");
            choixExercice = scanner.nextInt();
        }

        if (choixExercice == 1) {
            printEnonceExercice1(scanner);
        }else {
            System.out.println("Nous allons construire le graphe d'appel du code analysé pour vous !\n");
        }
    }

    public int getChoixExercice() {
        return choixExercice;
    }

    public void setChoixExercice(int choixExercice) {
        this.choixExercice = choixExercice;
    }

    public int getChoixAnalyse() {
        return choixAnalyse;
    }

    public void setChoixAnalyse(int choixAnalyse) {
        this.choixAnalyse = choixAnalyse;
    }

    public void printEnonceExercice1(Scanner scanner){

        System.out.println("\nMerci de choisir l'analyse que vous souhaitez réaliser parmi les choix suivants :");
        System.out.println("Si vous voulez quitter, choisissez le choix 14");

        System.out.println("Choix 1 : Le nombre de classe");
        System.out.println("Choix 2 : Le nombre de lignes de code");
        System.out.println("Choix 3 : Le nombre total de méthodes");
        System.out.println("Choix 4 : Le nombre total de packages");
        System.out.println("Choix 5 : Le nombre moyen de méthodes par classe");
        System.out.println("Choix 6 : Le nombre moyen de lignes de code par méthode");
        System.out.println("Choix 7 : Le nombre moyen d'attributs par classe");
        System.out.println("Choix 8 : Les 10% des classes qui possèdent le plus grand nombre de méthodes");
        System.out.println("Choix 9 : Les 10% des classes qui possèdent le plus grand nombre d'attributs");
        System.out.println("Choix 10 : Les classes qui font partie des deux catégories précédentes (deux 10% de classes)");
        System.out.println("Choix 11 : Les classes qui possèdent plus de X méthodes et vous devrez nous donner X ensuite");
        System.out.println("Choix 12 : Les 10% des méthodes qui possèdent le plus grand nombre de lignes de code par classe");
        System.out.println("Choix 13 : Le nombre maximal de paramètres parmi ceux de toutes les méthodes");
        System.out.println("Choix 14 : Quitter\n");

        System.out.println("Entrez donc votre choix :");
        choixAnalyse = scanner.nextInt();

        while (choixAnalyse < 1 || choixAnalyse > 14) {
            System.out.println("Vous n'avez pas choisi un choix existant, recommencez s'il vous plaît :");
            choixAnalyse = scanner.nextInt();
        }
    }
}