package com.mheisters.pizzafactory.annotations;

import com.google.auto.service.AutoService;
import com.mheisters.pizzafactory.exception.IdAlreadyInUseException;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;

@AutoService(Processor.class)
public class FactoryProcessor extends AbstractProcessor {

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;
    private final Map<String, FactoryGroupedClasses> factoryClasses = new LinkedHashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        //  messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        LinkedHashSet<String> annotations = new LinkedHashSet<>();
        annotations.add(Factory.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Factory.class)) {
            if (annotatedElement.getKind() != ElementKind.CLASS) {
                error(annotatedElement, "Only classes can be annotated with %s",
                        Factory.class.getSimpleName());
                return true; // Exit processing
            }

            TypeElement typeElement = (TypeElement) annotatedElement;
            if (addFactoryAnnotatedClassesToFactoryAnnotatedGroupedClass(annotatedElement, typeElement))
                return true; // Error Message printed, exit processing.

            generateCodeForClasses();
        }


        return false;
    }

    private void generateCodeForClasses() {
        try {
            for (FactoryGroupedClasses factoryClass : factoryClasses.values()) {
                factoryClass.generateCode(elementUtils, filer);
            }
        } catch (IOException e) {
            error(null, e.getMessage());
        }
    }

    private boolean addFactoryAnnotatedClassesToFactoryAnnotatedGroupedClass(Element annotatedElement, TypeElement typeElement) {
        try {
            FactoryAnnotatedClass annotatedClass = new FactoryAnnotatedClass(typeElement); //
            // Throws IllegalArgumentException

            if (!isValidClass(annotatedClass)) {
                return true;
            }
            // Everything is fine, so try to add
            FactoryGroupedClasses factoryClass =
                    factoryClasses.get(annotatedClass.getQualifiedFactoryGroupName());
            if (factoryClass == null) {
                String qualifiedGroupName = annotatedClass.getQualifiedFactoryGroupName();
                factoryClass = new FactoryGroupedClasses(qualifiedGroupName);
                factoryClasses.put(qualifiedGroupName, factoryClass);
            }

            // Throws IdAlreadyInUseException if id is conflicting with
            // another @Factory annotated class with the same id
            factoryClass.add(annotatedClass);

        } catch (IllegalArgumentException e) {
            // @Factory.id() is empty
            error(typeElement, e.getMessage());
            return true;
        } catch (IdAlreadyInUseException idAlreadyInUseException) {
            FactoryAnnotatedClass existing = idAlreadyInUseException.getExisting();

            Factory factory = annotatedElement.getAnnotation(Factory.class);
            // Already existing
            error(annotatedElement, "Conflict: The class %s is annotated with @%s with id = " +
                            "'%s' but %s" +
                            " already uses the same id",
                    typeElement.getQualifiedName(),
                    Factory.class.getSimpleName(),
                    factory != null ? factory.id() :
                            "unknown",
                    existing.getTypeElement().getQualifiedName());

            return true;
        }
        return false;
    }

    private void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR,
                String.format(msg, args), e);
    }

    private boolean isValidClass(FactoryAnnotatedClass item) {
        // Cast to typeElement since it has more specific methods
        TypeElement classElement = item.getTypeElement();

        if (!classElement.getModifiers().contains(Modifier.PUBLIC)) {
            error(classElement, "The class %s is not public.", classElement.getQualifiedName());
            return false;
        }

        // Check if annotated class is abstract
        if (classElement.getModifiers().contains(Modifier.ABSTRACT)) {
            error(classElement, "The class %s is abstract. You cannot annotate abstract classes " +
                            "with @%s",
                    classElement.getQualifiedName(), Factory.class.getSimpleName());
            return false;
        }

        // Check inheritance: Class must be child class as specified in @Factory.type()
        TypeElement superClassElement =
                elementUtils.getTypeElement(item.getQualifiedFactoryGroupName());
        if (superClassElement.getKind() == ElementKind.INTERFACE) {
            // Interface implemented?
            if (!classElement.getInterfaces().contains(superClassElement.asType())) {
                error(classElement, "The class %s annotated with @%s must implement the interface" +
                                " %s" +
                                ".", classElement.getQualifiedName(),
                        Factory.class.getSimpleName(), item.getQualifiedFactoryGroupName());
                return false;
            }
        } else {
            // Check subclassing
            if (validateSubclasses(item, classElement)) return false;
        }

        // Check if an empty public constructor is given
        if (validatePublicConstructor(classElement)) return true;

        // No Empty constructor found
        error(classElement, "The class %s must provide a public empty default constructor.",
                classElement.getQualifiedName());
        return false;

    }

    private static boolean validatePublicConstructor(TypeElement classElement) {
        for (Element enclosed : classElement.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement constructor = (ExecutableElement) enclosed;
                if (constructor.getParameters().isEmpty() && constructor.getModifiers().contains(Modifier.PUBLIC)) {
                    // Found an empty public constructor
                    return true;
                }
            }
        }
        return false;
    }

    private boolean validateSubclasses(FactoryAnnotatedClass item, TypeElement classElement) {
        TypeElement currentClass = classElement;

        while (true) {
            TypeMirror superClassType = currentClass.getSuperclass();

            if (superClassType.getKind() == TypeKind.NONE) {
                // Base class (java.lang.Object) reached, so exit
                error(classElement, "The class %s annotated with @%s must inherit from %s,",
                        classElement.getQualifiedName(), Factory.class.getSimpleName(),
                        item.getQualifiedFactoryGroupName());
                return true;
            }

            if (superClassType.toString().equals(item.getQualifiedFactoryGroupName())) {
                // Required superclass found
                break;
            }

            // Moving up the inheritance tree
            currentClass = (TypeElement) typeUtils.asElement(superClassType);
        }
        return false;
    }
}
