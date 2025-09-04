package ap.lab05.shell;

import ap.lab05.core.RepoException;

public interface Command {
    String name();
    String help();
    void run(Shell ctx, String[] args) throws RepoException;
}
