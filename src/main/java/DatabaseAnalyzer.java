import com.opencsv.CSVReader;
import java.io.*;
import java.util.ArrayList;

/**
 * This class aims to generate a list of different words from inci database
 * @Author Francesco Pham
 */
public class DatabaseAnalyzer {
    public static void main(String args[]){

        //open file db
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

        //generate word list
        int longestWord = 0;
        final int minChars = 3; //ignoring words with less than or equal to minChars caracters
        ArrayList<String> wordlist = new ArrayList<String>();
        for(Ingredient ingredient : listIngredients){
            String[] splitted = ingredient.getInciName().split("[^a-zA-Z0-9-]+");
            for(String word : splitted) {
                String wordTrimmed = word.trim();
                if (wordTrimmed.length()>=minChars && !wordlist.contains(wordTrimmed)) {
                    wordlist.add(wordTrimmed);

                    if(!wordTrimmed.matches("^[A-Z0-9-]+$")){
                        System.out.println("found a non character or number: "+wordTrimmed);
                    }

                    if(wordTrimmed.length()>longestWord) longestWord = wordTrimmed.length();
                }
            }
        }
        System.out.println("longest word "+longestWord+" characters" );

        //write to file
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
