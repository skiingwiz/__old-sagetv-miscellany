package main;

import java.io.File;

public class FailedRenameException extends Exception {

    public FailedRenameException(File fromFile, File toFile, String msg) {
        super(msg);
    }

}
