package ap.lab02;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/** Project identified by a unique code and a type; at most one student assigned. */
public final class Project {
    private final String code;
    private final ProjectType type;
    private Teacher proposer;
    private Student assigned; // null or the unique assignee

    public Project(String code, ProjectType type, Teacher proposer){
        this.code = Objects.requireNonNull(code);
        this.type = Objects.requireNonNull(type);
        this.proposer = proposer;
    }

    public String getCode(){ return code; }
    public ProjectType getType(){ return type; }
    public Teacher getProposer(){ return proposer; }
    public void setProposer(Teacher t){ proposer = t; }
    public Student getAssigned(){ return assigned; }
    public void setAssigned(Student s){ assigned = s; }

    @Override public @NotNull String toString(){
        return "Project("+code+", "+type+(proposer!=null?(", by "+proposer.getName()):"")+
                (assigned!=null?(", assigned="+assigned.getName()):"")+")";
    }

    /** Projects equal if same code. */
    @Override public boolean equals(Object o){
        if(this==o) return true; if(!(o instanceof Project p)) return false;
        return code.equals(p.code);
    }
    @Override public int hashCode(){ return code.hashCode(); }
}
