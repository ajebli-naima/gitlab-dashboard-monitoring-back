package com.leyton.backend.dto;

import java.util.Date;
import java.util.Map;

public class StaticticByUnitDto {
    private Map<String, Map<Date, Integer>> commitPerUnit;
    private String unit;

    public StaticticByUnitDto() {
    }

    public Map<String, Map<Date, Integer>> getCommitPerUnit() {
        return commitPerUnit;
    }

    public void setCommitPerUnit(Map<String, Map<Date, Integer>> commitPerUnit) {
        this.commitPerUnit = commitPerUnit;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
