package io.github.masterarbeit.generator.helper;

import io.github.masterarbeit.generator.helper.method.MethodDeclaration;
import io.github.masterarbeit.util.ListUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.maven.model.Model;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDeclaration {
    String name;
    List<ClassDeclaration> classDeclarations = new ArrayList<>();
    Model pom;


    public void addClassDeclaration(ClassDeclaration classDeclaration) {
        classDeclarations.add(classDeclaration);
    }

    public Pair<RequestType, Annotation> getRequestTypeAndAnnotationByMethodName(String functionName) {
        for (ClassDeclaration clazz : classDeclarations) {
            for (MethodDeclaration method : clazz.getMethods()) {
                if (functionName.equals(method.getName())) {
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

    public ProjectDeclaration combine(ProjectDeclaration project) {
        ProjectDeclaration newProject = new ProjectDeclaration();

        newProject.setPom(combinePom(project.pom));

        newProject.setName(name);

        newProject.setClassDeclarations(combineClasses(project.classDeclarations));

        return newProject;
    }

    private Model combinePom(Model pomToCombine) {
        Model newPom = pom.clone();
        newPom.setDependencies(ListUtil.combineWithoutDuplicates(pom.getDependencies(), pomToCombine.getDependencies()));
        return newPom;
    }

    private List<ClassDeclaration> combineClasses(List<ClassDeclaration> classes) {
        List<ClassDeclaration> newClasses = new ArrayList<>();

        for (ClassDeclaration clazz : classDeclarations) {
            List<ClassDeclaration> sameClasses = newClasses.stream().filter(value -> value.getName().equals(clazz.getName())).toList();
            if (!sameClasses.isEmpty()) {
                newClasses.add(clazz.combine(sameClasses.get(0)));
            } else {
                newClasses.add(clazz);
            }
        }


        return newClasses;
    }
}
