package ro.uaic.dbxdrgsl.quickgrade.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeEvent implements Serializable {
    private String studentCode;
    private String courseCode;
    private Double grade;
}
