package ro.uaic.dbxdrgsl.prefschedule.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import ro.uaic.dbxdrgsl.prefschedule.dto.StudentPreferenceDTO;
import ro.uaic.dbxdrgsl.prefschedule.dto.StudentPreferenceResponseDTO;
import ro.uaic.dbxdrgsl.prefschedule.exception.ErrorResponse;
import ro.uaic.dbxdrgsl.prefschedule.exception.ResourceNotFoundException;
import ro.uaic.dbxdrgsl.prefschedule.model.StudentPreference;
import ro.uaic.dbxdrgsl.prefschedule.service.StudentPreferenceService;

import java.util.List;

/**
 * REST Controller for managing student course preferences.
 * Supports JSON and XML content negotiation, ETag-based conditional requests,
 * and is fully documented with OpenAPI/Swagger.
 */
@RestController
@RequestMapping("/api/preferences")
@Tag(name = "Student Preferences", description = "API for managing student course preferences")
public class StudentPreferenceController {
    private final StudentPreferenceService preferenceService;

    public StudentPreferenceController(StudentPreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    /**
     * Create a new student preference.
     */
    @Operation(summary = "Create a new student preference", 
               description = "Creates a preference record linking a student to a course with a rank order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Preference created successfully",
                     content = @Content(mediaType = "application/json", 
                                        schema = @Schema(implementation = StudentPreferenceResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Student or Course not found",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                 consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<StudentPreferenceResponseDTO> createPreference(
            @Valid @RequestBody StudentPreferenceDTO dto) {
        StudentPreferenceResponseDTO created = preferenceService.createPreference(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Get all student preferences.
     */
    @Operation(summary = "Get all student preferences",
               description = "Retrieves a list of all student course preferences")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
                 content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = StudentPreferenceResponseDTO.class)))
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<StudentPreferenceResponseDTO>> getAllPreferences() {
        List<StudentPreferenceResponseDTO> preferences = preferenceService.findAll();
        return ResponseEntity.ok(preferences);
    }

    /**
     * Get a preference by ID with ETag support.
     * Supports conditional requests via If-None-Match header.
     */
    @Operation(summary = "Get a preference by ID",
               description = "Retrieves a specific preference. Supports ETag-based conditional requests.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved preference"),
        @ApiResponse(responseCode = "304", description = "Not Modified - ETag matches"),
        @ApiResponse(responseCode = "404", description = "Preference not found")
    })
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<StudentPreferenceResponseDTO> getPreferenceById(
            @Parameter(description = "ID of the preference to retrieve") @PathVariable Long id,
            WebRequest request) {
        
        StudentPreference entity = preferenceService.findEntityById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StudentPreference", id));

        // Generate ETag based on version
        String etag = "\"" + entity.getVersion() + "\"";
        
        // Check If-None-Match header for conditional request
        if (request.checkNotModified(etag)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }

        // Convert entity to DTO directly to avoid second fetch
        StudentPreferenceResponseDTO dto = toResponseDTO(entity);

        return ResponseEntity.ok()
                .eTag(etag)
                .body(dto);
    }

    private StudentPreferenceResponseDTO toResponseDTO(StudentPreference preference) {
        String packName = preference.getCourse().getPack() != null 
                ? preference.getCourse().getPack().getName() 
                : null;

        return StudentPreferenceResponseDTO.builder()
                .id(preference.getId())
                .studentId(preference.getStudent().getId())
                .studentName(preference.getStudent().getName())
                .studentCode(preference.getStudent().getCode())
                .courseId(preference.getCourse().getId())
                .courseName(preference.getCourse().getName())
                .courseCode(preference.getCourse().getCode())
                .packName(packName)
                .rankOrder(preference.getRankOrder())
                .version(preference.getVersion())
                .build();
    }

    /**
     * Get preferences for a specific student.
     */
    @Operation(summary = "Get preferences by student ID",
               description = "Retrieves all preferences for a specific student, ordered by rank")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved preferences")
    @GetMapping(value = "/student/{studentId}", 
                produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<StudentPreferenceResponseDTO>> getPreferencesByStudent(
            @Parameter(description = "ID of the student") @PathVariable Long studentId) {
        List<StudentPreferenceResponseDTO> preferences = preferenceService.findByStudentId(studentId);
        return ResponseEntity.ok(preferences);
    }

    /**
     * Get preferences for a student within a specific pack.
     */
    @Operation(summary = "Get preferences by student and pack",
               description = "Retrieves preferences for a student within a specific course pack")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved preferences")
    @GetMapping(value = "/student/{studentId}/pack/{packId}",
                produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<StudentPreferenceResponseDTO>> getPreferencesByStudentAndPack(
            @Parameter(description = "ID of the student") @PathVariable Long studentId,
            @Parameter(description = "ID of the pack") @PathVariable Long packId) {
        List<StudentPreferenceResponseDTO> preferences = 
                preferenceService.findByStudentIdAndPackId(studentId, packId);
        return ResponseEntity.ok(preferences);
    }

    /**
     * Update a preference.
     */
    @Operation(summary = "Update a preference",
               description = "Updates an existing student preference")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated preference"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Preference not found")
    })
    @PutMapping(value = "/{id}",
                produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<StudentPreferenceResponseDTO> updatePreference(
            @Parameter(description = "ID of the preference to update") @PathVariable Long id,
            @Valid @RequestBody StudentPreferenceDTO dto) {
        StudentPreferenceResponseDTO updated = preferenceService.updatePreference(id, dto);
        
        // Generate new ETag after update
        String etag = "\"" + updated.getVersion() + "\"";
        
        return ResponseEntity.ok()
                .eTag(etag)
                .body(updated);
    }

    /**
     * Delete a preference.
     */
    @Operation(summary = "Delete a preference",
               description = "Deletes a student preference by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Preference not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePreference(
            @Parameter(description = "ID of the preference to delete") @PathVariable Long id) {
        preferenceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get the count of all preferences.
     */
    @Operation(summary = "Get preference count",
               description = "Returns the total count of student preferences")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved count")
    @GetMapping(value = "/count", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Long> getPreferenceCount() {
        long count = preferenceService.count();
        return ResponseEntity.ok(count);
    }
}
