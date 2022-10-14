package org.example;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;


import static java.lang.System.exit;
import static java.lang.System.setOut;

public class Parser {

    //TODO factoriser code switch
    //TODO peut-être mettre les affichages directement dans les méthodes des visiteurs (surtout pour question 12)
    //TODO finir les questions où il y a des comptages de lignes de code j'y arrive pas

    public static final String projectPath = "/home/garcialea/Bureau/TP_RENDU/TP1/";
    public static final String projectSourcePath = projectPath + "/src";
    public static final String jrePath = "/usr/lib/jvm/java-1.11.0-openjdk-amd64";

    public static InterfaceUtilisateur interfaceUtilisateur = new InterfaceUtilisateur();

    public static void main(String[] args) throws IOException {

        //Mets dans une ArrayList javaFiles, l'ensemble des fichiers java d'un dossier
        final File folder = new File(projectSourcePath);
        ArrayList<File> javaFiles = listJavaFilesForFolder(folder);

        //CLI Utilisateur pour proposer l'exercice et le choix d'analyse dans l'exercice 1
        Scanner scanner = new Scanner(System.in);
        interfaceUtilisateur.printCLI(scanner);
        System.out.println(interfaceUtilisateur.getChoixAnalyse());

        if(interfaceUtilisateur.getChoixExercice() == 1){
            System.out.println("\n");
            System.out.println("----------------------------------------------------------------");
            System.out.println("Exercice 1. Vous avez choisi l'analyse numéro : " + interfaceUtilisateur.getChoixAnalyse());
            System.out.println("\n");
            choixAnalyse(javaFiles, interfaceUtilisateur.getChoixAnalyse());
            System.out.println("----------------------------------------------------------------");
            while(interfaceUtilisateur.getChoixAnalyse() != 14) {
                int choixSuite = 0;
                while (choixSuite != 1 && choixSuite != 2) {
                    System.out.println("\nTapez 1 pour faire une autre analyse");
                    System.out.println("Tapez 2 pour quitter l'application\n");
                    choixSuite = scanner.nextInt();
                    if(choixSuite == 1){

                        interfaceUtilisateur.printEnonceExercice1(scanner);
                        System.out.println("\n");
                        System.out.println("----------------------------------------------------------------");
                        System.out.println("Exercice 1. Vous avez choisi l'analyse numéro : " + interfaceUtilisateur.getChoixAnalyse());
                        System.out.println("\n");
                        choixAnalyse(javaFiles, interfaceUtilisateur.getChoixAnalyse());
                        System.out.println("----------------------------------------------------------------");
                        System.out.println("\n");

                    }else if(choixSuite == 2){

                        exit(1);

                    }
                }

            }

        }else{
            TypeDeclarationVisitor visiteurClasse = new TypeDeclarationVisitor();
            CompilationUnit parse = null;
            //Déclaration du graphe de la librairie JGrapht
            Graph<String, DefaultEdge> graphe = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

            //Lecture puis parsage des fichiers
            for (File fileEntry : javaFiles) {
                String content = FileUtils.readFileToString(fileEntry);
                parse = parse(content.toCharArray());
                parse.accept(visiteurClasse);
            }
            for (TypeDeclaration type : visiteurClasse.getTypes()) {

                for (MethodDeclaration methode : type.getMethods()) {

                    graphe.addVertex(methode.getName().toString());
                    MethodInvocationVisitor visteurMethodInvocation = new MethodInvocationVisitor();
                    parse.accept(visteurMethodInvocation);

                    if (visteurMethodInvocation.getMethods().size() != 0) {

                        for (MethodInvocation methodeInv : visteurMethodInvocation.getMethods()) {

                            graphe.addVertex(methodeInv.getName().toString());
                            graphe.addEdge(methode.getName().toString(), methodeInv.getName().toString());
                        }
                    }
                }
            }
            DOTExporter<String, DefaultEdge> exporter = new DOTExporter<String, DefaultEdge>();
            exporter.setVertexAttributeProvider((v) -> {
                Map<String, Attribute> map = new LinkedHashMap<String, Attribute>();
                map.put("label", DefaultAttribute.createAttribute(v.toString()));
                return map;
            });
            Writer writer = new StringWriter();
            exporter.exportGraph(graphe, writer);
            MutableGraph g = new guru.nidi.graphviz.parse.Parser().read(writer.toString());
            Graphviz.fromGraph(g).height(1000).render(Format.PNG).toFile(new File("example/callGraph.png"));
        }
    }

