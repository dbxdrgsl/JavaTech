package ro.uaic.dbxdrgsl.quickgrade.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeEvent implements Serializable {
    @NotBlank(message = "Student code is required")
    private String studentCode;
    
    @NotBlank(message = "Course code is required")
    private String courseCode;
    
    @NotNull(message = "Grade is required")
    @DecimalMin(value = "0.0", message = "Grade must be at least 0.0")
    @DecimalMax(value = "10.0", message = "Grade must not exceed 10.0")
    private Double grade;
}
