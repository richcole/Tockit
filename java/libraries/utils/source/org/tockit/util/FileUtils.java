/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Some static helper functions for java.io.File.
 */
public abstract class FileUtils {
    /**
     * Retrieves a list of all parent files.
     */
    public static File[] getAncenstors(File file) {
        List<File> result = new ArrayList<File>();
        File currentFile = file.getParentFile();
        while(currentFile.getParentFile() != null) {
            result.add(0,currentFile);
            currentFile = currentFile.getParentFile();
        }
        return result.toArray(new File[result.size()]);
    }

    /**
     * Tries to find a relative path between the files, returns an absolute path
     * it it fails.
     * 
     * @param from  the location the path should be relative to
     * @param to    the target location
     * @return a relative path between the two paramters if possible, to.getAbsolutePath() otherwise
     */
    public static String findRelativePath(File from, File to) {
        // comparison makes sense only on canonical files, so we try to get those,
        // otherwise we revert to absolute path
        try {
            // the "canonical" path of a File doesn't seem to be that canonical,
            // at least in some situations under Windows it may or may not start
            // with "My Computer" (or its equivalents). Creating a new file from
            // the path seems to get rid of that
            File[] csxParents = getAncenstors(new File(from.getAbsolutePath())
                    .getCanonicalFile());
            File[] dbFileParents = getAncenstors(new File(to.getAbsolutePath())
                    .getCanonicalFile());
            int i = 0;
            while (i < csxParents.length && i < dbFileParents.length
                    && csxParents[i].equals(dbFileParents[i])) {
                i++;
            }
            if (i != 0) {
                String result = "";
                for (int j = 0; j < csxParents.length - i; j++) {
                    result += ".." + File.separator;
                }
                for (int j = i; j < dbFileParents.length; j++) {
                    result += dbFileParents[j].getName()
                            + File.separator;
                }
                result += to.getName();
                return result;
            }
            return to.getAbsolutePath();
        } catch (IOException e) {
            // couldn't get canonical path, we just stick with the absolute path
        }
        return to.getAbsolutePath();
    }
}
