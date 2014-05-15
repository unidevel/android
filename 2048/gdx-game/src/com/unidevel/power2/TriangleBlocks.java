package com.unidevel.power2;
import java.util.*;
import java.io.*;
import com.badlogic.gdx.utils.*;

public class TriangleBlocks
{
	Box[] data;
	int size;
	Random random;
	
	public TriangleBlocks(int size){
		this.size=size;
		this.random = new Random(System.currentTimeMillis());
		int n=size*size;
		data= new Box[n];
		for(int i=0;i<n;++i){
			data[i]=new Box(i);
		}
		this.createNext();
	}
	
	public void createNext(){
		int last=0;
		for(int l=0,p=0,n=0;l<size;++l){
			n=(l+1)*2-1;last+=n;
			for(int i=0;i<n;++i){
				Box b=data[p];
				if((i%2)==0){
					if(l<size-1){
						int x=last+i+1;
						b.setNext(1,data[x]);
						b.setNext(2,data[x]);
						t("p,x="+p+","+x);
					}
					else{
						
					}
				}
				else{
					b.setNext(0,data[p+1]);
					b.setNext(2,data[p+1]);
					b.setNext(1,data[p-1]);
					
					data[p-1].setNext(0, b);
				}
				p++;
			}
		}
	}
	
	public boolean fill(){
		int r = random.nextInt()>100?2:4;
		int n = random.nextInt(data.length-1)+1;
		int p=-1;
		for(int i=0;i<n;i++){
			p++;
			for(int j=0;j<data.length;++j){
				if(p>=data.length)p=0;
				if(data[p].value==0){
					break;
				}
				p++;
			}
		}
		t("fill "+p+" with "+r);
		if(p>=data.length || data[p].value!=0){
			return false;
		}
		data[p].value=r;
		return true;
	}
	
	public boolean move(int d, boolean next)
	{
		List<Box> h = new ArrayList<Box>();
		for(Box b:data){
			Box n=next?b.next[d]:b.prev[d];
			if(n==null){
				h.add(b);
			}
		}
		boolean moved = false;
		for(Box b:h){
			Box t=b;
			while(t!=null){
				moved|=t.move(d,next);
				t=next?t.prev[d]:t.next[d];
				/*
				Box p=t.prev[d];
				if(p!=null){
					if(t.value!=0){
						if(p.value==t.value){
							t.value+=p.value;
							p.value=0;
						}
					}
					else{
						t.value=p.value;
						p.value=0;
					}
				}
				t=p;
				*/
			}
			//System.out.println();
		}		
		return moved;
	}
	
	public int[] getValues(){
		int[] values=new int[data.length];
		for(int i=0;i<data.length;++i){
			values[i]=data[i].value;
		}
		return values;
	}
	
	void t(String m){
		System.out.println(m);
	}
	/*
	public int next(int n, int x,int y){
		int p=x+y;
		int q=x+1
		if(n==0){
			return x
		}
		return 0;
	}
	public void test(){
		for(int i=0;i<data.length;++i){
			data[i]=i+1;
		}
	}
	//protected int 
	 */
	 
	public void dump2(){
		for(Box b:data){
			System.out.print(b+",");
		}
		System.out.println();
	}
	
	public void dump3(){
		//PrintWriter out=new PrintWriter
		for(int l=0,p=0,n=1;l<size;++l,n+=2){
			for(int j =0;j<size-l-1;++j){
				System.out.print("   ");					
			}
			for(int i=0;i<n;++i){
				Box b=data[p++];
				System.out.printf("%3x",b.value);				
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void dump4(){
		int r=1;
		for(int l=0,p=0,n=1;l<size;++l,n+=2){
			if(l>0)r=2;
			int s=p;
			for(int k=0;k<2;++k)
			{
				p=s;
				for(int j =0;j<size-l-1;++j){
					System.out.print("   ");					
				}
				for(int i=0;i<n;++i){
					Box b=data[p++];
					if(i%2==1-k)
						System.out.printf("%3x",b.value);	
					else
						System.out.print("   ");					
				}
				System.out.println();
			}
		}
		System.out.println();
	}
	
	public void dump(){
		for(int i=0;i<3;++i){
			System.out.println("next"+i+":");
			List<Box> h = new ArrayList<Box>();
			for(Box b:data){
				if(b.prev[i]==null){
					h.add(b);
				}
			}
			for(Box b:h){
				Box t=b;
				while(t!=null){
					System.out.print(t+",");
					t=t.next[i];
				}
				System.out.println();
			}
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		TriangleBlocks b=new TriangleBlocks(4);
		//b.test();
		//b.dump();
		while(b.fill()){
			b.move(0,false);
			b.dump3();
		}
	}
}
