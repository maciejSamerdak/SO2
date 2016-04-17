
public class HDD {

	private final int size;
	Queue refferences;
	final int defaultPos;
	
	public HDD(int size, int defaultPos){
		this.size=size;
		this.defaultPos=defaultPos;
		refferences=new Queue();
	}
	// czy dana pozycja na dysku ju� jest zaj�ta
	public boolean containsRef(int pos){
		Rekord temp;
		for (int i=0; i<refferences.size(); i++){
			temp=(Rekord)refferences.getValue(i);
			if (temp.getPos()==pos)
				return true;
		}
		return false;
	}
	//usu� zadanie na wybranym bloku
	public boolean removeRefAt(int pos){
		Rekord temp;
		for (int i=0; i<refferences.size(); i++){
			temp=(Rekord)refferences.getValue(i);
			if (temp.getPos()==pos){
				refferences.removeAt(i);
				return true;
			}
		}
		return false;
	}
	//czy w kolejce oczekuj� zadania real-time z wy�szym priorytetem
	public boolean containsDeadlines(){
		Rekord temp;
		for (int i=0; i<refferences.size(); i++){
			temp=(Rekord)refferences.getValue(i);
			if (temp.getDeadline()>0)
				return true;
		}
		return false;
	}
	//najbli�sze zadanie real-time z najkr�tszym deadlinem (wy�szy priorytet), zwraca referencj� do rekordu
	public Rekord nearestDeadline(int currentPos){
		int min=size;
		int n=-1;
		Rekord temp;
		for (int i=0; i<refferences.size(); i++){
			temp=(Rekord)refferences.getValue(i);
			int d=Math.abs(temp.getPos()-currentPos);
			if (d<min){
				min=d;
				n=i;
			}
		}
		return (Rekord)refferences.getValue(n);
	}
	
	public int getSize(){
		return size;
	}
}