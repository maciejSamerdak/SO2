import java.util.Random;

public class Main {

	final static int size=100;				//ilo�� blok�w na dysku
	final static int reffQuantity=10;		//ilo�� zg�osze� w kolejce
	final static int defaultPos=50;			//wyj�ciowa pozycja g�owic
		
	public static void main(String[] args) {
		
		Random rn=new Random();
		
		/*tworzymy cztery warianty tego samego dysku do obs�ugi przez cztery r�ne algorytmy,
		 * plus dwa warianty do ka�dego z nich do obs�ugi EDF i FD-SCAN dla real-time
		 */
		HDD disk1=new HDD(size, defaultPos);
		HDD disk12=new HDD(size, defaultPos);
		HDD disk13=new HDD(size, defaultPos);
		
		HDD disk2=new HDD(size, defaultPos);
		HDD disk22=new HDD(size, defaultPos);
		HDD disk23=new HDD(size, defaultPos);
		
		HDD disk3=new HDD(size, defaultPos);
		HDD disk32=new HDD(size, defaultPos);
		HDD disk33=new HDD(size, defaultPos);
		
		HDD disk4=new HDD(size, defaultPos);
		HDD disk42=new HDD(size, defaultPos);
		HDD disk43=new HDD(size, defaultPos);
		
		//proces wype�niania kolejki zg�osze�
		for (int i=0; i<reffQuantity; i++){
			int pos=rn.nextInt(size);
			
			//sprawdzamy czy blok na dysku nie jest ju� zaj�ty
			while(disk1.containsRef(pos))
				pos=rn.nextInt(size);
			
			disk1.refferences.insert(new Rekord(pos));
			disk12.refferences.insert(new Rekord(pos));
			disk13.refferences.insert(new Rekord(pos));
			
			disk2.refferences.insert(new Rekord(pos));
			disk22.refferences.insert(new Rekord(pos));
			disk23.refferences.insert(new Rekord(pos));
			
			disk3.refferences.insert(new Rekord(pos));
			disk32.refferences.insert(new Rekord(pos));
			disk33.refferences.insert(new Rekord(pos));
			
			disk4.refferences.insert(new Rekord(pos));
			disk42.refferences.insert(new Rekord(pos));
			disk43.refferences.insert(new Rekord(pos));
		}
		
		//teraz generujemy zg�oszenia dla aplikacji real time z deadlinem
		
		//domy�lnie ilo�� takich zg�osze� to liczba losowa z przedzia�u od 4 do ilo�� zg�osze�/2
		int rtReffQuant=rn.nextInt(reffQuantity/2-4)+4;
		for (int i=0; i<rtReffQuant; i++){
			int pos=rn.nextInt(size);
			
			while(disk1.containsRef(pos))
				pos=rn.nextInt(size);
			
			//System.out.println(pos);
			
			int deadline=rn.nextInt(size-1)+1;
			
			disk1.refferences.insert(new Rekord(pos, deadline));
			disk12.refferences.insert(new Rekord(pos, deadline));
			disk13.refferences.insert(new Rekord(pos, deadline));
			
			disk2.refferences.insert(new Rekord(pos, deadline));
			disk22.refferences.insert(new Rekord(pos, deadline));
			disk23.refferences.insert(new Rekord(pos, deadline));
			
			disk3.refferences.insert(new Rekord(pos, deadline));
			disk32.refferences.insert(new Rekord(pos, deadline));
			disk33.refferences.insert(new Rekord(pos, deadline));
			
			disk4.refferences.insert(new Rekord(pos, deadline));
			disk42.refferences.insert(new Rekord(pos, deadline));
			disk43.refferences.insert(new Rekord(pos, deadline));
		}
		//System.out.println("kontrola");	
		//tu podstawiamy wyniki z EDF i FD-SCAN, do p�zniejszego odniesienia w tabeli wynik�w
		int[]fordisk12=EDF(disk12);
		int[]fordisk13=FDSCAN(disk13);
		
		int[]fordisk22=EDF(disk22);
		int[]fordisk23=FDSCAN(disk23);
			
		int[]fordisk32=EDF(disk32);
		int[]fordisk33=FDSCAN(disk33);
			
		int[]fordisk42=EDF(disk42);
		int[]fordisk43=FDSCAN(disk43);
		
		//drukujemy dane i wyniki
		System.out.printf("Liczba blok�w: %d; Liczba zg�osze�: %d; Liczba zg�osze� Real-Time: %d; Pierwotne po�o�enie g�owic: %d;", size, reffQuantity, rtReffQuant, defaultPos);
		System.out.print("\n");
		System.out.printf("|%10s|%10s|%10s|%10s|%10s|%10s|", "Algorytm", "Standard", "Z EDF", "Odrzucenia", "Z FD-SCAN", "Odrzucenia");
		System.out.print("\n");
		System.out.printf("|%-10s|%10d|%10d|%10d|%10s|%10s|", "FCFS", FCFS(disk1, disk1.defaultPos), fordisk12[0]+FCFS(disk12,fordisk12[2]), fordisk12[1], fordisk13[0]+FCFS(disk13,fordisk13[2]), fordisk13[1]);
		System.out.print("\n");
		System.out.printf("|%-10s|%10d|%10d|%10d|%10s|%10s|", "SSTF", SSTF(disk2, disk2.defaultPos), fordisk22[0]+SSTF(disk12,fordisk22[2]), fordisk22[1], fordisk23[0]+0, fordisk23[1]);
		System.out.print("\n");
		System.out.printf("|%-10s|%10d|%10d|%10d|%10s|%10s|", "SCAN", SCAN(disk3, disk3.defaultPos), fordisk32[0]+SCAN(disk12,fordisk32[2]), fordisk32[1], fordisk33[0]+SCAN(disk33,fordisk33[2]), fordisk33[1]);
		System.out.print("\n");
		System.out.printf("|%-10s|%10d|%10d|%10d|%10s|%10s|", "C-SCAN", CSCAN(disk4, disk4.defaultPos), fordisk42[0]+CSCAN(disk12,fordisk42[2]), fordisk42[1], fordisk43[0]+CSCAN(disk43,fordisk43[2]), fordisk43[1]);
	}
	