    public static void choixAnalyse(ArrayList<File> javaFiles, int choix) throws IOException {

        //Je récupère mes visiteurs
        TypeDeclarationVisitor visiteurClassesEtInterfaces = new TypeDeclarationVisitor();
        MethodDeclarationVisitor visiteurDeclarationMethodes= new MethodDeclarationVisitor();
        PackageDeclarationVisitor visiteurPackages = new PackageDeclarationVisitor();

        switch (interfaceUtilisateur.getChoixAnalyse()) {
            case 1: {
                //Nombre de classes de l'application
                for (File fileEntry : javaFiles) {
                    //Pour chaque fichier java je récupère le contenu
                    String content = FileUtils.readFileToString(fileEntry);
                    //J'applique un extracteur de modèle, un parseur et je récupère l'AST du fichier
                    CompilationUnit parse = parse(content.toCharArray());
                    //J'applique un extracteur de propriétés, un visiteur qui va extraire les propriétés des noeuds de l'AST
                    //Je fais ensuite les calculs nécessaires pour obtenir les connaissances dont j'ai besoin
                    parse.accept(visiteurClassesEtInterfaces);
                }
                System.out.println("Nombre de classes de l'application : " + visiteurClassesEtInterfaces.getNbClassesEtInterfaces());
                break;
            }

            case 2: {
                //Nombre de lignes de code de l'application
                int nbLigneDeCode = 0;
                for (File fileEntry : javaFiles) {
                    //Pour chaque fichier java je récupère le contenu
                    String content = FileUtils.readFileToString(fileEntry);
                    //J'applique un extracteur de modèle, un parseur et je récupère l'AST du fichier (ici noeuds classes)
                    CompilationUnit parse = parse(content.toCharArray());
                    //Pour attraper la dernière ligne, son numéro c'est la longueur -1 sinon valeur égale au (nombre de classe * (-1))
                    //Pas besoin de visiteur ici, je récupère juste la taille du fichier parsé
                    nbLigneDeCode += parse.getLineNumber(parse.getLength() -1);

                }
                System.out.println("Nombre de lignes de code de l'application : "+nbLigneDeCode);
                break;
            }

            case 3: {
                //Nombre total de méthodes de l'application
                for (File fileEntry : javaFiles) {
                    //Pour chaque fichier java je récupère le contenu
                    String content = FileUtils.readFileToString(fileEntry);
                    //J'applique un extracteur de modèle, un parseur et je récupère l'AST du fichier
                    CompilationUnit parse = parse(content.toCharArray());
                    //J'applique un extracteur de propriétés, un visiteur qui va extraire les propriétés des noeuds de l'AST (ici noeuds méthodes)
                    //Je fais ensuite les calculs nécessaires pour obtenir les connaissances dont j'ai besoin
                    parse.accept(visiteurDeclarationMethodes);
                }
                System.out.println("Nombre total de méthodes de l'application : " + visiteurDeclarationMethodes.getNbMethodes());
                break;

            }

            case 4 : {
                //Nombre total de packages de l'application
                for (File fileEntry : javaFiles) {
                    //Pour chaque fichier java je récupère le contenu
                    String content = FileUtils.readFileToString(fileEntry);
                    //J'applique un extracteur de modèle, un parseur et je récupère l'AST du fichier
                    CompilationUnit parse = parse(content.toCharArray());
                    //J'applique un extracteur de propriétés, un visiteur qui va extraire les propriétés des noeuds de l'AST (ici noeuds packages)
                    //Je fais ensuite les calculs nécessaires pour obtenir les connaissances dont j'ai besoin
                    parse.accept(visiteurPackages);
                }
                System.out.println("Nombre total de packages de l'application : " + visiteurPackages.getNbPackages());
                break;
            }

            case 5 : {
                //Nombre moyen de méthodes par classe
                for (File fileEntry : javaFiles) {
                    //Pour chaque fichier java je récupère le contenu
                    String content = FileUtils.readFileToString(fileEntry);
                    //J'applique un extracteur de modèle, un parseur et je récupère l'AST du fichier
                    CompilationUnit parse = parse(content.toCharArray());
                    //J'applique un extracteur de propriétés, un visiteur qui va extraire les propriétés des noeuds de l'AST
                    //Je fais ensuite les calculs nécessaires pour obtenir les connaissances dont j'ai besoin
                    parse.accept(visiteurClassesEtInterfaces);
                }
                System.out.println("Nombre moyen de méthodes par classe (nombre exact trouvé): " + visiteurClassesEtInterfaces.nbMoyenMethodesParClasse());
                System.out.println("Nombre moyen de méthodes par classe (concrètement): " + (int) visiteurClassesEtInterfaces.nbMoyenMethodesParClasse());
                break;
            }

            case 6 : {
                //Nombre moyen de lignes de code par méthode
                CompilationUnit parse = null;
                int nbTotalLignesCode = 0;
                int nbMethods = 0;
                for (File fileEntry : javaFiles) {
                    //Pour chaque fichier java je récupère le contenu
                    String content = FileUtils.readFileToString(fileEntry);
                    //J'applique un extracteur de modèle, un parseur et je récupère l'AST du fichier
                    parse = parse(content.toCharArray());
                    //J'applique un extracteur de propriétés, un visiteur qui va extraire les propriétés des noeuds de l'AST
                    //Je fais ensuite les calculs nécessaires pour obtenir les connaissances dont j'ai besoin

                    //Je crée un nouveau visiteur à chaque fois pour éviter le fait que le visiteur (utilisé tout seul) rajoute, pour chaque fichier lu, l'ensemble des méthodes à la suite dans la variable methods
                    MethodDeclarationVisitor visiteurDeclarationMethodesNouveau = new MethodDeclarationVisitor();
                    parse.accept(visiteurDeclarationMethodesNouveau);
                    nbTotalLignesCode += visiteurDeclarationMethodesNouveau.nbMoyenLigneCodeParMethode(parse);
                    nbMethods += visiteurDeclarationMethodesNouveau.getNbMethodes();
                }
                    System.out.println("Nombre moyen de lignes de code par méthode (nombre exact trouvé) : "+nbTotalLignesCode/nbMethods);
                    System.out.println("Nombre moyen de lignes de code par méthode (concrètement) : "+ (int) nbTotalLignesCode/nbMethods);

                break;

            }

            case 7 : {
                //Nombre moyen d'attributs par classe
                for (File fileEntry : javaFiles) {
                    //Pour chaque fichier java je récupère le contenu
                    String content = FileUtils.readFileToString(fileEntry);
                    //J'applique un extracteur de modèle, un parseur et je récupère l'AST du fichier
                    CompilationUnit parse = parse(content.toCharArray());
                    //J'applique un extracteur de propriétés, un visiteur qui va extraire les propriétés des noeuds de l'AST
                    //Je fais ensuite les calculs nécessaires pour obtenir les connaissances dont j'ai besoin
                    parse.accept(visiteurClassesEtInterfaces);
                }
                System.out.println("Nombre moyen d'attributs par classe (nombre exact trouvé): " + visiteurClassesEtInterfaces.nbMoyenAttributsParClasse());
                System.out.println("Nombre moyen d'attributs par classe (concrètement): " + (int) visiteurClassesEtInterfaces.nbMoyenAttributsParClasse());
                break;
            }

            case 8 : {
                //Les 10% des classes qui possèdent le plus grand nombre de méthodes
                for (File fileEntry : javaFiles) {
                    //Pour chaque fichier java je récupère le contenu
                    String content = FileUtils.readFileToString(fileEntry);
                    //J'applique un extracteur de modèle, un parseur et je récupère l'AST du fichier
                    CompilationUnit parse = parse(content.toCharArray());
                    //J'applique un extracteur de propriétés, un visiteur qui va extraire les propriétés des noeuds de l'AST
                    //Je fais ensuite les calculs nécessaires pour obtenir les connaissances dont j'ai besoin
                    parse.accept(visiteurClassesEtInterfaces);
                }
                System.out.println("Voici les 10% des classes qui possèdent le plus grand nombre de méthodes :");
                ArrayList<TypeDeclaration> listeClasses = new ArrayList<>();
                listeClasses = visiteurClassesEtInterfaces.DixPourcentsClassesMaxMethodes();
                for (TypeDeclaration classeDixPourcent : listeClasses) {
                    System.out.println("-- La classe "+classeDixPourcent.getName());
                    System.out.println("   Dont le nombre de méthodes s'élève à : "+classeDixPourcent.getMethods().length);
                }
                break;
            }

            case 9 : {
                //Les 10% des classes qui possèdent le plus grand nombre d'attributs
                for (File fileEntry : javaFiles) {
                    //Pour chaque fichier java je récupère le contenu
                    String content = FileUtils.readFileToString(fileEntry);
                    //J'applique un extracteur de modèle, un parseur et je récupère l'AST du fichier
                    CompilationUnit parse = parse(content.toCharArray());
                    //J'applique un extracteur de propriétés, un visiteur qui va extraire les propriétés des noeuds de l'AST
                    //Je fais ensuite les calculs nécessaires pour obtenir les connaissances dont j'ai besoin
                    parse.accept(visiteurClassesEtInterfaces);
                }
                System.out.println("Voici les 10% des classes qui possèdent le plus grand nombre d'attributs :");
                ArrayList<TypeDeclaration> listeClasses = new ArrayList<>();
                listeClasses = visiteurClassesEtInterfaces.DixPourcentsClassesMaxAttributs();
                for (TypeDeclaration classeDixPourcent : listeClasses) {
                    System.out.println("La classe "+classeDixPourcent.getName());
                    System.out.println("Dont le nombre d'attributs s'élève à : "+classeDixPourcent.getFields().length);
                }
                break;
            }

            case 10 : {
                //Les classes qui font partie en même temps des deux catégories précédentes
                for (File fileEntry : javaFiles) {
                    //Pour chaque fichier java je récupère le contenu
                    String content = FileUtils.readFileToString(fileEntry);
                    //J'applique un extracteur de modèle, un parseur et je récupère l'AST du fichier
                    CompilationUnit parse = parse(content.toCharArray());
                    //J'applique un extracteur de propriétés, un visiteur qui va extraire les propriétés des noeuds de l'AST
                    //Je fais ensuite les calculs nécessaires pour obtenir les connaissances dont j'ai besoin
                    parse.accept(visiteurClassesEtInterfaces);
                }
                System.out.println("Voici les 10% des classes qui possèdent le plus grand nombre d'attributs :");
                ArrayList<TypeDeclaration> listeClasses = new ArrayList<>();
                listeClasses = visiteurClassesEtInterfaces.DixPourcentsClassesMaxMethodesEtAttributs();
                if(listeClasses.size() == 0){
                    System.out.println("\nAucune des classes ne fait partie des deux catégories, essayez peut-être de tester sur un plus gros projet (10% des classes c'est 1 classe s'il y en a moins de 10");

                }
                for (TypeDeclaration classeDixPourcent : listeClasses) {
                    System.out.println("La classe "+classeDixPourcent.getName());
                    System.out.println("Dont le nombre d'attributs s'élève à : "+classeDixPourcent.getFields().length);
                }
                break;
            }

            case 11 : {
                //Les classes qui possèdent plus de X méthodes
                System.out.println("Entrez le nombre de méthodes minimum que nous allons chercher s'il vous plaît");
                Scanner scanner = new Scanner(System.in);
                int nombreMethodes = scanner.nextInt();
                for (File fileEntry : javaFiles) {
                    //Pour chaque fichier java je récupère le contenu
                    String content = FileUtils.readFileToString(fileEntry);
                    //J'applique un extracteur de modèle, un parseur et je récupère l'AST du fichier
                    CompilationUnit parse = parse(content.toCharArray());
                    //J'applique un extracteur de propriétés, un visiteur qui va extraire les propriétés des noeuds de l'AST
                    //Je fais ensuite les calculs nécessaires pour obtenir les connaissances dont j'ai besoin
                    parse.accept(visiteurClassesEtInterfaces);
                }
                System.out.println("Voici les classes qui possèdent plus de "+nombreMethodes+" méthodes :\n");
                ArrayList<TypeDeclaration> listeClasses = new ArrayList<>();
                listeClasses = visiteurClassesEtInterfaces.PlusDeXMethodes(nombreMethodes);
                for (TypeDeclaration classePlusDeXMethodes : listeClasses) {
                    System.out.println("La classe "+classePlusDeXMethodes.getName());
                    System.out.println("Dont le nombre de méthodes s'élève à : "+classePlusDeXMethodes.getMethods().length);
                }
                break;
            }

            case 12 : {
                //Les 10% des méthodes qui possèdent le plus grand nombre de lignes de code (par classe)
                CompilationUnit parse = null;
                System.out.println("Pour chaque classe, voici les 10% des méthodes qui possèdent le plus grand nombre de lignes de code :");
                for (File fileEntry : javaFiles) {
                    //Pour chaque fichier java je récupère le contenu
                    String content = FileUtils.readFileToString(fileEntry);
                    //J'applique un extracteur de modèle, un parseur et je récupère l'AST du fichier
                    parse = parse(content.toCharArray());
                    //J'applique un extracteur de propriétés, un visiteur qui va extraire les propriétés des noeuds de l'AST
                    //Je fais ensuite les calculs nécessaires pour obtenir les connaissances dont j'ai besoin
                    TypeDeclarationVisitor visiteurClassesEtInterfacesNouveau = new TypeDeclarationVisitor();
                    parse.accept(visiteurClassesEtInterfacesNouveau);

                    List<TypeDeclaration> listeClasses = new ArrayList<>();
                    //La liste listeClasses contient toutes les classes de mon application
                    listeClasses = visiteurClassesEtInterfacesNouveau.getTypes();

                    for (TypeDeclaration classe : listeClasses) {
                        //J'affiche la classe choisie dans la liste
                        System.out.println("\n-- La classe " + classe.getName());
                        //Je récupère l'ensemble de ses méthodes
                        MethodDeclaration[] methods = classe.getMethods();
                        //Je définis combien représente 10% de méthodes de cette classe
                        int DixPourcentsMethods = (int) (classe.getMethods().length * 0.1 + 1);
                        //System.out.println("(10% de " + classe.getMethods().length + " méthodes c'est " + DixPourcentsMethods + " méthodes(s)");
                        int MoinsQue = 0;
                        int longueurMethode1 = 0;
                        int longueurMethode2 = 0;
                        for (MethodDeclaration methode : methods) {
                            MoinsQue = 0;
                            //Je reparcours toutes mes méthodes
                            for (MethodDeclaration methodeComparaison : methods) {
                                if (methode.getName() != methodeComparaison.getName()) {
                                    longueurMethode1 = parse.getLineNumber(methode.getStartPosition() + methode.getLength()) - parse.getLineNumber(methode.getStartPosition()) + 1;
                                    longueurMethode2 = parse.getLineNumber(methodeComparaison.getStartPosition() + methodeComparaison.getLength()) - parse.getLineNumber(methodeComparaison.getStartPosition()) + 1;
                                    //System.out.println(longueurMethode1);
                                    //System.out.println(longueurMethode2);

                                    if (longueurMethode1 < longueurMethode2) {
                                        MoinsQue++;
                                    }
                                }
                            }
                            //Si la méthode courante fait partie des 10%, je l'ajoute
                            if (MoinsQue < DixPourcentsMethods) {
                                System.out.println("    -- La méthode : " + methode.getName().toString() + " qui a : " + longueurMethode1 + " lignes de code");
                            }


                        }
                    }
                }
                break;

            }

            case 13 : {
                //Le nombre maximal de paramètres par rapport à toutes les méthodes
                for (File fileEntry : javaFiles) {
                    //Pour chaque fichier java je récupère le contenu
                    String content = FileUtils.readFileToString(fileEntry);
                    //J'applique un extracteur de modèle, un parseur et je récupère l'AST du fichier
                    CompilationUnit parse = parse(content.toCharArray());
                    //J'applique un extracteur de propriétés, un visiteur qui va extraire les propriétés des noeuds de l'AST (ici noeuds méthodes)
                    //Je fais ensuite les calculs nécessaires pour obtenir les connaissances dont j'ai besoin
                    parse.accept(visiteurDeclarationMethodes);
                }
                System.out.println("Le nombre maximal de paramètres par rapport à toutes les méthodes de l'application: " + visiteurDeclarationMethodes.getMaxParametresDeMethode());
                break;
            }

            case 14: break;
            }
    }

    //Permet de lire tous les fichiers java contenu dans le dossier (folder)
    public static ArrayList<File> listJavaFilesForFolder(final File folder) {
        ArrayList<File> javaFiles = new ArrayList<File>();
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                javaFiles.addAll(listJavaFilesForFolder(fileEntry));
            } else if (fileEntry.getName().contains(".java")) {
                javaFiles.add(fileEntry);
            }
        }
        return javaFiles;
    }

    //Crée notre AST
    private static CompilationUnit parse(char[] classSource) {

        ASTParser parser = ASTParser.newParser(AST.JLS4); // java +1.6
        parser.setResolveBindings(true);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setBindingsRecovery(true);

        Map options = JavaCore.getOptions();
        parser.setCompilerOptions(options);
        parser.setUnitName("");

        String[] sources = { projectSourcePath };
        String[] classpath = {jrePath};

        parser.setEnvironment(classpath, sources, new String[] { "UTF-8"}, true);
        parser.setSource(classSource);

        return (CompilationUnit) parser.createAST(null);
    }
}
