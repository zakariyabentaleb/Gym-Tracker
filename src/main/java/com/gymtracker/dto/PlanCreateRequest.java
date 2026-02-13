package com.gymtracker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PlanCreateRequest {

    @NotBlank
    private String name;

    @NotNull
    @Min(1)
    private Integer durationDays;

    @NotNull
    @Min(0)
    private Integer priceCents;

    private Boolean includesClasses = false;

    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }
    public Integer getPriceCents() { return priceCents; }
    public void setPriceCents(Integer priceCents) { this.priceCents = priceCents; }
    public Boolean getIncludesClasses() { return includesClasses; }
    public void setIncludesClasses(Boolean includesClasses) { this.includesClasses = includesClasses; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

