
public class Queue {

	private int size;
	private Element head;
	
	public Queue(){
		this.size=0;
		this.head=null;
	}

	public boolean isEmpty(){
		return (size==0);
	}
	
	public void insert(Object value){
		if (isEmpty()){
			head=new Element(value);
			size++;
		}
		else{
			Element search=head;
			while(search.getNext()!=null){
				search=search.getNext();				
			}
			search.setNext(new Element(value));
			size++;
		}
	}
	
	public boolean removeFirst(){
		if(!isEmpty()){
			head=head.getNext();
			size--;
			return true;
		}
		return false;
	}
	
	public boolean removeAt(int n){
		if(!isEmpty() && n>0 && n<=size){
			if(n==1){
				head=head.getNext();
				size--;
				return true;
			}
			Element it=head;
			for (int i=0; i<n-2; i++)
				it=it.getNext();
			it.setNext(it.getNext().getNext());
			size--;
				return true;	
		}
		return false;
	}
	
	public int size(){
		return size;
	}
		
	public Object getValue(int n){
		Element it=head;
		for (int i=1; i<n; i++)
			it=it.getNext();
		return it.getValue();
	}	
	
	private static class Element{
		private Object value;
		private Element next;
		
		private Element(Object value){
			this.value=value;
			this.next=null;
		}
		
		private Element getNext(){
			return next;
		}
		
		private void setNext(Element element){
			next=element;	
		}
		
		private Object getValue(){
			return value;
		}
	}
}