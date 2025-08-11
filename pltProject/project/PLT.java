package pltProject.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class PLT {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String fileContent = instruction();
        List<String> dataType = extractDataTypes(fileContent);
        List<String> var = extractVariables(fileContent);
        List<String> assign = extractAssignments(fileContent);
        List<String> value = extractValues(fileContent, dataType);
        List<String> stop = extractStops(fileContent);
        List<List<String>> numbersList = check(fileContent, var);
        writeToFile(convertToJavaCode(fileContent, var));
        
        System.out.println("Check the file named JPLT2");
    }

    static String instruction() throws FileNotFoundException, IOException {
    File I = new File("pltProject/PLT2.txt");  // specify relative path
    FileInputStream file = new FileInputStream(I);
    byte[] b = new byte[(int) I.length()];
    file.read(b);
    file.close();
    return new String(b);
}


    public static List<String> extractDataTypes(String file) {
        String[] lines = file.split("\n");
        List<String> dataType = new ArrayList<>();
        for (int i=0;i<lines.length;i++) {
            if (lines[i].startsWith("print")) 
            	continue; 
            int space1 = lines[i].indexOf(' ');
            dataType.add(lines[i].substring(0, space1));
        }
        checkDataType(dataType);
        return dataType;
    }

    public static List<String> extractVariables(String file) {
        String[] lines = file.split("\n");
        List<String> var = new ArrayList<>();
        for (int i=0;i<lines.length;i++) {
            if (lines[i].startsWith("print")) 
            	continue;
            int space1 = lines[i].indexOf(' ');
            int space2 = lines[i].indexOf(' ', space1 + 1);
            var.add(lines[i].substring(space1 + 1, space2));
        }
        checkVariables(var);
        return var;
    }

    public static List<String> extractAssignments(String file) {
        String[] lines = file.split("\n");
        List<String> assign = new ArrayList<>();
        for (int i=0;i<lines.length;i++) {
            if (lines[i].startsWith("print"))
            	continue;
            int space1 = lines[i].indexOf(' ');
            int space2 = lines[i].indexOf(' ', space1 + 1);
            int space3 = lines[i].indexOf(' ', space2 + 1);
            if (space3 != -1) { //in g
                assign.add(lines[i].substring(space2 + 1, space3));
            } 
            else {
                System.out.println("Missing space after the variable name in line: " + (assign.size() + 1));
                System.exit(-1);
            }
        }
        checkAssign(assign);
        return assign;
    }

   public static List<String> extractValues(String file, List<String> dataType) {
        String[] lines = file.split("\n");
        List<String> value = new ArrayList<>();

        for (int r = 0; r < lines.length; r++) {

            if (!lines[r].contains("sub") && !lines[r].contains("sum") && !lines[r].contains("mul")) {
                String line = lines[r];
                int space1 = line.indexOf(' ');
                int space2 = line.indexOf(' ', space1 + 1);
                int space3 = line.indexOf(' ', space2 + 1);
                int space4 = line.indexOf(' ', space3 + 1);
                if (space4 != -1) {
                    String valueString = line.substring(space3 + 1, space4);
                    try {
                        if (dataType.equals("in")) {
                            int intValue = Integer.parseInt(valueString);
                            value.add(String.valueOf(intValue));
                        } else if (dataType.equals("fl")) {
                            float floatValue = Float.parseFloat(valueString);
                            value.add(String.valueOf(floatValue));
                        } else if (dataType.equals("dou")) {
                            double doubleValue = Double.parseDouble(valueString);
                            value.add(String.valueOf(doubleValue));
                        }
                    } 
                    catch (NumberFormatException e) {

                           System.out.println("Please enter a valid value in line: " + (r + 1));
                        System.exit(-1);
                    }
                } else {
                    System.out.println("Missing space after the value in line: " + (r + 1));
                    System.exit(-1);
                }
            }
        }
        return value;
    }

   public static List<String> extractStops (String file) {
	    String[] lines = file.split("\n");
	    List<String> stops = new ArrayList<>();
	    for (int i = 0; i < lines.length; i++) {
	        String line = lines[i].trim();
	        if (!line.endsWith("#")) {
	            System.out.println("Error: Line does not end with #  "+ (i + 1));
                System.exit(-1);
	        }
	        String[] words = line.split("\\s+");
	        for (int j=0;j<words.length;j++) {
	            if (words[j].equals("##")) {
	            	System.out.println("Error: Line does can't end with ##  "+ (i + 1));
                    System.exit(-1);
	            }
	            else
	            	stops.add(words[j]);
	        }
	    }
	    return stops;
   }

    public static List<List<String>> check(String file, List<String> variables) {
        String[] lines = file.split("\n");
        List<List<String>> numbers = new ArrayList<>();
        for (int j=0;j<lines.length;j++) {
            List<String> lineNumbers = new ArrayList<>();
            String[] words = lines[j].split("\\s+");
            for (int i = 0; i < words.length; i++) {
                String word = words[i];
                if (word.equals("=") || word.equals("sum") || word.equals("sub") || word.equals("mul")) {
                    if (i + 1 < words.length) { // in g = 9 sum 
                        String numberString = words[i + 1]; //sum 9 sum q
                        if (isNumeric(numberString) || variables.contains(numberString)) {
                            lineNumbers.add(numberString);
                        } 
                        else {
                            System.out.println("Please enter a valid number in line or The variable Not defined: " + (numbers.size() + 1));
                            System.exit(-1);
                        }
                    }
                    else {
                        System.out.println("Missing value after operator in line: " + (numbers.size() + 1));
                        System.exit(-1);
                    }
                }
            }
            if (!lineNumbers.isEmpty()) { //sum
                numbers.add(lineNumbers);
            }
        }
        return numbers;
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } 
        catch (NumberFormatException e) {
            return false;
        }
    }

    public static void checkDataType(List<String> dataType) {
        for (int i = 0; i < dataType.size(); i++) {
            if (!dataType.get(i).equals("in") && !dataType.get(i).equals("fl") && !dataType.get(i).equals("dou")) {
                System.out.println("DataType Error in line: " + (i + 1));
                System.exit(0);
            }
        }
    }

    public static void checkAssign(List<String> assign) {
        for (int i = 0; i < assign.size(); i++) {
            if (!assign.get(i).equals("=")) {
                assign.set(i, "=");//عشان لو مفيش = يحط هو 
            }
        }
    }

    public static void checkVariables(List<String> variables) {
        for (int i = 0; i < variables.size(); i++) {
            String variable = variables.get(i);
            if (!Character.isLetter(variable.charAt(0))) {
                System.out.println("Error: Variable names must start with a letter in line " + (i + 1));
                System.exit(0);
            } else if (variable.isEmpty() || variable.equals("=")) {
                System.out.println("Error: Invalid variable name in line " + (i + 1));
                System.exit(0);
            }
            for (int j = i + 1; j < variables.size(); j++) {
                if (variables.get(i).equals(variables.get(j))) {
                    System.out.println("Error: The variable is repeated in line " + (j + 1));
                    System.exit(0);
                }
            }
        }
    }

    public static String convertToJavaCode(String file, List<String> definedVariables) {
        String javaCode = "";
        String[] lines = file.split("\n");
        
        for (int j=0;j<lines.length;j++) {
            if (lines[j].startsWith("print")) {
                javaCode += convertPrintStatement(lines[j], definedVariables);
            } else {
                String[] tokens = lines[j].split("\\s+");

                String dataType = tokens[0];
                if (dataType.equals("in")) {
                    dataType = "int";
                } else if (dataType.equals("fl")) {
                    dataType = "float";
                } else if (dataType.equals("dou")) {
                    dataType = "double";
                }

                String variableName = tokens[1];
                javaCode += dataType + " " + variableName + " = ";
                for (int i = 3; i < tokens.length; i++) {
                    if (tokens[i].equals("#")) 
                        break;
                    if (tokens[i].equals("sum")) {
                        javaCode += " + ";
                    } else if (tokens[i].equals("sub")) {
                        javaCode += " - ";
                    } else if (tokens[i].equals("mul")) {
                        javaCode += " * ";
                    } else {
                        javaCode += tokens[i];
                    }
                }
                javaCode += ";\n";
            }
        }
        return javaCode;
    }

    public static String convertPrintStatement(String line, List<String> variables) {
        String[] tokens = line.split("\\s+", 2); //بتقسم السطر لجزئين
        if (tokens.length > 1) {
            String printContent = tokens[1].trim(); 
            if (printContent.startsWith("\"") && printContent.endsWith("#")) {
                printContent = printContent.substring(0, printContent.length() - 1);
                return "System.out.println(" + printContent + ");\n";
            }
            else if (printContent.startsWith("+") && printContent.endsWith("#")) {
                String variable = printContent.substring(1, printContent.length() - 1).trim(); 
                if (variables.contains(variable)) {
                    return "System.out.println(" + variable + ");\n";
                } else {
                    System.out.println("The printed variable is not initialized: " + variable);
                    System.exit(0);
                }
            }
            else {
                System.out.println("Missing double quotation or invalid format in line: " + line);
                System.exit(0);
            }
        }
        return ""; 
    }


    public static void writeToFile(String content) {
        try {
            FileWriter writer = new FileWriter("JPLT2.txt");
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }
}
