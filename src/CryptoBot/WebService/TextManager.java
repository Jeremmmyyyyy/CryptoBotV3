package CryptoBot.WebService;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public final class TextManager {

    private static final DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final LocalDateTime now = LocalDateTime.now();
    private static String fileId;
    private static File file;

    private TextManager(){}

    public static void createFile(String fileName){
        file = new File(formatDate.format(now)  + "_" +fileName+ ".txt");
        fileId = formatDate.format(now)  + "_" +fileName+ ".txt";
        if (! (checkExistingFile(fileId)))
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToFile(String fileName, String textToWrite){
        try {
            file = new File(fileName);
            PrintWriter pw = new PrintWriter(new FileOutputStream(file,true));
            pw.append(textToWrite).append("\n");
            pw.close();

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static ArrayList<String> readFromFileToArray(String fileName){
        ArrayList<String> text = new ArrayList<>();
        String line;
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){
            while ((line = reader.readLine()) != null){
                text.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    public static String[][] readFromFileToTable(String fileName){
        if(TextManager.checkExistingFile(fileName)){
            ArrayList<String> data = TextManager.readFromFileToArray(fileName);
            String[][] dataTab = new String[data.size()][];
            for (int i = 0; i < data.size(); i++) {
                dataTab[i] = data.get(i).split(" ");
            }
            return dataTab;
        }else{
           return new String[0][0];
        }
    }

    public static boolean checkExistingFile(String fileName){
        file = new File(fileName);
        return file.exists();
    }

    public static void delete(String fileName){
        file = new File(fileName);
        file.delete();
    }
}
