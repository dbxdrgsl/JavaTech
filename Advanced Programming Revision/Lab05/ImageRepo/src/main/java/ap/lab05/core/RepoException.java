package ap.lab05.core;

/** Base checked exception for repository operations. */
public class RepoException extends Exception {
    public RepoException(String msg) { super(msg); }
    public RepoException(String msg, Throwable cause) { super(msg, cause); }
}
