package Test;

public class Testd{ 
	
	String str=new String("hello"); 
	char[]ch={'a','b','c'}; 
	
	public static void main(String args[]){ 
		Testd ex=new Testd(); 
		ex.change(ex.str,ex.ch); 
		System.out.print(ex.str+" "); 
		System.out.print(ex.ch); 
	} 
	
	public void change(String str,char ch[]){ 
		str="hi"; 
		ch[0]='x'; 
	} 
	
}
