package mainpackage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    
    public static List<Students> loadStudentsFromFile(String filename) {
        List<Students> students = new ArrayList<>();
        BufferedReader br = null;
        
        try {
            br = new BufferedReader(new FileReader(filename));
            String line;
            
            while ((line = br.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                // Split components by semicolon (;)
                String[] parts = line.split(";");
                if (parts.length == 5) {
                    try {
                        String username = parts[0].trim();
                        String name = parts[1].trim();
                        String surname = parts[2].trim();
                        String dept = parts[3].trim();
                        int regNum = Integer.parseInt(parts[4].trim());
                        
                        // Create and add the student object to the list
                        students.add(new Students(username, name, surname, dept, regNum));
                    } catch (NumberFormatException nfe) {
                        System.err.println("Σφάλμα μορφοποίησης αριθμού μητρώου: " + parts[4]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Explicitly close resources in the finally block according to the slides
            try {
                if (br != null) br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return students;
    }
}