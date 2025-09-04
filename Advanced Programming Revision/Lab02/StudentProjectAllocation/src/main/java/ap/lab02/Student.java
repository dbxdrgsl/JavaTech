package ap.lab02;

import java.time.LocalDate;
import java.util.Arrays;

/** Student with regNo and acceptable projects (array). */
public final class Student extends Person {
    private final String registrationNumber;
    private Project[] acceptable; // no collections
    public Student(String name, LocalDate dob, String regNo, Project[] acceptable){
        super(name,dob);
        this.registrationNumber = regNo;
        this.acceptable = acceptable!=null ? acceptable.clone() : new Project[0];
    }
    public String getRegistrationNumber(){ return registrationNumber; }
    public Project[] getAcceptable(){ return acceptable.clone(); }
    public void setAcceptable(Project[] a){ acceptable = a!=null ? a.clone() : new Project[0]; }

    @org.jetbrains.annotations.NotNull
    @Override public String toString(){
        return super.toString()+" regNo="+registrationNumber+" prefs="+ Arrays.toString(acceptable);
    }

    /** Students equal if Person equals OR same regNo. */
    @Override public boolean equals(Object o){
        if(this==o) return true;
        if(!(o instanceof Student s)) return false;
        return super.equals(o) || registrationNumber.equals(s.registrationNumber);
    }
    @Override public int hashCode(){ return registrationNumber.hashCode()*31 + super.hashCode(); }
}
