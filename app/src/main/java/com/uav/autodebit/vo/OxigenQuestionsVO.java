package com.uav.autodebit.vo;

import java.io.Serializable;

public class OxigenQuestionsVO extends BaseVO  implements Serializable {



    private String questionLabel;
    private String minLength;
    private String maxLength;
    private String validationRegex;
    private String isNumeric;
    private int elementId;
    private String instructions;
    private String jsonKey;


    public OxigenQuestionsVO(){

    }


    public String getQuestionLabel() {
        return questionLabel;
    }

    public void setQuestionLabel(String questionLabel) {
        this.questionLabel = questionLabel;
    }

    public String getMinLength() {
        return minLength;
    }

    public void setMinLength(String minLength) {
        this.minLength = minLength;
    }

    public String getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(String maxLength) {
        this.maxLength = maxLength;
    }

    public String getValidationRegex() {
        return validationRegex;
    }

    public void setValidationRegex(String validationRegex) {
        this.validationRegex = validationRegex;
    }

    public String getIsNumeric() {
        return isNumeric;
    }

    public void setIsNumeric(String isNumeric) {
        this.isNumeric = isNumeric;
    }

    public int getElementId() {
        return elementId;
    }

    public void setElementId(int elementId) {
        this.elementId = elementId;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getJsonKey() {
        return jsonKey;
    }

    public void setJsonKey(String jsonKey) {
        this.jsonKey = jsonKey;
    }
}
