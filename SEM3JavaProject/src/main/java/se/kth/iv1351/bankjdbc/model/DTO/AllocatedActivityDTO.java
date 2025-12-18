package se.kth.iv1351.bankjdbc.model.DTO;

public class AllocatedActivityDTO {

    private final int id;
    private final int ciid;
    private final String study_period;

    public AllocatedActivityDTO(int id, int ciid, String study_period){
        
        this.id = id;
        this.ciid = ciid;
        this.study_period = study_period;
    }

    public int getId(){return this.id;}
    public int getCiid(){return this.ciid;}
    public String getStudyPeriod(){return this.study_period;}
}
