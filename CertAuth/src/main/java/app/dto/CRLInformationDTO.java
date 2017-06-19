package app.dto;

import java.util.Date;

public class CRLInformationDTO extends AbstractEntityDTO {

    protected int ca; // id
    protected Date currentIssued;
    protected Date nextIssued;
    protected String crlFilename;
    protected int crlNumber;
    protected String cronExpression;
    protected String frequencyDescription;

    public CRLInformationDTO() { }

    public int getCa() {
        return ca;
    }

    public void setCa(int ca) {
        this.ca = ca;
    }

    public Date getCurrentIssued() {
        return currentIssued;
    }

    public void setCurrentIssued(Date currentIssued) {
        this.currentIssued = currentIssued;
    }

    public Date getNextIssued() {
        return nextIssued;
    }

    public void setNextIssued(Date nextIssued) {
        this.nextIssued = nextIssued;
    }

    public String getCrlFilename() {
        return crlFilename;
    }

    public void setCrlFilename(String crlFilename) {
        this.crlFilename = crlFilename;
    }

    public int getCrlNumber() {
        return crlNumber;
    }

    public void setCrlNumber(int crlNumber) {
        this.crlNumber = crlNumber;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getFrequencyDescription() {
        return frequencyDescription;
    }

    public void setFrequencyDescription(String frequencyDescription) {
        this.frequencyDescription = frequencyDescription;
    }
}
