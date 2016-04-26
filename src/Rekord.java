
public class Rekord {
	
	private int pos;
	private int deadline;
	private int arrival;
	
	
	public Rekord(int pos, int deadline, int arrival){		//rekord dla aplikacji real-time z deadlinem
		this.pos=pos;
		this.deadline=deadline;
		this.arrival=arrival;
	}
	
	public Rekord(int pos){ 	//zwyk³y rekord, bez deadlinu
		this.pos=pos;
		deadline=0;
		arrival=0;
	}
	
	public int getPos(){
		return pos;
	}
	
	public int getDeadline(){
		return deadline;
	}
	
	public void setDeadline(int deadline){
		this.deadline=deadline;
	}
	
	public int getArrival(){
		return arrival;
	}
}
