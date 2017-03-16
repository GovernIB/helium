package net.consellemallorca.helium.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;


public class CheckFileHash {
	private static final Logger LOG = Logger.getLogger(CheckFileHash.class);
    private HashType typeOfHash = null;
    private String expectedFileHash = null;
    private byte[] fileToCheck = null;
 
    /**
     * The File to perform a Hash check upon
     *
     * @param fileToCheck
     * @throws FileNotFoundException
     */
    public void fileToCheck(File fileToCheck) throws FileNotFoundException, IOException {
        if (!fileToCheck.exists()) throw new FileNotFoundException(fileToCheck + " does not exist!");
 
        this.fileToCheck = IOUtils.toByteArray(new FileInputStream(fileToCheck));
    }
    
    public void fileToCheck(byte[] fileToCheck) throws FileNotFoundException {
        if (fileToCheck == null) throw new FileNotFoundException(fileToCheck + " does not exist!");
 
        this.fileToCheck = fileToCheck;
    }
 
    /**
     * Hash details used to perform the Hash check
     *
     * @param hash
     * @param hashType
     */
    public void hashDetails(String hash, HashType hashType) {
        this.expectedFileHash = hash;
        this.typeOfHash = hashType;
    }
 
    /**
     * Performs a expectedFileHash check on a File.
     *
     * @return
     * @throws IOException
     */
    public boolean hasAValidHash() throws IOException {
        if (this.fileToCheck == null) throw new FileNotFoundException("File to check has not been set!");
        if (this.expectedFileHash == null || this.typeOfHash == null) throw new NullPointerException("Hash details have not been set!");
 
        String actualFileHash = "";
        boolean isHashValid = false;
 
//        byte[] file = IOUtils.toByteArray(new FileInputStream(this.fileToCheck));
        
        switch (this.typeOfHash) {
            case MD5:
                actualFileHash = DigestUtils.md5Hex(this.fileToCheck);
                if (this.expectedFileHash.equals(actualFileHash)) isHashValid = true;
                break;
            case SHA1:
                actualFileHash = DigestUtils.shaHex(this.fileToCheck);
                if (this.expectedFileHash.equals(actualFileHash)) isHashValid = true;
                break;
        }
 
        LOG.info("Expected Hash = '" + this.expectedFileHash + "'");
        LOG.info("Actual Hash = '" + actualFileHash + "'");
 
        return isHashValid;
    }
}