	/*we wszystkich algorytmach czas zliczamy na podstawie liczby blok�w, przez kt�re g�owica musi przej�� do celu
	 * czas = | aktualna pozycja - pozycja celu |
	 */
	public static int FCFS(HDD disk, int pos){
		int time=0;
		int currentPos=pos;
		Rekord temp;
		while(!disk.refferences.isEmpty()){ //dop�ki kolejka jest pe�na realizujemy kolejne zg�oszenia
			temp=(Rekord)disk.refferences.getValue(1);
			time+=Math.abs(currentPos-temp.getPos());
			currentPos=temp.getPos();
			disk.refferences.removeFirst();
		}
		return time;
	}
	
	public static int SCAN(HDD disk, int pos){
		int time=0;
		
		int currentPos=pos;
		//idziemy w d� do samego ko�ca realizuj�c napotkane zadania
		for (int i=currentPos; i>=0; i--){
			if(disk.removeRefAt(i)){
				time+=Math.abs(currentPos-i);
				currentPos=i;
			}
			
		}
		time+=currentPos;
		currentPos=0;
		//idziemy w g�r� do samego ko�ca realizuj�c napotkane zadania
		for (int i=currentPos; i<disk.getSize(); i++){
			if(disk.removeRefAt(i)){
				time+=Math.abs(currentPos-i);
				currentPos=i;
			}
		}
		time+=Math.abs(disk.getSize()-1-currentPos);
		return time;
	}

	public static int CSCAN(HDD disk, int pos){
		int time=0;
		//idziemy w d� do samego ko�ca realizuj�c napotkane zadania
		int currentPos=pos;
		for (int i=currentPos; i>=0; i--){
			if(disk.removeRefAt(i)){
				time+=Math.abs(currentPos-i);
				currentPos=i;
			}			
		}
		//idziemy na drugi koniec
		time+=currentPos+disk.getSize()-1;
		currentPos=disk.getSize()-1;
		//zn�w idziemy w d� do pozycji startowej realizuj�c napotkane zadania
		for (int i=currentPos; i>pos; i--){
			if(disk.removeRefAt(i)){
				time+=Math.abs(currentPos-i);
				currentPos=i;
			}
		}
		time+=Math.abs(pos-currentPos);
		return time;
	}
	
