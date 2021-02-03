import java.util.*;
import edu.duke.*;


public class VigenereBreaker {
    private String sliceString(String message, int whichSlice, int totalSlices) {
        StringBuilder msg = new StringBuilder(message), rtn = new StringBuilder();
        for (int i = whichSlice; i < msg.length(); i += totalSlices) {
            rtn.append(msg.charAt(i));
        }
        return rtn.toString();
    }

    private int[] tryKeyLength(String encrypted, int klength, char ch) {
        int[] key = new int[klength];
        for (int i = 0; i < klength; i++) {
            int shift = 0, maxChCount = 0;;
            String splice = sliceString(encrypted, i, klength);
            for (int j = 1; j <= 26; j++) {
                String decrypted = new CaesarCipher(j).decrypt(splice);
                int chCount = 0;
                StringBuilder sb = new StringBuilder(decrypted);
                for (int l = 0; l < sb.length(); l++) {
                    if (sb.charAt(l) == Character.toLowerCase(ch)) {chCount++;}
                }
                if (chCount > maxChCount) {
                    maxChCount = chCount;
                    shift = j;
                }
            }
            key[i] = shift;
        }
        return key;
    }
    
    private int[] breakForLanguage(String encrypted, HashSet<String> dictionary) {
        ArrayList<int[]> keys = new ArrayList<int[]>();
        int maxCount = 0, i = -1;
        char ch = mostCommonCharIn(dictionary);
        for (int j = 0; j < 100; j++) {
            keys.add(tryKeyLength(encrypted, j + 1, ch));
            String decryped = new VigenereCipher(keys.get(j)).decrypt(encrypted);
            int count = countWords(decryped, dictionary);
            if (count > maxCount) {
                maxCount = count;
                i = j;
            }
        }
        return keys.get(i);
    }
    
    public void breakForAllLangs(String encrypted, HashMap<String, HashSet<String>> langs) {
        int maxCount = 0;
        for (Map.Entry<String, HashSet<String>> entry : langs.entrySet()) {
            int[] key = breakForLanguage(encrypted, entry.getValue());
            String decryped = new VigenereCipher(key).decrypt(encrypted);
            int count = countWords(decryped, entry.getValue());
            if (count > maxCount) {
                maxCount = count;
                System.out.println("Lang: " + entry.getKey() + " with max count: " + count + "\n");
                System.out.println(decryped.substring(0, 150));
            } 
        }
    }
    
    
    private HashSet<String> readDictionary(String path) {
        HashSet<String> hs = new HashSet<String>();
        FileResource fr = new FileResource(path);
        for (String line : fr.lines()) {
            hs.add(line.toLowerCase());
        }
        return hs;
    }
    
    private int countWords(String message, HashSet<String> dictionary) {
        int count = 0;
        String[] arr = message.split("\\W");
        for (int i = 0; i < arr.length; i++) {
            if (dictionary.contains(arr[i].toLowerCase())) {count++;}
        }
        return count;
    }
    
    private char mostCommonCharIn(HashSet<String> dictionary) {
       HashMap<Character, Integer> hm = new HashMap<Character, Integer>();
       for (String word : dictionary) {
           StringBuilder sb = new StringBuilder(word.toLowerCase());
           for (int i = 0; i < sb.length(); i++) {
               char ch = sb.charAt(i);
               if (hm.containsKey(ch) && ch != ' ') {
                   hm.put(ch, hm.get(ch) + 1);
               } else {
                   hm.put(ch, 1);
               }
           }
       }
       char maxCh = ' ';
       int maxChCount = 0;
       for (Map.Entry<Character, Integer> entry : hm.entrySet()) {
           if (entry.getValue() > maxChCount) {
               maxCh = entry.getKey(); 
               maxChCount = entry.getValue();
           }
       }
       return maxCh;
    }

    public void breakVigenere () {
        String encrypted = new FileResource().asString();
        HashMap<String, HashSet<String>> langs = new HashMap<String, HashSet<String>>();
        langs.put("Danish", readDictionary("dictionaries/" + "Danish"));
        langs.put("Dutch", readDictionary("dictionaries/" + "Dutch"));        
        langs.put("English", readDictionary("dictionaries/" + "English"));
        langs.put("French", readDictionary("dictionaries/" + "French"));      
        langs.put("German", readDictionary("dictionaries/" + "German"));
        langs.put("Italian", readDictionary("dictionaries/" +  "Italian"));      
        langs.put("Portuguese", readDictionary("dictionaries/" + "Portuguese"));
        langs.put("Spanish", readDictionary("dictionaries/" + "Spanish"));
        breakForAllLangs(encrypted, langs);
    }
    
}
