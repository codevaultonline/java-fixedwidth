package online.codevault.java.utilities.fixedformat.generator;

import com.squareup.javapoet.*;
import online.codevault.java.utilities.fixedformat.annotations.FixedWidthField;
import online.codevault.java.utilities.fixedformat.annotations.FixedWidthRecord;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Generator {

    public static void generateFromCsv(File inputFile, File outputDirectory, boolean includesHeader) throws Exception {

        // check if inputFile exists
        if (!inputFile.exists()) {
            System.err.println("Input file \"" + inputFile.getPath() + "\" does not exist!");
        }

        // check if inputFile is a directory
        if (!outputDirectory.isDirectory()) {
            throw new Exception("Output path is not a directory!");
        }

        // check if outputDirectory exists
        if (!outputDirectory.exists()) {
            throw new Exception("Output directory does not exist!");
        }

        // check if outputDirectory is a directory
        if (!outputDirectory.isDirectory()) {
            throw new Exception("Output path is not a directory!");
        }

        List<RecordDefinition> definitions = new ArrayList<>();

        Reader in = new FileReader(inputFile);

        CSVFormat.Builder builder = CSVFormat.Builder.create();

        if (!includesHeader) {
            builder.setSkipHeaderRecord(true);
        } else {
            builder.setHeader();
        }

        CSVFormat reader = builder.build();

        Iterable<CSVRecord> records = reader.parse(in);

        RecordDefinition definition = null;

        String lastClassName = null;
        int fieldNumber = 0;

        for (CSVRecord record : records) {

            String packageName = record.get(0);
            String className = record.get(1);
            String recordLength = record.get(2);
            String classDescription = record.get(3);
            String format = record.get(7);
            FieldDefinition.SignedValueIndicator signedValueIndicator = (record.size() >= 11) ? StringUtils.isNotBlank(record.get(10)) ? FieldDefinition.SignedValueIndicator.valueOf(record.get(10)) : null : null;
            String additionalInformation = (record.size() >= 12) ? StringUtils.isNotBlank(record.get(11)) ? record.get(11) : null : null;
            String extendedInformation = (record.size() >= 13) ? StringUtils.isNotBlank(record.get(12)) ? record.get(12) : null : null;

            if (StringUtils.isAnyBlank(packageName, className)) {
                break;
            }

            if (!className.equals(lastClassName)) {

                if (null != definition) {
                    definitions.add(definition);
                }

                fieldNumber = 0;

                definition = new RecordDefinition();

                // add package name
                definition.setPackageName(packageName);

                // add record length
                definition.setLength(recordLength);

                // add class name
                definition.setClassName(className);

                // add class description
                if (!StringUtils.isEmpty(classDescription)) {
                    classDescription = classDescription.trim();
                    definition.setDescription(classDescription);
                }

            }

            // increment field number
            ++fieldNumber;

            // set class description
            if (!StringUtils.isBlank(classDescription)) {
                definition.setDescription(WordUtils.capitalize(classDescription.toLowerCase()).trim());
            }

            // get type
            String type = StringUtils.trim(record.get(6)).toLowerCase();

            FieldDefinition field = new FieldDefinition();

            field.setOrdinal(fieldNumber);

            if (null != signedValueIndicator) {
                field.setSignedValueIndicator(signedValueIndicator);
            }

            if (StringUtils.isBlank(type)) {
                field.setType(String.class);
            } else {

                switch (type) {

                    case "int":
                        field.setType(int.class);
                        break;

                    case "char":
                        field.setType(char.class);
                        break;

                    case "integer":
                        field.setType(Integer.class);
                        break;

                    case "bigdecimal":
                        field.setType(BigDecimal.class);
                        break;

                    case "date":
                        field.setType(LocalDate.class);
                        break;

                    case "time":
                        field.setType(LocalTime.class);
                        break;

                }

            }

            // get field start position
            field.setStartPosition(Integer.parseInt(record.get(8)));

            // get field length
            field.setLength(Integer.parseInt(record.get(9)));

            // determine field end position
            field.setEndPosition(field.getStartPosition() + field.getLength() - 1);

            if (!StringUtils.isEmpty(format)) {
                field.setFormatString(format);
            }

            if (!StringUtils.isEmpty(additionalInformation)) {
                field.setAdditionalInformation(additionalInformation.replaceAll("\"", "\\\\\""));
            }

            if (!StringUtils.isEmpty(extendedInformation)) {
                field.setExtendedInformation(extendedInformation.replaceAll("\"", "\\\\\""));
            }

            if (type.equals("filler")) {

                field.setFiller(true);

            } else {

                // generate field name
                String name = StringUtils.firstNonBlank(record.get(5), record.get(4)).toLowerCase();

                String fieldNameStandardization = name.replaceAll("[^a-zA-Z\\d\\s:]", "");
                fieldNameStandardization = fieldNameStandardization.replaceAll("\\s+", " ");
                fieldNameStandardization = fieldNameStandardization.replaceAll("\\([^)]*\\)", "");
                fieldNameStandardization.toLowerCase();

                String[] fieldNameParts = fieldNameStandardization.split(" ");
                StringBuilder fieldName = new StringBuilder();

                for (int i = 0; i < fieldNameParts.length; i++) {
                    if (i == 0) {
                        fieldName.append(fieldNameParts[i]);
                    } else {
                        fieldName.append(StringUtils.capitalize(fieldNameParts[i]));
                    }
                }

                field.setFieldName(fieldName.toString());

            }

            definition.addField(field);

            lastClassName = className;

        }

        if (null != definition) {
            definitions.add(definition);
        }

        Generator.generateOutput(definitions, outputDirectory);

    }

    private static void generateOutput(List<RecordDefinition> records, File outputDirectory) throws Exception {

        for (RecordDefinition record : records) {

            File outputFile = new File(outputDirectory.toString() + File.separator + record.getClassName() + ".java");

            // delete output file if it exists
            if (outputFile.exists()) {
                outputFile.delete();
                Thread.sleep(2000);
            }

            final TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(record.getClassName())
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

            final AnnotationSpec.Builder classAnnotation = AnnotationSpec.builder(FixedWidthRecord.class);
            classAnnotation.addMember("length", "$L", record.getLength());

            if (!StringUtils.isBlank(record.getDescription())) {
                classAnnotation.addMember("description", "$S", record.getDescription());
            }

            typeSpecBuilder.addAnnotation(classAnnotation.build());

            // create field definitions
            for (FieldDefinition field : record.getFields()) {

                FieldSpec.Builder fieldSpecBuilder = null;

                if (field.isFiller()) {

                    fieldSpecBuilder = FieldSpec.builder(String.class, "field" + field.getOrdinal(), Modifier.PRIVATE);

                    // add unused declaration annotation
                    fieldSpecBuilder.addAnnotation(AnnotationSpec
                            .builder(SuppressWarnings.class)
                            .addMember("value", "$L", "{\"UnusedDeclaration\"}")
                            .build());

                    fieldSpecBuilder.addAnnotation(
                            AnnotationSpec.builder(FixedWidthField.class)
                                    .addMember("ordinal", "$L", field.getOrdinal())
                                    .addMember("startPosition", "$L", field.getStartPosition())
                                    .addMember("endPosition", "$L", field.getEndPosition())
                                    .addMember("length", "$L", field.getLength())
                                    .addMember("description", "$S", "Filler")
                                    .addMember("filler", "$L", "true")
                                    .build());

                } else {

                    fieldSpecBuilder = FieldSpec.builder(field.getType(), field.getFieldName(), Modifier.PUBLIC);

                    AnnotationSpec.Builder methodAnnotation = AnnotationSpec.builder(FixedWidthField.class)
                            .addMember("ordinal", "$L", field.getOrdinal())
                            .addMember("startPosition", "$L", field.getStartPosition())
                            .addMember("endPosition", "$L", field.getEndPosition())
                            .addMember("length", "$L", field.getLength());

                    if (null != field.getSignedValueIndicator()) {
                        methodAnnotation.addMember("signedValueIndicator", "$T.$L", FieldDefinition.SignedValueIndicator.class, field.getSignedValueIndicator().name());
                    }

                    // format
                    if (null != field.getFormatString()) {
                        methodAnnotation.addMember("formatString", "$S", field.getFormatString());
                    }

                    // additional information
                    if (null != field.getAdditionalInformation()) {
                        methodAnnotation.addMember("additionalInformation", "$S", field.getAdditionalInformation());
                    }

                    // extended information
                    if (null != field.getExtendedInformation()) {
                        methodAnnotation.addMember("extendedInformation", "$S", field.getExtendedInformation());
                    }

                    if (!StringUtils.isBlank(field.getDescription())) {
                        methodAnnotation.addMember("description", "$S", field.getDescription());
                    }

                    fieldSpecBuilder.addAnnotation(methodAnnotation.build());

                }

                // getters/setters
                if (!field.isFiller()) {

                    // create getter
                    typeSpecBuilder.addMethod(
                            MethodSpec.methodBuilder("get" + StringUtils.capitalize(field.getFieldName()))
                                    .addModifiers(Modifier.PUBLIC)
                                    .returns(field.getType())
                                    .addCode("return this." + field.getFieldName() + ";" + System.lineSeparator())
                                    .build()
                    );

                    // create getter
                    typeSpecBuilder.addMethod(
                            MethodSpec.methodBuilder("set" + StringUtils.capitalize(field.getFieldName()))
                                    .addModifiers(Modifier.PUBLIC)
                                    .addParameter(field.getType(), field.getFieldName())
                                    .addCode("this." + field.getFieldName() + " = " + field.getFieldName() + ";" + System.lineSeparator())
                                    .build()
                    );

                }

                typeSpecBuilder.addField(fieldSpecBuilder.build());

            }

            final TypeSpec typeSpec = typeSpecBuilder.build();

            JavaFile javaFile = JavaFile.builder(record.getPackageName(), typeSpec)
                    .build();

            FileWriter writer = new FileWriter(outputFile);
            javaFile.writeTo(writer);
            writer.flush();
            writer.close();

        }

    }

    private static String prepareArguments(AnnotationArguments arguments) {

        StringBuilder argumentString = new StringBuilder();

        for (AnnotationArgument argument : arguments.getArguments()) {

            if (0 != argumentString.length()) {
                argumentString.append(", ");
            }

            if (int.class.equals(argument.getClassType())) {
                argumentString.append(argument.getName() + " = " + argument.getValue());
            } else if (boolean.class.equals(argument.getClassType())) {
                argumentString.append(argument.getName() + " = " + argument.getValue());
            } else {
                argumentString.append(argument.getName() + " = \"" + argument.getValue() + "\"");
            }

        }

        return argumentString.toString();

    }

    public static void main(String[] args) throws Exception {

        if (3 != args.length) {
            System.err.println("Not enough arguments have been submitted.");
            return;
        }

        Generator.generateFromCsv(new File(args[0]), new File(args[1]), Boolean.parseBoolean(args[2]));

    }

}