	public static int SSTF(HDD disk, int pos){
		int time=0;
		int currentPos=pos;
		int i,j=0;
		i=currentPos+1; j=currentPos-1;
		boolean zn1=false, zn2=false; int zn3=0, zn4=0;
		for (int l=0; l<reffQuantity; l++){
			//System.out.println("currentPos= " + currentPos);
			while(i>=0 && zn1==false){
				i--;
				if(disk.containsRef(i)){
					zn1=true;
				}
			}
			//System.out.println("i= " + i);
			while(j<disk.getSize() && zn2==false){
				j++;
				if(disk.containsRef(j)){
					zn2=true;
				}
			}
			//System.out.println("j= " + j);
			if(zn1==true && zn2==false) { disk.removeRefAt(i); time+=Math.abs(currentPos-i); /*System.out.println("usuwana1= " + i);*/ currentPos=i; j=i;}
			if(zn1==false && zn2==true) { disk.removeRefAt(j); time+=Math.abs(j-currentPos); /*System.out.println("usuwana2= " + j);*/ currentPos=j; i=j;}
			if(zn1==true && zn2==true){
				if(Math.abs(currentPos-i)>=Math.abs(currentPos-j)){
					time+=Math.abs(j-currentPos);
					while(zn3<1){
						disk.removeRefAt(j); /*System.out.println("usuwana3= " + j)*/; zn3=1; currentPos=j; i=j;
					}
				}
				else{
					time+=Math.abs(currentPos-i);
					while(zn4<1){
						disk.removeRefAt(i); /*System.out.println("usuwana4= " + i)*/;zn4=1; currentPos=i; j=i;
					}
				}
			}
			zn1=false; zn2=false; zn3=0; zn4=0;
		}
		return time;
	}
	
	public static int[] EDF(HDD disk){
		int currentPos=disk.defaultPos;
		int time=0;
		int rejected=0;
		int[] result=new int[3];
		while(disk.containsDeadlines()){
			Rekord nearest=disk.shortestDeadline();
			int deadlinePos=nearest.getPos();
			if(nearest.getDeadline()>=Math.abs(deadlinePos-currentPos)){
				time+=Math.abs(deadlinePos-currentPos);
				currentPos=deadlinePos;
				disk.removeRefAt(deadlinePos);
			}
			else{
				rejected++;
				disk.removeRefAt(deadlinePos);
			}
		}
		result[0]=time;
		result[1]=rejected;
		result[2]=currentPos;
		return result;
	}
	
	//wynikiem jest trzyelementowa tablica zawieraj�ca w kom�rce 0 czas realizacji, liczb� odrzuconych zada� w kom�rce 1 i ostatni� pozycj� g�owic w kom�rce 2
	public static int[] FDSCAN(HDD disk){
		int[] result=new int[3];
		int time=0;
		int rejected=0;
		Rekord nearest;
		int currentPos=disk.defaultPos;
		while(disk.containsDeadlines()){
			nearest=disk.nearestDeadline(currentPos);
			int deadlinePos=nearest.getPos();
			//System.out.println(deadlinePos);
			if (nearest.getDeadline()>=Math.abs(currentPos-deadlinePos))	//je�eli czas przej�cia do celu jest mniejszy ni� jego deadline
				if (deadlinePos<currentPos)
					// kiedy deadline znajduje si� ni�ej pozycji bie��cej
					for (int i=currentPos; i>=deadlinePos; i--){
						if(disk.removeRefAt(i)){
							time+=Math.abs(currentPos-i);
							currentPos=i;
						}
					}
				else
					//kiedy deadline znajduje si� wy�ej pozycji bie��cej
					for (int i=currentPos; i<=deadlinePos; i++){
						if(disk.removeRefAt(i)){
							time+=Math.abs(currentPos-i);
							currentPos=i;
						}
					}
			else{
				//doliczamy do zada� odrzuconych 
				rejected++;
				disk.removeRefAt(deadlinePos);
			//System.out.println(rejected);
			}
		}
		result[0]=time;
		result[1]=rejected;
		result[2]=currentPos;
		return result;
	}
}
