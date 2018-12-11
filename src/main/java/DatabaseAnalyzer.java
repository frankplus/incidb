import com.opencsv.CSVReader;
import java.io.*;
import java.util.ArrayList;

public class DatabaseAnalyzer {
    public static void main(String args[]){
        Reader reader = null;
        try {
            reader = new BufferedReader(new FileReader("src/main/resources/incidb.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ArrayList<Ingredient> listIngredients = new ArrayList<Ingredient>(); //initializing list of ingredients

        //initializing openCSV reader
        CSVReader csvReader = new CSVReader(reader);
        String[] line;

        //for each line in the csv add an Ingredient object to the list
        try {
            while ((line = csvReader.readNext()) != null) {
                if(line.length > 6) {
                    Ingredient element = new Ingredient();
                    element.setCosingRefNo(line[0]);
                    element.setInciName(line[1]);
                    element.setDescription(line[6]);
                    listIngredients.add(element);
                }
                else {
                    System.out.println("There is an empty line in the database file, line "+csvReader.getLinesRead());
                }
            }

        } catch(IOException e){
            System.out.println("Error trying to read csv");
        }

        //closing
        try {
            reader.close();
            csvReader.close();
        } catch(IOException e){
            System.out.println("Error closing csv reader");
        }

        //generate
        final int minChars = 2;
        ArrayList<String> wordlist = new ArrayList<String>();
        for(Ingredient ingredient : listIngredients){
            String[] splitted = ingredient.getInciName().split("[\\[\\] -/()\\n\\r]+");
            for(String word : splitted) {
                String wordTrimmed = word.trim();
                if (wordTrimmed.length()>minChars && !wordlist.contains(wordTrimmed)) {
                    wordlist.add(wordTrimmed);
                }
            }
        }

        //write
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("inciwordlist.txt"));
            for(String word : wordlist) {
                writer.write(word);
                writer.newLine();
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
