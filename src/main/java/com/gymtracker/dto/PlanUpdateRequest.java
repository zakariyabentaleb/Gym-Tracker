package com.gymtracker.dto;

public class PlanUpdateRequest {
    private String name;
    private Integer durationDays;
    private Integer priceCents;
    private Boolean includesClasses;
    private String description;
    private Boolean active;

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
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}

