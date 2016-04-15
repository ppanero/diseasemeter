package com.diseasemeter.data_colector.common;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

/**
 * FileSystem utilities.
 */
public class UtilsFS {


    private static Logger log = Logger.getLogger(UtilsFS.class);

    public static boolean saveFile(String outputDir, String filename, Set<String> data){
        Configuration confHDFS = new Configuration(true);
        OutputStream os = null;
        BufferedWriter br = null;
        try {
            FileSystem fSystem = FileSystem.get(confHDFS);
            if (!fSystem.exists(new Path(outputDir))) {
                fSystem.mkdirs(new Path(outputDir));
            }
            outputDir = preparePath(outputDir, false);
            log.debug("Saving data to: " + outputDir + "/" + filename);
            Path file = new Path(outputDir.concat(filename).concat("_").concat(
                    String.valueOf(Calendar.getInstance().getTimeInMillis())));
            os = fSystem.create(file);
            br = new BufferedWriter( new OutputStreamWriter( os, "UTF-8" ) );

            Iterator<String> it = data.iterator();
            while(it.hasNext()){
                String line = it.next().concat("\n");
                br.write(line);
            }
            br.close();
            os.close();
        } catch (IOException e) {
            log.error("Error saving file to destination: ".concat(outputDir));
            return false;
        }
        finally {
            if(os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("Error closing OutputStream to " + outputDir + "/" + filename);
                }
            }
            if(br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    log.error("Error closing BufferedWriter to " + outputDir + "/" + filename);
                }
            }
        }
        confHDFS.clear();
        return true;
    }

    /**
     * Moves all files from a directory, oldDir, to newDir adding them the markdown and filetype.
     * Files will only be moved if the do not contain any of the avoided strings of characters, being
     * this comparison character sensitive (capital letters are not equal to non-capital letters).
     * @param oldDir
     * @param newDir
     */
    public static void MoveAndDelete(String oldDir, String newDir, String markdown, String fileType,
                                     List<String> avoided, boolean deleteOldDir){
        Configuration confHDFS = new Configuration(true);
        try {
            FileSystem fSystem = FileSystem.get(confHDFS);
            if (!fSystem.exists(new Path(newDir))) {
                fSystem.mkdirs(new Path(newDir));
            }
            newDir = preparePath(newDir, false);
            log.debug("Moving files...");
            FileStatus[] fileStatus = fSystem.listStatus(new Path(oldDir));
            for (FileStatus fs : fileStatus) {
                String filename = fs.getPath().getName();
                log.debug("File to move: ".concat(filename));
                if (!isAvoided(filename ,avoided) && ! isHidden(filename)) {
                    String destination = newDir.concat(MACRO.PATH_SEPARATOR)
                            .concat(markdown).concat(MACRO.UNDERSCORE)
                            .concat(filename);
                    if(!fileType.isEmpty())
                        destination = destination.concat(MACRO.UNDERSCORE).concat(MACRO.DOT).concat(fileType);

                    fSystem.rename(fs.getPath(), new Path(destination));
                    log.debug("File correctly moved to: ".concat(destination));
                }
            }
            if(deleteOldDir) {
                fSystem.delete(new Path(oldDir), true);
                log.debug("Deleting old directory: ".concat(oldDir));
            }
        } catch (Exception e) {
            log.error("Error moving files to destination: ".concat(newDir));
        }
        confHDFS.clear();
    }

    /**
     * Searches recursively the given directory and returs a list of files without including subdirectories.
     * Filenames are given as its absolute path so they can be accessed.
     * @param pathToDir
     * @return List of files in the directory and its subdirectories.
     */
    public static List<String> getFilenamesFromDir(String pathToDir, boolean recursive){
        List<String> files = new ArrayList<String>();
        Configuration confHDFS = new Configuration(true);

        try {
            FileSystem fSystem = FileSystem.get(confHDFS);
            FileStatus[] fileStatus = fSystem.listStatus(new Path(pathToDir));
            for (FileStatus fs : fileStatus) {
                boolean hidden = fs.getPath().getName().substring(0, 1).equals(MACRO.HIDDEN_FILES_CHARACTER);
                if(!hidden){
                    if (fs.isDirectory()) {
                        if(recursive){
                            List<String> subList = getFilenamesFromDir(fs.getPath().toString(), true);
                            for(int i=0; i< subList.size(); ++i){
                                files.add(subList.get(i));
                            }
                        }
                    } else {
                        files.add(fs.getPath().toString().replaceAll("file:", MACRO.EMPTY));
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error getting the list of file in: ".concat(pathToDir));
        }
        confHDFS.clear();

        return files;
    }

    public static String preparePath(String path, boolean deleteLastSeparator){
        String preparedPath = path;
        if(preparedPath.lastIndexOf(MACRO.PATH_SEPARATOR) == preparedPath.length()-1){
            if(deleteLastSeparator) {
                preparedPath = preparedPath.substring(0, preparedPath.length() - 1);
            }
        }
        else if(!deleteLastSeparator){
            preparedPath = preparedPath.concat(MACRO.PATH_SEPARATOR);
        }
        return preparedPath;
    }

    public static boolean cleanDirectory(String dirPath){
        Configuration confHDFS = new Configuration(true);
        try {
            FileSystem fSystem = FileSystem.get(confHDFS);
            if (fSystem.exists(new Path(dirPath))) {
                    fSystem.delete(new Path(dirPath), true);
                    log.debug("Directory deleted successfully: ".concat(dirPath));
            }
        } catch (Exception e) {
            log.error("Error moving files to destination: ".concat(dirPath));
            return false;
        }
        confHDFS.clear();
        return true;
    }

    private static boolean isAvoided(String name, List<String> avoided) {
        for(String toAvoid : avoided){
            if(name.contains(toAvoid)){
                return true;
            }
        }
        return false;
    }

    private static boolean isHidden(String file){
        return file.substring(0, 1).equals(MACRO.HIDDEN_FILES_CHARACTER);
    }

    /**
     * It calculates the path for the temporary directory from the given one. Meaning that the last part of the path
     * will be replaced by "tmp". For example tmpDir(/output/path/, tmp) will return /output/tmp/.
     * @param outputDir
     * @return
     */
    public static String tmpDir(String outputDir, String tmpDir) {
        String[] parts = outputDir.split(MACRO.PATH_SEPARATOR);
        int i=parts.length-1;
        String toReplace = "";
        while (i >= 0 && toReplace != null && toReplace.equals("")){
            toReplace = parts[i];
        }
        return outputDir.replace(toReplace, tmpDir);
    }
}
