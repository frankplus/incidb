import com.github.liblevenshtein.collection.dictionary.SortedDawg;
import com.github.liblevenshtein.serialization.PlainTextSerializer;
import com.github.liblevenshtein.serialization.ProtobufSerializer;
import com.github.liblevenshtein.serialization.Serializer;
import com.github.liblevenshtein.transducer.Algorithm;
import com.github.liblevenshtein.transducer.Candidate;
import com.github.liblevenshtein.transducer.ITransducer;
import com.github.liblevenshtein.transducer.factory.TransducerBuilder;

import java.io.*;

/**
 * Testing levenshtein automata library on inci database
 * For more information: https://github.com/universal-automata/liblevenshtein-java/blob/gh-pages/docs/wiki/3.0.0/usage.md
 * @Author Francesco Pham
 */
public class LevenshteinAutomata {
    public static void main(String[] args){

        //opening dictionary
        final SortedDawg dictionary;
        File dictionaryFile = new File("inciwordlist.txt");

        try (final InputStream inputStream = new FileInputStream(dictionaryFile);) {
            final Serializer serializer = new PlainTextSerializer(false);
            dictionary = serializer.deserialize(SortedDawg.class, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        //initializing transducer
        final int defMaxDist = 8; //Default maximum number of errors tolerated between each spelling candidate and the query term.
        final ITransducer<Candidate> transducer = new TransducerBuilder()
                .dictionary(dictionary)
                .algorithm(Algorithm.MERGE_AND_SPLIT) //Using MERGE_AND_SPLIT because it's better for OCR
                .defaultMaxDistance(defMaxDist) // this value should be changed
                .includeDistance(true)
                .build();

        //serialize dictionary to a format that's easy to read later
        File outputFile = new File("serialized_dictionary.bytes");
        try (final OutputStream stream = new FileOutputStream(outputFile)) {
            final Serializer serializer = new ProtobufSerializer();
            serializer.serialize(dictionary, stream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //test with some words
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
