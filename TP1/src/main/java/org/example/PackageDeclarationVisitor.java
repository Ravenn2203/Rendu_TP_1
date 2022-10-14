package org.example;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class PackageDeclarationVisitor extends ASTVisitor {
    List<PackageDeclaration> packages = new ArrayList<>();

    public boolean visit(PackageDeclaration node) {
        packages.add(node);
        return super.visit(node);
    }

    public List<PackageDeclaration> getTypes() {
        return packages;
    }

    public int getNbPackages(){
        return packages.size();
    }


}
