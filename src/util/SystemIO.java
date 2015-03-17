package util;

import java.io.*;

/**
 *
 * @author Diego Barrionuevo
 * @verison 1.1
 */
public class SystemIO {

    public static final String PATH_SEPARATOR_WINDOWS = "\\";

    /**
     * Reads and returns the content of a text file.
     *
     * @param file The file to be read.
     * @return Text content of the file.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String readFile(File file) throws FileNotFoundException, IOException {
        StringBuilder content = new StringBuilder();
        BufferedReader reader;
        try (FileReader fr = new FileReader(file)) {
            reader = new BufferedReader(fr);
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        reader.close();
        return content.toString();
    }
    
    /**
     * Reads and returns the content of a text file.
     *
     * @param pathFile Path or name of the file to be read.
     * @return Text content of the file.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String readFile(String pathFile) throws FileNotFoundException, IOException {
        return SystemIO.readFile(new File(pathFile));
    }

    /**
     * Writes content in a text file. Overwrite everything in the file.
     *
     * @param pathName Path or name of the file to be written.
     * @param content Text content to be written.
     * @throws IOException
     */
    public static void writeFile(String pathName, String content) throws IOException {
        SystemIO.writeInFile(pathName, content, false);
    }

    /**
     * Append text content in a text file.
     *
     * @param pathName Path or name of the file which the text content will be
     * appended.
     * @param content Text content to be appended.
     * @throws IOException
     */
    public static void appendFile(String pathName, String content) throws IOException {
        SystemIO.writeInFile(pathName, content, true);
    }

    private static void writeInFile(String pathName, String content, boolean append) throws IOException {
        FileWriter w = new FileWriter(new File(pathName), append);
        try (BufferedWriter bw = new BufferedWriter(w); PrintWriter writer = new PrintWriter(bw)) {
            writer.write("");
            writer.append(content);
        }
    }

    @Deprecated
    public static String readBinaryFile(String pathFile) throws FileNotFoundException, IOException {
        StringBuilder content = new StringBuilder();
        FileInputStream fileInput = new FileInputStream(pathFile);
        try (BufferedInputStream bis = new BufferedInputStream(fileInput)) {
            byte[] array = new byte[1024];
            int bytesRead = bis.read(array);
            while (bytesRead > 0) {
                bytesRead = bis.read(array);
            }
        }
        return content.toString();
    }

    @Deprecated
    public static String writeBinaryFile(String pathFile) throws FileNotFoundException, IOException {
        StringBuilder content = new StringBuilder();
        FileInputStream fileInput = new FileInputStream(pathFile);
        try (BufferedInputStream bis = new BufferedInputStream(fileInput)) {
            byte[] array = new byte[1024];
            int bytesRead = bis.read(array);
            while (bytesRead > 0) {
                bytesRead = bis.read(array);
            }
        }
        return content.toString();
    }

    /**
     * Reads and returns the content of a text file.
     *
     * @param pathFile Path or name of the file to be created.
     * @param webUrl URL of the file to be downloaded.
     * @return Text content of the file.
     * @throws java.net.MalformedURLException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static File downloadFile(String webUrl, String pathFile) throws java.net.MalformedURLException, FileNotFoundException, IOException {
        java.net.URL url = new java.net.URL(webUrl);
        java.net.URLConnection urlCon = url.openConnection();
        FileOutputStream fos;
        try (InputStream is = urlCon.getInputStream()) {
            int len;
            fos = new FileOutputStream(pathFile);
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        }
        fos.close();
        return new File(pathFile);
    }

    /**
     * Copies one file.
     *
     * @param originalPathName Path or name of the file to be copied.
     * @param copiedPathName Path or name of the desired copy.
     * @throws java.io.FileNotFoundException
     */
    public static void copyFile(String originalPathName, String copiedPathName) throws FileNotFoundException, IOException {
        File originalFile = new File(originalPathName);
        File copiedFile = new File(copiedPathName);
        InputStream in = new FileInputStream(originalFile);
        OutputStream out = new FileOutputStream(copiedFile);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
    }

