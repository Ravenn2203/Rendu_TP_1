package org.example;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodDeclarationVisitor extends ASTVisitor {
    List<MethodDeclaration> methods = new ArrayList<>();

    public boolean visit(MethodDeclaration node) {
        methods.add(node);
        return super.visit(node);
    }

    public List<MethodDeclaration> getMethods() {
        return methods;
    }

    public int getNbMethodes(){
        return methods.size();
    }

    public int nbMoyenLigneCodeParMethode(CompilationUnit parse){
        int nbTotalLigneCodeDansMethodes = 0;
        int debutMethode = 0;
        int finMethode = 0;
        int longueurMethode = 0;
        for(MethodDeclaration method : methods){
            debutMethode = parse.getLineNumber(method.getStartPosition());
            finMethode = parse.getLineNumber(method.getStartPosition() + method.getLength());
            longueurMethode = finMethode - debutMethode + 1;
            nbTotalLigneCodeDansMethodes += longueurMethode;
            System.out.println(longueurMethode);

        }
        return nbTotalLigneCodeDansMethodes;

    }

    public int getMaxParametresDeMethode(){

        int max = 0;

        for(MethodDeclaration methode : methods){
            if(methode.parameters().size() >= max){
                max = methode.parameters().size();
            }
        }
        return max;
    }

}