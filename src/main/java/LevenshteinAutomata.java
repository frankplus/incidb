import com.github.liblevenshtein.collection.dictionary.SortedDawg;
import com.github.liblevenshtein.serialization.PlainTextSerializer;
import com.github.liblevenshtein.serialization.Serializer;
import com.github.liblevenshtein.transducer.Algorithm;
import com.github.liblevenshtein.transducer.Candidate;
import com.github.liblevenshtein.transducer.ITransducer;
import com.github.liblevenshtein.transducer.factory.TransducerBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class LevenshteinAutomata {
    public static void main(String[] args){
        final SortedDawg dictionary;
        File dictionaryFile = new File("inciwordlist.txt");
        InputStream inputStream;

        try {
            inputStream = new FileInputStream(dictionaryFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        final Serializer serializer = new PlainTextSerializer(false);
        try {
            dictionary = serializer.deserialize(SortedDawg.class, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        final ITransducer<Candidate> transducer = new TransducerBuilder()
                .dictionary(dictionary)
                .algorithm(Algorithm.TRANSPOSITION)
                .defaultMaxDistance(4)
                .includeDistance(true)
                .build();

        for (final String queryTerm : new String[] {"AURARTIOLLA", "SCORHYLLUM", "TETRANETHYUHEXADEFENYLCYRTEINE"}) {
            System.out.println(
                    "+-------------------------------------------------------------------------------");
            System.out.printf("| Spelling Candidates for Query Term: \"%s\"%n", queryTerm);
            System.out.println(
                    "+-------------------------------------------------------------------------------");
            for (final Candidate candidate : transducer.transduce(queryTerm)) {
                System.out.printf("| d(\"%s\", \"%s\") = [%d]%n",
                        queryTerm,
                        candidate.term(),
                        candidate.distance());
            }
        }
    }
}
