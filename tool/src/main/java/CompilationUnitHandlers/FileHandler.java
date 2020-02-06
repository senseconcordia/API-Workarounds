package CompilationUnitHandlers;


import java.io.*;

import java.util.*;

public class FileHandler {


    private List<String> filesList;

    public FileHandler(File rootFolder, CompilationUnitRequest.ProcessType processType) {
        /*
        String projectRoot = rootFolder.getPath();
        this.parser = buildAstParser(rootFolder);
        this.filesList = getFilesFromFolder(projectRoot);

        int totalFiles = filesList.size();

        String[] javaFiles = filesList.toArray(new String[0]);

        FileASTRequestor fileASTRequestor = new FileASTRequestor() {
            @Override
            public void acceptAST(String sourceFilePath, CompilationUnit ast) {
                filesList.remove(sourceFilePath);
                printProgress(totalFiles - filesList.size(), totalFiles);

                String test = sourceFilePath;
                CompilationUnitRequest request = new CompilationUnitRequest(ast, processType, test);
                request.processCompilationUnit();
            }
        };
        try {

        } catch (UnsupportedOperationException e){
            System.out.println(e.getMessage());
        }
        */
    }


/*
    public static void confirmExamples(File exampleSourceFolder){
        LinkedList<File> unconfirmedExampleFiles = getFilesFromFolder(exampleSourceFolder);

        for (File unconfirmedExampleFile : unconfirmedExampleFiles){
            try {
                CompilationUnit unit = parseAstFromSourceFile(unconfirmedExampleFile);
                CompilationUnitRequest request = new CompilationUnitRequest(unit, CompilationUnitRequest.ProcessType.API_USES, unconfirmedExampleFile.getAbsolutePath());
                request.processCompilationUnit();
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
        }

    }


    public static List<String> getFilesFromFolder(String folder){
        List<String> projectFiles = new ArrayList<>();

        File dir = new File(folder);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null){
            for(File child: directoryListing){
                if(child.isDirectory()){
                    projectFiles.addAll(getFilesFromFolder(child.getAbsolutePath()));
                }
                else if(child.getAbsolutePath().endsWith(".java")){
                    if (!child.getAbsolutePath().contains("apct-tests") &&
                            !child.getAbsolutePath().contains("legacy-test") &&
                            !child.getAbsolutePath().contains("tests")) {
                        projectFiles.add(child.getAbsolutePath());
                    }
                }
            }
        }

        return projectFiles;

    }

    public static LinkedList<File> getFilesFromFolder(File folder){
        LinkedList<File> projectFiles = new LinkedList<>();

        File[] directoryListing = folder.listFiles();
        if (directoryListing != null){
            for(File child: directoryListing){
                if(child.isDirectory()){
                    projectFiles.addAll(getFilesFromFolder(child));
                }
                else if(child.getAbsolutePath().endsWith(".java")){
                    projectFiles.add(child);
                }
            }
        }

        return projectFiles;

    }

    public static HashSet<File> getSubFolders(File folder){
        HashSet<File> subFolders = new HashSet<>();

        File[] directoryListing = folder.listFiles();
        if (directoryListing != null){
            for (File child : directoryListing){
                if (child.isDirectory()){
                    subFolders.add(child);
                }
            }
        }
        return subFolders;
    }

    private static ASTParser buildAstParser(File srcFolder) {

        ASTParser parser = ASTParser.newParser(AST.JLS9);
        parser.setResolveBindings(true);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setBindingsRecovery(true);

        Map options = JavaCore.getOptions();
        parser.setCompilerOptions(options);

        String unitName = srcFolder.getName();
        parser.setUnitName(unitName);

        parser.setEnvironment(new String[0], new String[]{srcFolder.getPath()}, null, true);
        return parser;
    }

    public static CompilationUnit parseAstFromString(String srcString){
        ASTParser parser = ASTParser.newParser(AST.JLS9);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(srcString.toCharArray());
        parser.setResolveBindings(true);
        return (CompilationUnit) parser.createAST(null); // parse
    }

    public static CompilationUnit parseAstFromSourceFile(File srcFile) throws IOException{

        ASTParser parser = ASTParser.newParser(AST.JLS9);
        parser.setResolveBindings(true);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setBindingsRecovery(true);

        Map options = JavaCore.getOptions();
        parser.setCompilerOptions(options);

        String unitName = srcFile.getName();
        parser.setUnitName(unitName);

        String[] sources = {srcFile.getParent()};
        //TODO make a config file to set this up
        String[] classpath = {};

        parser.setEnvironment(classpath,sources,new String[]{ "UTF-8"}, true);
        parser.setSource(fileToCharArray(srcFile));

        return (CompilationUnit) parser.createAST(null); // parse
    }

    public static char[] fileToCharArray(File srcFile) throws IOException{
        String source = FileUtils.readFileToString(srcFile);
        return source.toCharArray();
    }


    public static void writeToDoneFile(String stringToWrite){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(Paths.get("done.txt").toFile(), true));
            writer.write(stringToWrite);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            System.out.println("couldn't write to done file, things might be redone if program fails");
        }
    }

    public static boolean stringInFile(String stringToFind){

        String line;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("done.txt"));
            try {
                while((line = reader.readLine()) != null){
                    if (line.contains(stringToFind)){
                        return true;
                    }
                }
            }catch (IOException e){
                System.out.println(e.getMessage());
            }

        }catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        }

        return false;
    }


    private static void printProgress(int currentItemsCompleted, int totalItems){
        int progress = (currentItemsCompleted*100/totalItems);
        int scale = totalItems/100;

        if(scale > 0 && (currentItemsCompleted % scale) == 0){

            System.out.printf("Currently %d%% done \n", progress);
        }
    }
*/
}
