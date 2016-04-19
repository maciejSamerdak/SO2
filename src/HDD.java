
public class HDD {

	private final int size;
	Queue refferences;
	final int defaultPos;
	
	public HDD(int size, int defaultPos){
		this.size=size;
		this.defaultPos=defaultPos;
		refferences=new Queue();
	}
	// czy dana pozycja na dysku ju¿ jest zajêta
	public boolean containsRef(int pos){
		Rekord temp;
		for (int i=0; i<refferences.size(); i++){
			temp=(Rekord)refferences.getValue(i+1);
			if (temp.getPos()==pos)
				return true;
		}
		return false;
	}
	//usuñ zadanie na wybranym bloku
	public boolean removeRefAt(int pos){
		Rekord temp;
		for (int i=0; i<refferences.size(); i++){
			temp=(Rekord)refferences.getValue(i+1);
			if (temp.getPos()==pos){
				refferences.removeAt(i+1);
				return true;
			}
		}
		return false;
	}
	//czy w kolejce oczekuj¹ zadania real-time z wy¿szym priorytetem
	public boolean containsDeadlines(){
		Rekord temp;
		for (int i=0; i<refferences.size(); i++){
			temp=(Rekord)refferences.getValue(i+1);
			if (temp.getDeadline()!=0)
				return true;
		}
		return false;
	}
	//najbli¿sze zadanie real-time z najkrótszym deadlinem (wy¿szy priorytet), zwraca referencjê do rekordu
	public Rekord nearestDeadline(int currentPos){
		int min=size;
		int n=-1;
		Rekord temp;
		for (int i=0; i<refferences.size(); i++){
			temp=(Rekord)refferences.getValue(i+1);
			if (temp.getDeadline()!=0){
				int d=Math.abs(temp.getPos()-currentPos);
				if (d<min){
					min=d;
					n=i;
				}
			}
		}
		if(n!=-1)
		return (Rekord)refferences.getValue(n+1);
		else
		return null;
	}
	
	public Rekord shortestDeadline(){
		int min=size;
		int n=-1;
		Rekord temp;
		for (int i=0; i<refferences.size(); i++){
			temp=(Rekord)refferences.getValue(i+1);
			if (temp.getDeadline()!=0){
				int deadline=temp.getDeadline();
				if (deadline<min){
					min=deadline;
					n=i;
				}
			}
		}
		if(n!=-1)
		return (Rekord)refferences.getValue(n+1);
		else
		return null;
	}
	
	public int getSize(){
		return size;
	}
}
