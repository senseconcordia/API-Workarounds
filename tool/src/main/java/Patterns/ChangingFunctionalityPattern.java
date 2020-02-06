package Patterns;

import com.github.javaparser.ast.CompilationUnit;

public class ChangingFunctionalityPattern extends PatternInstance {

    public String classInvoked = null;
    public String methodInvoked = null;

    public ChangingFunctionalityPattern(CompilationUnit compilationUnit){
        super(compilationUnit,"ChangingFunctionalityPattern");
    }

    public String getClassInvoked() {
        return classInvoked;
    }

    public void setClassInvoked(String classInvoked) {
        this.classInvoked = classInvoked;
    }

    public String getMethodInvoked() {
        return methodInvoked;
    }

    public void setMethodInvoked(String methodInvoked) {
        this.methodInvoked = methodInvoked;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(this.getType());
        sb.append(": ");
        sb.append(this.getFile());

        if (methodInvoked != null){
            sb.append(", invoked method: ");
            sb.append(this.getMethodInvoked());
        } else{
            sb.append(", invoked class: ");
            sb.append(this.getClassInvoked());
        }
        return sb.toString();
    }
}
