import java.util.Random;

public class Main {

	final static int size=100;				//iloœæ bloków na dysku
	final static int reffQuantity=10;		//iloœæ zg³oszeñ w kolejce
	final static int defaultPos=50;			//wyjœciowa pozycja g³owic
		
	public static void main(String[] args) {
		
		Random rn=new Random();
		
		/*tworzymy cztery warianty tego samego dysku do obs³ugi przez cztery ró¿ne algorytmy,
		 * plus dwa warianty do ka¿dego z nich do obs³ugi EDF i FD-SCAN dla real-time
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
		
		//proces wype³niania kolejki zg³oszeñ
		for (int i=0; i<reffQuantity; i++){
			int pos=rn.nextInt(size);
			
			//sprawdzamy czy blok na dysku nie jest ju¿ zajêty
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
		
		//teraz generujemy zg³oszenia dla aplikacji real time z deadlinem
		
		//domyœlnie iloœæ takich zg³oszeñ to liczba losowa z przedzia³u od 4 do iloœæ zg³oszeñ/2
		int rtReffQuant=rn.nextInt(reffQuantity/2-4)+4;
		for (int i=0; i<rtReffQuant; i++){
			int pos=rn.nextInt(size);
			
			while(disk1.containsRef(pos))
				pos=rn.nextInt(size);
			
			int deadline=rn.nextInt(size)+1;
			
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
		System.out.println("kontrola");	
		//tu podstawiamy wyniki z EDF i FD-SCAN, do pózniejszego odniesienia w tabeli wyników
		/*int[]fordisk12;
		int[]fordisk13=FDSCAN(disk13);
		
		int[]fordisk22;
		int[]fordisk23=FDSCAN(disk23);
			
		int[]fordisk32;
		int[]fordisk33=FDSCAN(disk33);
			
		int[]fordisk42;
		int[]fordisk43=FDSCAN(disk43);*/
		
		//drukujemy dane i wyniki
		System.out.printf("Liczba bloków: %d; Liczba zg³oszeñ: %d; Liczba zg³oszeñ Real-Time: %d; Pierwotne po³o¿enie g³owic: %d;", size, reffQuantity, rtReffQuant, defaultPos);
		System.out.print("\n");
		System.out.printf("|%10s|%10s|%10s|%10s|%10s|%10s|", "Algorytm", "Standard", "Z EDF", "Odrzucenia", "Z FD-SCAN", "Odrzucenia");
		System.out.print("\n");
		System.out.printf("|%-10s|%10d|%10d|%10d|%10s|%10s|", "FCFS", FCFS(disk1), 0, 0, 0, 0); //fordisk13[0]+FCFS(disk13), fordisk13[1]);
		System.out.print("\n");
		System.out.printf("|%-10s|%10d|%10d|%10d|%10s|%10s|", "SSTF", 0, 0, 0, 0, 0); //fordisk23[0]+0, fordisk23[1]);
		System.out.print("\n");
		System.out.printf("|%-10s|%10d|%10d|%10d|%10s|%10s|", "SCAN", SCAN(disk3), 0, 0, 0, 0); //fordisk33[0]+SCAN(disk33), fordisk33[1]);
		System.out.print("\n");
		System.out.printf("|%-10s|%10d|%10d|%10d|%10s|%10s|", "C-SCAN", CSCAN(disk4), 0, 0, 0, 0); //fordisk43[0]+CSCAN(disk43), fordisk43[1]);
	}
	
	/*we wszystkich algorytmach czas zliczamy na podstawie liczby bloków, przez które g³owica musi przejœæ do celu
	 * czas = | aktualna pozycja - pozycja celu |
	 */
	public static int FCFS(HDD disk){
		int time=0;
		int currentPos=disk.defaultPos;
		Rekord temp;
		while(!disk.refferences.isEmpty()){ //dopóki kolejka jest pe³na realizujemy kolejne zg³oszenia
			temp=(Rekord)disk.refferences.getValue(1);
			time+=Math.abs(currentPos-temp.getPos());
			currentPos=temp.getPos();
			disk.refferences.removeFirst();
		}
		return time;
	}
	
	public static int SCAN(HDD disk){
		int time=0;
		
		int currentPos=disk.defaultPos;
		//idziemy w dó³ do samego koñca realizuj¹c napotkane zadania
		for (int i=currentPos; i>=0; i--){
			if(disk.removeRefAt(i)){
				time+=Math.abs(currentPos-i);
				currentPos=i;
			}
		}
		//idziemy w górê do samego koñca realizuj¹c napotkane zadania
		for (int i=currentPos; i<disk.getSize(); i++){
			if(disk.removeRefAt(i)){
				time+=Math.abs(currentPos-i);
				currentPos=i;
			}
		}
		return time;
	}

	public static int CSCAN(HDD disk){
		int time=0;
		//idziemy w dó³ do samego koñca realizuj¹c napotkane zadania
		int currentPos=disk.defaultPos;
		for (int i=currentPos; i>=0; i--){
			if(disk.removeRefAt(i)){
				time+=Math.abs(currentPos-i);
				currentPos=i;
			}
		}
		//idziemy na drugi koniec
		time+=disk.getSize()-1;
		currentPos=disk.getSize()-1;
		//znów idziemy w dó³ do pozycji startowej realizuj¹c napotkane zadania
		for (int i=currentPos; i>disk.defaultPos; i--){
			if(disk.removeRefAt(i)){
				time+=Math.abs(currentPos-i);
				currentPos=i;
			}
		}
		return time;
	}
	//wynikiem jest dwuelementowa tablica zawieraj¹ca w komórce 0 czas realizacji oraz liczbê odrzuconych zadañ w komórce 1
	public static int[] FDSCAN(HDD disk){
		int[] result=new int[2];
		int time=0;
		int rejected=0;
		Rekord nearest;
		int currentPos=disk.defaultPos;
		while(disk.containsDeadlines()){
			nearest=disk.nearestDeadline(currentPos);
			int deadlinePos=nearest.getPos();
			if (nearest.getDeadline()<=Math.abs(currentPos-deadlinePos))	//je¿eli czas przejœcia do celu jest mniejszy ni¿ jego deadline
				if (deadlinePos<currentPos)
					// kiedy deadline znajduje siê ni¿ej pozycji bie¿¹cej
					for (int i=currentPos; i>=deadlinePos; i--){
						if(disk.removeRefAt(i)){
							time+=Math.abs(currentPos-i);
							currentPos=i;
						}
					}
				else
					//kiedy deadline znajduje siê wy¿ej pozycji bie¿¹cej
					for (int i=currentPos; i<=deadlinePos; i++){
						if(disk.removeRefAt(i)){
							time+=Math.abs(currentPos-i);
							currentPos=i;
						}
					}
			else
				//doliczamy do zadañ odrzuconych 
				rejected++;
		}
		result[0]=time;
		result[1]=rejected;
		return result;
	}
}
