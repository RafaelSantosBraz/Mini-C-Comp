/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package preprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import parser.ErrorType;
import parser.Util;

/**
 *
 * @author rafael
 */
public class PreProcessor {

    public static final String PART = ".part";
    public static final String DONE = ".done";

    private final String originalName;

    public PreProcessor(String fileName) {
        originalName = fileName;
    }

    private Boolean replaceIncludes() {
        try {
            FileReader file = new FileReader(originalName + PART);
            BufferedReader buffer = new BufferedReader(file);
            String line;
            BufferedWriter fileDone = new BufferedWriter(new FileWriter(originalName + PART + DONE, false));
            while ((line = buffer.readLine()) != null) {
                if (line.matches("#include \"(.+)\"")) {
                    String parts[] = line.split(" ");
                    String filePath = parts[1];
                    PreProcessor prep = new PreProcessor(filePath.substring(1, filePath.length() - 1));
                    if (prep.doPrep()) {
                        FileReader fileInc = new FileReader(filePath.substring(1, filePath.length() - 1) + PART + DONE);
                        BufferedReader bufferInc = new BufferedReader(fileInc);
                        String lineInc;
                        while ((lineInc = bufferInc.readLine()) != null) {
                            fileDone.write(lineInc);
                            fileDone.newLine();
                        }
                    }
                } else {
                    fileDone.write(line);
                    fileDone.newLine();
                }
            }
            fileDone.close();
        } catch (FileNotFoundException ex) {
            ArrayList<Object> args = new ArrayList<>();
            args.add(originalName);
            Util.getInstance().error(ErrorType.FILE_DOES_NOT_EXIST, args);
            return false;
        } catch (IOException ex) {
            ArrayList<Object> args = new ArrayList<>();
            args.add(originalName);
            Util.getInstance().error(ErrorType.FILE_MANIPULATION_ERROR, args);
            return false;
        }
        return true;
    }

    private Boolean replaceDefines() {
        try {
            FileReader file = new FileReader(originalName);
            BufferedReader buffer = new BufferedReader(file);
            String line;
            ArrayList<String> defines = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();
            BufferedWriter filePart = new BufferedWriter(new FileWriter(originalName + PART, false));
            while ((line = buffer.readLine()) != null) {
                if (line.matches("#define\\s.+\\s((\".+\")|(\\d+)|('.'))")) {
                    String parts[] = line.split(" ");
                    defines.add(parts[1]);
                    values.add(parts[2]);
                } else if (line.matches("#include (.+)")) {
                    filePart.write(line);
                    filePart.newLine();
                } else {
                    String aux[] = line.split("\\W");
                    int start = 0;
                    for (String t : aux) {
                        int pos = line.indexOf(t, start);
                        start = pos + t.length();
                        for (int c = 0; c < defines.size(); c++) {
                            if (t.contentEquals(defines.get(c))) {
                                if (isCharCorrect(line, pos - 1, pos + t.length())) {
                                    line = replaceByPosition(line, t, values.get(c), pos - 1, pos + t.length());
                                }
                            }
                        }
                    }
                    filePart.write(line);
                    filePart.newLine();
                }
            }
            filePart.close();
        } catch (FileNotFoundException ex) {
            ArrayList<Object> args = new ArrayList<>();
            args.add(originalName);
            Util.getInstance().error(ErrorType.FILE_DOES_NOT_EXIST, args);
            return false;
        } catch (IOException ex) {
            ArrayList<Object> args = new ArrayList<>();
            args.add(originalName);
            Util.getInstance().error(ErrorType.FILE_MANIPULATION_ERROR, args);
            return false;
        }
        return true;
    }

    public Boolean doPrep() {
        return (replaceDefines() && replaceIncludes());
    }

    private Boolean isCharCorrect(String line, int p1, int p2) {
        if (p1 < 0 && p2 < line.length()) {
            return !(Character.isDigit(line.toCharArray()[p2]) || Character.isAlphabetic(line.toCharArray()[p2]));
        } else if (p1 >= 0 && p2 < line.length()) {
            return !((Character.isDigit(line.toCharArray()[p1]) || Character.isAlphabetic(line.toCharArray()[p1]))
                    && (Character.isDigit(line.toCharArray()[p2]) || Character.isAlphabetic(line.toCharArray()[p2])));
        } else if (p1 >= 0 && p2 >= line.length()) {
            return !(Character.isDigit(line.toCharArray()[p1]) || Character.isAlphabetic(line.toCharArray()[p1]));
        }
        return false;
    }

    private String replaceByPosition(String line, String match, String value, int p1, int p2) {
        if (p1 < 0 && p2 < line.length()) {
            return line.replace(match + line.toCharArray()[p2], value + line.toCharArray()[p2]);
        } else if (p1 >= 0 && p2 < line.length()) {
            return line.replace(line.toCharArray()[p1] + match + line.toCharArray()[p2], line.toCharArray()[p1] + value + line.toCharArray()[p2]);
        } else if (p1 >= 0 && p2 >= line.length()) {
            return line.replace(line.toCharArray()[p1] + match, line.toCharArray()[p1] + value);
        } else {
            return line.replace(match, value);
        }
    }
}
