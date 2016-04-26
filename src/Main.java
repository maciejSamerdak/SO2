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
		int x=0;
		for (int i=0; i<rtReffQuant; i++){
			int pos=rn.nextInt(size);
			
			while(disk1.containsRef(pos))
				pos=rn.nextInt(size);
			
			
			int arrival=x+rn.nextInt(5);
			x+=5;
			
			//System.out.println(pos);
			
			int deadline=rn.nextInt(size-1)+1;
			
			//disk1.refferences.insert(new Rekord(pos, deadline, arrival));
			disk12.refferences.insert(new Rekord(pos, deadline, arrival));
			disk13.refferences.insert(new Rekord(pos, deadline, arrival));
			
			//disk2.refferences.insert(new Rekord(pos, deadline, arrival));
			disk22.refferences.insert(new Rekord(pos, deadline, arrival));
			disk23.refferences.insert(new Rekord(pos, deadline, arrival));
			
			//disk3.refferences.insert(new Rekord(pos, deadline, arrival));
			disk32.refferences.insert(new Rekord(pos, deadline, arrival));
			disk33.refferences.insert(new Rekord(pos, deadline, arrival));
			
			//disk4.refferences.insert(new Rekord(pos, deadline, arrival));
			disk42.refferences.insert(new Rekord(pos, deadline, arrival));
			disk43.refferences.insert(new Rekord(pos, deadline, arrival));
		}
		//System.out.println("kontrola");	
		//tu podstawiamy wyniki z EDF i FD-SCAN, do pózniejszego odniesienia w tabeli wyników
		/*int[]fordisk12=EDF(disk12);
		int[]fordisk13=FDSCAN(disk13);
		
		int[]fordisk22=EDF(disk22);
		int[]fordisk23=FDSCAN(disk23);
			
		int[]fordisk32=EDF(disk32);
		int[]fordisk33=FDSCAN(disk33);
			
		int[]fordisk42=EDF(disk42);
		int[]fordisk43=FDSCAN(disk43);*/
		
		//drukujemy dane i wyniki
		System.out.printf("Liczba bloków: %d; Liczba zg³oszeñ: %d; Liczba zg³oszeñ Real-Time: %d; Pierwotne po³o¿enie g³owic: %d;", size, reffQuantity, rtReffQuant, defaultPos);
		System.out.print("\n");
		System.out.printf("|%10s|%10s|%10s|%10s|", "Algorytm", "Standard", "Z EDF", /*"Odrzucenia",*/ "Z FD-SCAN"/*, "Odrzucenia"*/);
		System.out.print("\n");
		System.out.printf("|%-10s|%10d|%10d|%10d|", "FCFS", FCFS(disk1, false, false), FCFS(disk12, true, false), /*fordisk12[1],*/ FCFS(disk13, false, true)/*, fordisk13[1]*/);
		System.out.print("\n");
		System.out.printf("|%-10s|%10d|%10d|%10d|", "SSTF", SSTF(disk2, false, false), /*fordisk22[0]+*/SSTF(disk22, true, false), /*fordisk22[1],*/ /*fordisk23[0]+*/SSTF(disk23, false, true)/*, fordisk23[1]*/);
		System.out.print("\n");
		System.out.printf("|%-10s|%10d|%10d|%10d|", "SCAN", SCAN(disk3, false, false), /*fordisk32[0]+*/SCAN(disk12, true, false), /*fordisk32[1],*/ /*fordisk33[0]+*/SCAN(disk33, false, true)/*, fordisk33[1]*/);
		System.out.print("\n");
		System.out.printf("|%-10s|%10d|%10d|%10d|", "C-SCAN", CSCAN(disk4, false, false), /*fordisk42[0]+*/CSCAN(disk12, true, false), /*fordisk42[1],*/ /*fordisk43[0]+*/CSCAN(disk43, false, true)/*, fordisk43[1]*/);
	}
	
	/*we wszystkich algorytmach czas zliczamy na podstawie liczby bloków, przez które g³owica musi przejœæ do celu
	 * czas = | aktualna pozycja - pozycja celu |
	 */
	public static int FCFS(HDD disk, boolean edf, boolean fdscan){
		int time=0;
		int currentPos=disk.defaultPos;
		Rekord temp;
		while(!disk.refferences.isEmpty()){ //dopóki kolejka jest pe³na realizujemy kolejne zg³oszenia
			
			if(edf || fdscan){
				int[] result;
				if(edf)
					result=EDF(disk, currentPos, time);
				else
					result=FDSCAN(disk, currentPos, time);
				currentPos=result[2];
				time=result[0];
			}
			
			temp=(Rekord)disk.refferences.getValue(1);
			time+=Math.abs(currentPos-temp.getPos());
			currentPos=temp.getPos();
			disk.refferences.removeFirst();
			disk.updateDeadlines(time);
		}
		return time;
	}
	
	public static int SCAN(HDD disk, boolean edf, boolean fdscan){
		int time=0;
		
		int currentPos=disk.defaultPos;
		//idziemy w dó³ do samego koñca realizuj¹c napotkane zadania
		for (int i=currentPos; i>=0; i--){
			
			if(edf || fdscan){
				int[] result;
				if(edf)
					result=EDF(disk, currentPos, time);
				else
					result=FDSCAN(disk, currentPos, time);
				currentPos=result[2];
				time=result[0];
			}
			
			if(disk.removeRefAt(i,time)){
				time+=Math.abs(currentPos-i);
				currentPos=i;
				disk.updateDeadlines(time);
				
			}
			
		}
		time+=currentPos;
		currentPos=0;
		//idziemy w górê do samego koñca realizuj¹c napotkane zadania
		for (int i=currentPos; i<disk.getSize(); i++){
			
			if(edf || fdscan){
				int[] result;
				if(edf)
					result=EDF(disk, currentPos, time);
				else
					result=FDSCAN(disk, currentPos, time);
				currentPos=result[2];
				time=result[0];
			}
			
			if(disk.removeRefAt(i,time)){
				time+=Math.abs(currentPos-i);
				currentPos=i;
				disk.updateDeadlines(time);
			}
		}
		time+=Math.abs(disk.getSize()-1-currentPos);
		return time;
	}

	public static int CSCAN(HDD disk, boolean edf, boolean fdscan){
		int time=0;
		//idziemy w dó³ do samego koñca realizuj¹c napotkane zadania
		int currentPos=disk.defaultPos;
		for (int i=currentPos; i>=0; i--){
			
			if(edf || fdscan){
				int[] result;
				if(edf)
					result=EDF(disk, currentPos, time);
				else
					result=FDSCAN(disk, currentPos, time);
				currentPos=result[2];
				time=result[0];
			}
			
			if(disk.removeRefAt(i,time)){
				time+=Math.abs(currentPos-i);
				currentPos=i;
				disk.updateDeadlines(time);
			}			
		}
		//idziemy na drugi koniec
		time+=currentPos+disk.getSize()-1;
		currentPos=disk.getSize()-1;
		//znów idziemy w dó³ do pozycji startowej realizuj¹c napotkane zadania
		for (int i=currentPos; i>disk.defaultPos; i--){
			
			if(edf || fdscan){
				int[] result;
				if(edf)
					result=EDF(disk, currentPos, time);
				else
					result=FDSCAN(disk, currentPos, time);
				currentPos=result[2];
				time=result[0];
			}
			
			if(disk.removeRefAt(i, time)){
				time+=Math.abs(currentPos-i);
				currentPos=i;
				disk.updateDeadlines(time);
			}
		}
		time+=Math.abs(disk.defaultPos-currentPos);
		return time;
	}
	
	public static int SSTF(HDD disk, boolean edf, boolean fdscan ){
		int time=0;
		int currentPos=disk.defaultPos;
		int i,j=0;
		i=currentPos+1; j=currentPos-1;
		boolean zn1=false, zn2=false; int zn3=0, zn4=0;
		while (!disk.refferences.isEmpty()){
			
			if(edf || fdscan){
				int[] result;
				if(edf)
					result=EDF(disk, currentPos, time);
				else
					result=FDSCAN(disk, currentPos, time);
				currentPos=result[2];
				time=result[0];
			}
			
			//System.out.println("currentPos= " + currentPos);
			while(i>=0 && zn1==false){
				i--;
				if(disk.containsRef(i) && disk.getRef(i).getDeadline()<=time){
					zn1=true;
				}
			}
			//System.out.println("i= " + i);
			while(j<disk.getSize() && zn2==false){
				j++;
				if(disk.containsRef(j) && disk.getRef(j).getDeadline()<=time){
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
	
	public static int[] EDF(HDD disk, int pos, int t){
		int currentPos=pos;
		int time=t;
		int rejected=0;
		int[] result=new int[3];
		while(disk.containsDeadlines(time)){
			Rekord nearest=disk.shortestDeadline(time);
			int deadlinePos=nearest.getPos();
			if(nearest.getDeadline()>=Math.abs(deadlinePos-currentPos)){
				time+=Math.abs(deadlinePos-currentPos);
				currentPos=deadlinePos;
				disk.removeRefAt(deadlinePos);
				disk.updateDeadlines(time);
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
	
	//wynikiem jest trzyelementowa tablica zawieraj¹ca w komórce 0 czas realizacji, liczbê odrzuconych zadañ w komórce 1 i ostatni¹ pozycjê g³owic w komórce 2
	public static int[] FDSCAN(HDD disk, int pos, int t){
		int[] result=new int[3];
		int time=t;
		int rejected=0;
		Rekord nearest;
		int currentPos=pos;
		while(disk.containsDeadlines(time)){
			nearest=disk.nearestDeadline(currentPos, time);
				
			int deadlinePos=nearest.getPos();
			//System.out.println(deadlinePos);
			if (nearest.getDeadline()>=Math.abs(currentPos-deadlinePos))	//je¿eli czas przejœcia do celu jest mniejszy ni¿ jego deadline
				if (deadlinePos<currentPos)
					// kiedy deadline znajduje siê ni¿ej pozycji bie¿¹cej
					for (int i=currentPos; i>=deadlinePos; i--){
						if(disk.removeRefAt(i, time)){
							time+=Math.abs(currentPos-i);
							disk.updateDeadlines(time);
							currentPos=i;
						}
					}
				else
					//kiedy deadline znajduje siê wy¿ej pozycji bie¿¹cej
					for (int i=currentPos; i<=deadlinePos; i++){
						if(disk.removeRefAt(i, time)){
							time+=Math.abs(currentPos-i);
							disk.updateDeadlines(time);
							currentPos=i;
						}
					}
			else{
				//doliczamy do zadañ odrzuconych 
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
