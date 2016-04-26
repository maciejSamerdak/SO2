
public class HDD {

	private final int size;
	Queue refferences;
	//Queue 
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
	
	public Rekord getRef(int pos){
		Rekord temp;
		for (int i=0; i<refferences.size(); i++){
			temp=(Rekord)refferences.getValue(i+1);
			if (temp.getPos()==pos)
				return temp;
		}
		return null;
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
	
	public boolean removeRefAt(int pos, int time){
		Rekord temp;
		for (int i=0; i<refferences.size(); i++){
			temp=(Rekord)refferences.getValue(i+1);
			if (temp.getPos()==pos && temp.getArrival()<=time){
				refferences.removeAt(i+1);
				return true;
			}
		}
		return false;
	}
	//czy w kolejce oczekuj¹ zadania real-time z wy¿szym priorytetem
	public boolean containsDeadlines(int time){
		Rekord temp;
		for (int i=0; i<refferences.size(); i++){
			temp=(Rekord)refferences.getValue(i+1);
			if (temp.getDeadline()!=0 && temp.getArrival()<=time)
				return true;
		}
		return false;
	}
	//najbli¿sze zadanie real-time z najkrótszym deadlinem (wy¿szy priorytet), zwraca referencjê do rekordu
	public Rekord nearestDeadline(int currentPos, int t){
		int min=size;
		int n=-1;
		Rekord temp;
		for (int i=0; i<refferences.size(); i++){
			temp=(Rekord)refferences.getValue(i+1);
			if (temp.getDeadline()!=0 && temp.getArrival()<=t){
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
	
	public Rekord shortestDeadline(int t){
		int min=size;
		int n=-1;
		Rekord temp;
		for (int i=0; i<refferences.size(); i++){
			temp=(Rekord)refferences.getValue(i+1);
			if (temp.getDeadline()!=0 && temp.getArrival()<=t){
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
	
	public void updateDeadlines(int t){
		Rekord temp;
		for (int i=0; i<refferences.size(); i++){
			temp=(Rekord)refferences.getValue(i+1);
			if (temp.getDeadline()!=0){
				if(temp.getDeadline()<=t){
					refferences.removeAt(i+1);
					i--;
				}
				else{
					temp.setDeadline(temp.getDeadline()-t);
				}
			}
		}
	}
	
	public int getSize(){
		return size;
	}
}
