package com.heb.hebcodingchallenge.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ImageUtils {

    public static Path getApplicationLocationPath() throws FileNotFoundException {
        return Paths.get(new File("").getAbsolutePath());
    }

    public static String getSha256(byte[] byteArray) throws NoSuchAlgorithmException {
        return new DigestUtils("SHA3-256").digestAsHex(byteArray);
    }


}
