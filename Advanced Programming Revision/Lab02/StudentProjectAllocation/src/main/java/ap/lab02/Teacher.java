package ap.lab02;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Arrays;

/** Teacher proposes projects. */
public final class Teacher extends Person {
    private Project[] proposed;
    public Teacher(String name, LocalDate dob, Project[] proposed){
        super(name,dob);
        this.proposed = proposed!=null ? proposed.clone() : new Project[0];
    }
    public Project[] getProposed(){ return proposed.clone(); }
    public void setProposed(Project[] p){ proposed = p!=null ? p.clone() : new Project[0]; }
    @Override public @NotNull String toString(){ return super.toString()+" proposed="+ Arrays.toString(proposed); }
}
