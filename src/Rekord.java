
public class Rekord {
	
	private int pos;
	private int deadline;
	
	public Rekord(int pos, int deadline){		//rekord dla aplikacji real-time z deadlinem
		this.pos=pos;
		this.deadline=deadline;
	}
	
	public Rekord(int pos){ 	//zwyk³y rekord, bez deadlinu
		this.pos=pos;
		deadline=0;
	}
	
	public int getPos(){
		return pos;
	}
	
	public int getDeadline(){
		return deadline;
	}
	
}
