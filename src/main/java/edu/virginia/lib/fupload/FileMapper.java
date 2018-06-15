package edu.virginia.lib.fupload;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import javax.servlet.http.HttpServlet;
import java.io.File;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Maps a filename to a destination path and the reverse.  The current implementation
 * extracts numeric values from the end of the pre-extension filename and slots files
 * into folders to avoid having an overwhealming number of files in a single directory.
 */
public class FileMapper implements Iterable<File>, IOFileFilter {

    final File baseDirectory;

    final Pattern pattern = Pattern.compile(".*[^\\d](\\d+)\\.[^\\.]+");

    public FileMapper(final HttpServlet servlet) {
        this(getUploadPath(servlet));
    }

    public FileMapper(final String baseDirectoryName) {
        baseDirectory = new File(baseDirectoryName);
    }

    private static String getUploadPath(final HttpServlet servlet) {
        final String config_dir = servlet.getInitParameter("upload_dir");
        String uploadPath = "";
        if ((config_dir != null) && (!config_dir.equals(""))) {
            uploadPath = config_dir;
        } else {
            uploadPath = servlet.getServletContext().getRealPath("")
                    + File.separator + "upload";
        }
        return uploadPath;
    }

    /**
     * Gets the location on disk for the given filename (and creates any parent directories).
     */
    public File getFileLocation(final String filename) {
        Matcher m = pattern.matcher(filename);
        if (m.matches()) {
            final String numericPortion = m.group(1);
            File result = baseDirectory;
            int offset = 0;
            while (offset < numericPortion.length()) {
                final int remainingDigits = numericPortion.length() - offset;
                if (remainingDigits > 1) {
                    result = new File(result, numericPortion.substring(offset, offset + 2));
                    offset += 2;
                } else if (remainingDigits == 1) {
                    result = new File(result, numericPortion.substring(offset));
                    offset ++;
                }
            }
            result = new File(result, filename);
            result.getParentFile().mkdirs();
            return result;
        } else {
            // doesn't end in a number, so just put it in the root listing
            return new File(baseDirectory, filename);
        }
    }

    public int redistributeFromRoot() {
        int count = 0;
        for (File f : baseDirectory.listFiles()) {
            File rightLocation = getFileLocation(f.getName());
            if (!rightLocation.equals(f)) {
                if (f.renameTo(rightLocation)) {
                    count ++;
                }
            }
        }
        return count;
    }


    @Override
    public Iterator<File> iterator() {
        return FileUtils.iterateFiles(baseDirectory, this, TrueFileFilter.INSTANCE);
    }

    @Override
    public boolean accept(File file) {
        return getFileLocation(file.getName()).equals(file);
    }

    @Override
    public boolean accept(File dir, String name) {
        return accept(new File(dir, name));
    }
}
