package app.dto;

import java.util.Date;

public class CRLInformationDTO extends AbstractEntityDTO {

    protected int ca; // id
    protected Date currentIssued;
    protected Date nextIssued;
    protected String crlFilename;
    protected int crlNumber;

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
}
