package com.ppanero.disease_checker;

/**
 * Created by Light on 14/03/16.
 */
public enum DiseaseLevel {

    UNDEF, LOW, MEDIUM, HIGH, INACTIVE;

    public String toString(){
        switch(this){
            case INACTIVE:
                return "Inactive";
            case LOW:
                return "Low";
            case MEDIUM:
                return "Medium";
            case HIGH:
                return "High";
            case UNDEF:
            default:
                return "Undefined";
        }
    }

    public static DiseaseLevel fromString(String level){
        switch(level.toUpperCase()){
            case "INACTIVE": return DiseaseLevel.INACTIVE;
            case "LOW": return DiseaseLevel.LOW;
            case "MEDIUM": return DiseaseLevel.MEDIUM;
            case "HIGH": return DiseaseLevel.HIGH;
            default: return DiseaseLevel.UNDEF;
        }
    }
}
