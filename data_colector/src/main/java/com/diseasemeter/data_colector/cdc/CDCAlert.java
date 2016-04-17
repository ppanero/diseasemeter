package com.diseasemeter.data_colector.cdc;


import com.diseasemeter.data_colector.common.MACRO;

public class CDCAlert {

    private String disease;
    private String diseaseExtra;
    private String place;
    private String placeExtra;
    private String date;
    private int level;
    private int weight;

    public CDCAlert() { }

    public CDCAlert(String disease, String diseaseExtra, String place, String placeExtra, String date, int level, int weight) {
        this.disease = disease;
        this.diseaseExtra = diseaseExtra;
        this.place = place;
        this.placeExtra = placeExtra;
        this.date = date;
        this.level = level;
        this.weight = weight;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getDiseaseExtra() {
        return diseaseExtra;
    }

    public void setDiseaseExtra(String diseaseExtra) {
        this.diseaseExtra = diseaseExtra;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPlaceExtra() {
        return placeExtra;
    }

    public void setPlaceExtra(String placeExtra) {
        this.placeExtra = placeExtra;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public static int getWeightFromLevel(int level){
        if(level == 1) return 100;
        else if (level == 2) return 250;
        else if (level == 3) return 500;
        else return 0;
    }

    @Override
    public String toString(){
        String dExtra = this.diseaseExtra;
        if(dExtra == null) dExtra = " null disease extra";
        String pExtra = this.placeExtra;
        if(pExtra == null) pExtra = " null place extra";
        return this.disease.concat(MACRO.COMMA).concat(dExtra).concat(MACRO.COMMA).concat(this.date).concat(MACRO.COMMA)
                .concat(this.place).concat(MACRO.COMMA).concat(pExtra).concat(MACRO.COMMA)
                .concat(String.valueOf(this.level)).concat(MACRO.COMMA).concat(String.valueOf(this.weight));
    }
}
