/*
 * Copyright 2019 codevault.online
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package online.codevault.java.utilities.fixedformat;

import online.codevault.java.utilities.fixedformat.annotations.FixedWidthField;
import online.codevault.java.utilities.fixedformat.annotations.FixedWidthRecord;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FixedWidthWriter {

    private OutputStream os;

    public FixedWidthWriter(OutputStream os) {
        this.os = os;
    }

    public void write(Object object) throws WriterException, NoSuchFieldException, IllegalAccessException, IOException, NoSuchMethodException, InvocationTargetException {

        Class<?> clazz = object.getClass();

        if (!clazz.isAnnotationPresent(FixedWidthRecord.class)) {
            throw new WriterException("Class " + object.getClass().getName().toString() + " is not annotated with as a FixedWidthRecord.");
        }

        FixedWidthRecord recordAnnotation = clazz.getAnnotation(FixedWidthRecord.class);

        StringBuilder line = new StringBuilder();

        for (Field field : object.getClass().getDeclaredFields()) {

            if (field.isAnnotationPresent(FixedWidthField.class)) {

                FixedWidthField fieldAnnotation = field.getAnnotation(FixedWidthField.class);

                // sanity check for field start position, end position and length agreement
                if ((fieldAnnotation.endPosition() - fieldAnnotation.startPosition() + 1) != fieldAnnotation.length()) {
                    throw new WriterException("Field " + field.getName() + " has a start and end position that does not match length");
                }

                field.setAccessible(true);

                String value;

                Object fieldObject = field.get(object);

                if (null == fieldObject) {
                    value = "";
                } else if (field.getType().equals(int.class)) {
                    value = Integer.toString((int) fieldObject);
                } else if (field.getType().equals(Integer.class)) {
                    value = Integer.toString((Integer) fieldObject);
                } else if (field.getType().equals(LocalDate.class)) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fieldAnnotation.dateFormat());
                    value = ((LocalDate) fieldObject).format(formatter);
                } else if (field.getType().equals(Boolean.class)) {

                    switch (fieldAnnotation.booleanDisplay()) {

                        case YES_NO:
                            value = ((boolean) fieldObject) ? "YES" : "NO";
                            break;

                        case Y_N:
                            value = ((boolean) fieldObject) ? "Y" : "N";
                            break;

                        case yes_no:
                            value = ((boolean) fieldObject) ? "yes" : "no";
                            break;

                        case y_n:
                            value = ((boolean) fieldObject) ? "y" : "n";
                            break;

                        case TRUE_FALSE:
                            value = ((boolean) fieldObject) ? "TRUE" : "FALSE";
                            break;

                        case true_false:
                            value = ((boolean) fieldObject) ? "true" : "false";
                            break;

                        case ONE_ZERO:
                        default:
                            value = ((boolean) fieldObject) ? "1" : "0";
                            break;

                    }

                } else if (field.getType().isEnum()) {

                    Method method = field.getType().getDeclaredMethod("getValue");

                    if (null != method) {
                        value = (String) method.invoke(fieldObject);
                    } else {
                        throw new WriterException("Field " + field.getName() + " is an ENUM, however it has no getValue method.");
                    }

                } else {
                    value = StringUtils.defaultIfEmpty((String) fieldObject, "");
                }

                if (fieldAnnotation.mandatory() && StringUtils.isEmpty(value)) {
                    throw new WriterException("Field " + field.getName() + " is mandatory, however it has no value assigned.");
                }

                if (fieldAnnotation.filler()) {

                    line.append(StringUtils.repeat(" ", fieldAnnotation.length()));

                } else {

                    if (Padding.RIGHT.equals(fieldAnnotation.padding())) {
                        line.append(StringUtils.rightPad(value, fieldAnnotation.length(), fieldAnnotation.paddingCharacter()));
                    } else if (Padding.LEFT.equals(fieldAnnotation.padding())) {
                        line.append(StringUtils.leftPad(value, fieldAnnotation.length(), fieldAnnotation.paddingCharacter()));
                    }

                }

            }

        }

        // sanity check for record length
        if ((0 != recordAnnotation.length()) && (recordAnnotation.length() != line.length())) {
            System.err.println(line.toString());
            throw new WriterException("Actual record length (" + line.length() + ") does not match expected length (" + recordAnnotation.length() + ").");
        }

        line.append("\r\n");
        this.os.write(line.toString().getBytes());

    }

    public OutputStream getOutputStream() {
        return os;
    }

}
