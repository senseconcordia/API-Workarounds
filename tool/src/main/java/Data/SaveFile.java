package Data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SaveFile {
    private static SaveFile single_instance=null;

    private Path saveFilePath;
    private String fileName;

    private SaveFile(){
        // Exists only to defeat instantiation as part of Singleton pattern.
    }

    public static SaveFile getInstance(){
        if (single_instance == null){
            single_instance = new SaveFile();
        }
        return single_instance;
    }

    public void createNewSaveFile(Path absFilePath){
        createFileName();
        this.saveFilePath = Paths.get(absFilePath.toString(), fileName);
    }

    private void createFileName(){
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss"));
        StringBuilder sb = new StringBuilder("piperPattern_results_");
        sb.append(timeStamp);
        sb.append(".csv");
        fileName = sb.toString();
    }

    public void writeToSaveFile(String text) {
        File file = new File(saveFilePath.toString());
        FileWriter fr = null;
        try {
            // Below constructor argument decides whether to append or override
            fr = new FileWriter(file, true);
            fr.write(text);
            fr.write("\n");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
