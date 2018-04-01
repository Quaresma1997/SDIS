package utils;

public class Utils{
    public static final int BODY_SIZE = 64000;
    public static final int HEADER_SIZE = 256;
    public static final int CHUNK_MAX_SIZE = 64256;

    public final static char CR = (char) 0x0D;
    public final static char LF = (char) 0x0A;

    public final static int DELETE_MSGS_NUM = 3;
    public final static int NUM_TRANSMISSIONS = 5;

    public final static int NUM_THREADS_POOL = 5;

    public final static long MAX_DISK_REQUIRED_SPACE = 8000;

    public final static String CRLF = "" + CR + LF;

    public static final String TMP_CHUNKS = "tmp_chunks";
    public static final String TMP_FILES_RESTORED = "tmp_restores/";
    public static final String FILES = "files_examples/";

    
}