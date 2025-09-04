package ap.lab02;

import java.time.LocalDate;
import java.util.Objects;

/** Abstract base for Student and Teacher. */
public abstract class Person {
    private final String name;
    private final LocalDate birthDate;

    protected Person(String name, LocalDate birthDate) {
        this.name = Objects.requireNonNull(name);
        this.birthDate = Objects.requireNonNull(birthDate);
    }

    public String getName() { return name; }
    public LocalDate getBirthDate() { return birthDate; }

    @Override public String toString() { return getClass().getSimpleName()+"("+name+", "+birthDate+")"; }

    /** Persons are equal if name and birthDate match (identity surrogate for lab). */
    @Override public boolean equals(Object o){
        if(this==o) return true; if(!(o instanceof Person p)) return false;
        return name.equals(p.name) && birthDate.equals(p.birthDate);
    }
    @Override public int hashCode(){ return Objects.hash(name,birthDate); }
}
