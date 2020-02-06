package CompilationUnitHandlers;

public class CompilationUnitRequest {

    public enum ProcessType {API_BUILDER, API_USES, API_USAGE, CFG_MAPPING}


    //private CompilationUnit unit;
    private ProcessType processType;
    //private LinkedList<MethodDeclaration> apiMethods = new LinkedList<>();
    private String sourceFilePath;

/*
    public CompilationUnitRequest(CompilationUnit unit, ProcessType processType, String sourceFilePath){
        this.unit = unit;
        this.processType = processType;
        this.sourceFilePath = sourceFilePath;
    }*/

    protected void processCompilationUnit(){
        switch (this.processType){
            case API_BUILDER:
                break;
            case API_USES:
        }
    }
}
