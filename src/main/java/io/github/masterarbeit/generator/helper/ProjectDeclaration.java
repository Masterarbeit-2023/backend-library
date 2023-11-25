package io.github.masterarbeit.generator.helper;

import io.github.masterarbeit.generator.helper.method.MethodDeclaration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDeclaration {
    String name;
    List<ClassDeclaration> classDeclarations = new ArrayList<>();
    List<OtherClass> otherClasses = new ArrayList<>();
    Model pom;
    List<Pair<String, String>> properties = new ArrayList<>();

    public void addClassDeclaration(ClassDeclaration classDeclaration) {
        classDeclarations.add(classDeclaration);
    }

    public void addOtherClass(OtherClass otherClass) {
        otherClasses.add(otherClass);
    }

    public Pair<RequestType, Annotation> getRequestTypeAndAnnotationByMethodName(String functionName) {
        for (ClassDeclaration clazz : classDeclarations) {
            for (MethodDeclaration method : clazz.getMethods()) {
                if (functionName.toLowerCase().equals(method.getName())) {
                    return method.getRequestTypeAndAnnotation();
                }
            }
        }
        return null;
    }

    public ClassDeclaration getClassDeclarationsByName(String s) {
        for (ClassDeclaration clazz : classDeclarations) {
            if (clazz.getName().equals(s)) {
                return clazz;
            }
        }
        return null;
    }

    public OtherClass getOtherClassByName(String name) {
        for (OtherClass otherClass : otherClasses) {
            if (otherClass.getClassName().equals(name)) {
                return otherClass;
            }
        }
        return null;
    }

    public ProjectDeclaration combine(ProjectDeclaration project) {
        ProjectDeclaration newProject = new ProjectDeclaration();

        newProject.setPom(combinePom(project.pom));

        newProject.setName(name);
        List<ClassDeclaration> newClasses = combineClasses(project.classDeclarations);
        newProject.setClassDeclarations(newClasses);


        List<OtherClass> newOtherClasses = combineOtherClasses(project.otherClasses);
        newProject.setOtherClasses(newOtherClasses);

        return newProject;
    }

    private Model combinePom(Model pomToCombine) {
        Model newPom = pom.clone();
        Map<String, Dependency> map = new HashMap<>();
        for (Dependency item : pom.getDependencies()) {
            map.putIfAbsent(item.getGroupId(), item);
        }

        // Process List B
        for (Dependency item : pomToCombine.getDependencies()) {
            map.putIfAbsent(item.getGroupId(), item);
        }
        newPom.setDependencies(new ArrayList<>(map.values()));
        return newPom;
    }

    private List<ClassDeclaration> combineClasses(List<ClassDeclaration> classes) {
        Map<String, ClassDeclaration> nameMap = new HashMap<>();

        // Process List A
        for (ClassDeclaration item : classDeclarations) {
            nameMap.putIfAbsent(item.name, item);
        }

        // Process List B
        for (ClassDeclaration item : classes) {
            nameMap.merge(item.name, item, ClassDeclaration::combine);
        }

        // Convert map values to a list
        return new ArrayList<>(nameMap.values());
    }

    private List<OtherClass> combineOtherClasses(List<OtherClass> classes) {
        Map<String, OtherClass> nameMap = new HashMap<>();

        // Process List A
        for (OtherClass item : otherClasses) {
            nameMap.putIfAbsent(item.getPackageName() + "." + item.className, item);
        }

        // Process List B
        for (OtherClass item : classes) {
            nameMap.putIfAbsent(item.getPackageName() + "." + item.className, item);
        }

        // Convert map values to a list
        return new ArrayList<>(nameMap.values());
    }
}