    /**
     * Moves one file. If the destiny directory does not exists, it creates it.
     *
     * @param originalPathName Path or name of the file to be moved.
     * @param toDirectoryPath Path of the destiny direcotry.
     * @return True if the file were moved succesfully.
     */
    public static boolean moveFile(String originalPathName, String toDirectoryPath) {
        File originalFile = new File(originalPathName);
        if (!originalFile.isFile()) {
            return false;
        }
        File toDir = SystemIO.createDirectoryIfNotExists(toDirectoryPath);
        String fileName = originalFile.getName();
        File movedFile = new File(toDir.getAbsolutePath() + PATH_SEPARATOR_WINDOWS + fileName);
        if (!movedFile.exists()) {
            originalFile.renameTo(movedFile);
        }
        return true;
    }

    /**
     * Moves files from a directory to another. If the destiny directory does
     * not exists, it creates it.
     *
     * @param fromDirectoryPath Path of the directory where the files to be
     * moved are.
     * @param toDirectoryPath Path of the directory where the files will be.
     * @return True if all the files were moved succesfully.
     */
    public static boolean moveFiles(String fromDirectoryPath, String toDirectoryPath) {
        File fromDir = new File(fromDirectoryPath);
        if (!fromDir.isDirectory()) {
            return false;
        }
        boolean allFilesMoved = true;
        File[] originalFiles = fromDir.listFiles();
        for (File originalFile : originalFiles) {
            allFilesMoved &= SystemIO.moveFile(originalFile.getAbsolutePath(), toDirectoryPath);
        }
        return allFilesMoved;
    }

//    public static boolean renameFile(String originalPathName, String finalNameFile) {
//        File originalFile = new File(originalPathName);
//        if (!originalFile.isFile()) {
//            return false;
//        }
//        String fileExtension;
//        String fileName;
//        File movedFile;
//        for (File originalFile : originalFiles) {
//            SystemIO.moveFile(originalFile.getAbsolutePath(), toDirectoryPath);
//            if (originalFile.isFile()) {
//                fileName = originalFile.getName();
//                fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
//                movedFile = new File(toDir.getAbsolutePath() + PATH_SEPARATOR_WINDOWS + fileName);
//                if (!movedFile.exists()) {
//                    originalFile.renameTo(movedFile);
//                }
//            }
//        }
//        return true;
//    }
//    
//    public static boolean renameFile(String fromDirectoryPath,String finalNameFile, String toDirectoryPath) {
//        File fromDir = new File(fromDirectoryPath);
//        if (!fromDir.isDirectory()) {
//            return false;
//        }
//        File[] originalFiles = fromDir.listFiles();
//        File toDir = SystemIO.createDirectoryIfNotExists(toDirectoryPath);
//        String fileExtension;
//        String fileName;
//        File movedFile;
//        for (File originalFile : originalFiles) {
//            SystemIO.moveFile(originalFile.getAbsolutePath(), toDirectoryPath);
//            if (originalFile.isFile()) {
//                fileName = originalFile.getName();
//                fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
//                movedFile = new File(toDir.getAbsolutePath() + PATH_SEPARATOR_WINDOWS + fileName);
//                if (!movedFile.exists()) {
//                    originalFile.renameTo(movedFile);
//                }
//            }
//        }
//        return true;
//    }
    private static File createDirectoryIfNotExists(String pathDirectory) {
        File directory = new File(pathDirectory);
        if (directory.isDirectory()) {
            return directory;
        } else {
            if (!directory.exists() || (directory.exists() && !directory.isDirectory())) {
                if (!directory.mkdir()) {
                    return null;
                }
            }
        }
        return directory;
    }

    /**
     * Counts the lines in a file, such as text or binary, given that it counts
     * by the '\n' charcode, so depeding on the charcodes the files is, it will
     * return one o another number.
     *
     * @param filename Path of the file to count its lines.
     * @return The count of lines
     * @throws IOException
     */
    public static int countLines(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }

}
