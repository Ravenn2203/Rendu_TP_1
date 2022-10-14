package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class TypeDeclarationVisitor extends ASTVisitor {
    List<TypeDeclaration> types = new ArrayList<>();

    public boolean visit(TypeDeclaration node) {
        types.add(node);
        return super.visit(node);
    }

    public List<TypeDeclaration> getTypes() {
        return types;
    }

    public int getNbClassesEtInterfaces() {
        return types.size();
    }

    public float nbMoyenMethodesParClasse() {
        float nbMethodes = 0;
        //Pour chaque classe (noeud classe) récupérée par le visiteur dans l'AST
        //J'accède aux méthodes puis je demande la longueur de ce tableau renvoyé (tableau de MethodDeclaration)
        for (TypeDeclaration classe : types) {
            nbMethodes += classe.getMethods().length;
        }
        return nbMethodes / types.size();
    }

    public float nbMoyenAttributsParClasse() {
        float nbAttributs = 0;
        //Pour chaque classe (noeud classe) récupérée par le visiteur dans l'AST
        //J'accède aux attributs puis je demande la longueur de ce tableau renvoyé (tableau de TypeDeclaration)
        for (TypeDeclaration classe : types) {
            nbAttributs += classe.getFields().length;
        }
        return nbAttributs / types.size();
    }

    public ArrayList<TypeDeclaration> DixPourcentsClassesMaxMethodes() {

        int DixPourcentsClasses = (int) (types.size() * 0.1 + 1);
        System.out.println("(10% de " + types.size() + " classes c'est " + DixPourcentsClasses + " classe(s), si deux classes ont le même nombre de méthodes et qu'une des deux classes est dans les 10 % alors on prend les deux)\n");
        //Ma liste à renvoyer
        ArrayList<TypeDeclaration> listeClasses = new ArrayList<>();
        int MoinsQue = 0;

        //Tant que j'ai pas taille = 5 (si 5 c'est 10% des classes de l'application), je continue d'ajouter
        for (TypeDeclaration classe : types) {
            MoinsQue = 0;
            //Je reparcours toutes mes classes
            for (TypeDeclaration test : types) {
                if (classe.getName() != test.getName()) {
                    //Si je mets <=, les classes de même taille ne pourront pas être choisies
                    if (classe.getMethods().length < test.getMethods().length) {
                        MoinsQue++;
                    }
                }
            }
            //Si la classe courante fait partie des 10%, je l'ajoute
            if (MoinsQue < DixPourcentsClasses) {
                listeClasses.add(classe);
            }
        }


        return listeClasses;
    }

    public ArrayList<TypeDeclaration> DixPourcentsClassesMaxAttributs() {

        int DixPourcentsClasses = (int) (types.size() * 0.1 + 2);
        System.out.println("(10% de " + types.size() + " classes c'est " + DixPourcentsClasses + " classe(s), si deux classes ont le même nombre d'attributs et qu'une des deux classes est dans les 10 % alors on prend les deux)\n");
        //Ma liste à renvoyer
        ArrayList<TypeDeclaration> listeClasses = new ArrayList<>();
        int MoinsQue = 0;

        //Tant que j'ai pas taille = 5 (si 5 c'est 10% des classes de l'application), je continue d'ajouter
        for (TypeDeclaration classe : types) {
            MoinsQue = 0;
            //Je reparcours toutes mes classes
            for (TypeDeclaration test : types) {
                if (classe.getName() != test.getName()) {
                    //Si je mets <=, les classes de même taille ne pourront pas être choisies
                    if (classe.getFields().length < test.getFields().length) {
                        MoinsQue++;
                    }
                }
            }
            //Si la classe courante fait partie des 10%, je l'ajoute
            if (MoinsQue < DixPourcentsClasses) {
                listeClasses.add(classe);
            }
        }


        return listeClasses;
    }

    public ArrayList<TypeDeclaration> DixPourcentsClassesMaxMethodesEtAttributs() {
        //Ma liste à renvoyer
        ArrayList<TypeDeclaration> listeClasses = new ArrayList<>();
        ArrayList<TypeDeclaration> listeClasses2 = DixPourcentsClassesMaxMethodes();
        ArrayList<TypeDeclaration> listeClasses3 = DixPourcentsClassesMaxAttributs();

        for (TypeDeclaration classe : listeClasses2) {
            if (listeClasses3.contains(classe)) {
                listeClasses.add(classe);
            }
        }


        return listeClasses;
    }

    public ArrayList<TypeDeclaration> PlusDeXMethodes(int X) {
        //Ma liste à renvoyer
        ArrayList<TypeDeclaration> listeClasses = new ArrayList<>();

        for (TypeDeclaration classe : types) {
            if (classe.getMethods().length > X) {
                listeClasses.add(classe);
            }
        }
        return listeClasses;
    }

}
