package com.sitecake.commons.compile;

import java.io.PrintWriter;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

public class StringConstantsGenerator extends Generator {

	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
		TypeOracle typeOracle = context.getTypeOracle();
		JClassType classType = typeOracle.findType(typeName);
		
		String newTypeSimpleName = classType.getSimpleSourceName() + "GeneratedImpl";
		String newTypePackageName = classType.getPackage().getName(); 
		
		PrintWriter printWriter = context.tryCreate(logger, newTypePackageName, newTypeSimpleName);
		
		if ( printWriter != null ) {
			
			ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(newTypePackageName, newTypeSimpleName);
			
			composer.addImplementedInterface(typeName);
			SourceWriter sourceWriter = composer.createSourceWriter(context, printWriter);
			
			for ( JMethod method : classType.getMethods() ) {
				if ( method.isAnnotationPresent(RawStringValue.class) ) {
					try {
						String rawValue = method.getAnnotation(RawStringValue.class).value();
						String develRawValue = method.getAnnotation(RawStringValue.class).develValue();
						sourceWriter.println("public String " + method.getName() + "() { ");
						sourceWriter.println("String value = \"" + rawValue + "\";");
						
						if ( !"".equals(develRawValue) ) {
							sourceWriter.println("if ( !com.google.gwt.core.client.GWT.isScript() ) {");
							sourceWriter.println("value = \"" + develRawValue + "\";");
							sourceWriter.println("}");
						}
						sourceWriter.println("return value;");
						sourceWriter.println("}");
					} catch (Exception e) {
						throw new UnableToCompleteException();
					}
				}
			}
			sourceWriter.commit(logger);
		}
		
		return newTypePackageName + "." + newTypeSimpleName;
	}

}
