package com.gymtracker.dto;

public class PlanResponse {
    private Long id;
    private String name;
    private Integer durationDays;
    private Integer priceCents;
    private Boolean includesClasses;
    private String description;
    private Boolean active;

    public PlanResponse() {}

    public PlanResponse(Long id, String name, Integer durationDays, Integer priceCents, Boolean includesClasses, String description, Boolean active) {
        this.id = id;
        this.name = name;
        this.durationDays = durationDays;
        this.priceCents = priceCents;
        this.includesClasses = includesClasses;
        this.description = description;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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

