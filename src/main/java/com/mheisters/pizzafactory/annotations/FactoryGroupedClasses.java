package com.mheisters.pizzafactory.annotations;

import com.mheisters.pizzafactory.exception.IdAlreadyInUseException;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class FactoryGroupedClasses {

    private static final String SUFFIX = "Factory";

    private final String qualifiedClassName;

    private final Map<String, FactoryAnnotatedClass> itemsMap = new LinkedHashMap<>();

    public FactoryGroupedClasses(String qualifiedClassName) {
        this.qualifiedClassName = qualifiedClassName;
    }

    public void add(FactoryAnnotatedClass toInsert) throws IdAlreadyInUseException {
        FactoryAnnotatedClass existing = itemsMap.get(toInsert.getId());

        if (existing != null) {
            throw new IdAlreadyInUseException(existing);

        }

        itemsMap.put(toInsert.getId(), toInsert);
    }

    public void generateCode(Elements elementUtils, Filer filer) throws IOException {
        TypeElement superClass = elementUtils.getTypeElement(qualifiedClassName);
        String factoryClassName = superClass.getSimpleName() + SUFFIX;
        String packageName = elementUtils.getPackageOf(superClass).getQualifiedName().toString();

        MethodSpec.Builder createBuilder = MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "id")
                .returns(ClassName.get(superClass))
                .beginControlFlow("if (id == null)")
                .addStatement("throw new $T($S)", IllegalArgumentException.class, "id is null!")
                .endControlFlow();


        for (FactoryAnnotatedClass item : itemsMap.values()) {
            createBuilder.beginControlFlow("if ($S.equals(id))", item.getId())
                    .addStatement("return new $T()", item.getTypeElement().getQualifiedName())
                    .endControlFlow();
        }
        createBuilder.addStatement("throw new $T($S + id)", IllegalArgumentException.class,
                        "Unknown id)")
                .build();

        TypeSpec factoryClass = TypeSpec.classBuilder(factoryClassName)
                .addModifiers(Modifier.PUBLIC)
                .addOriginatingElement(superClass)
                .addMethod(createBuilder.build())
                .build();

        JavaFile.builder(packageName,
                factoryClass).build().writeTo(filer);
    }
}
