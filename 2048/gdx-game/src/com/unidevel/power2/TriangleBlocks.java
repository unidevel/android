package com.unidevel.power2;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TriangleBlocks
{
	Box[] data;
	int size;
	Random random;
	int score;
	
	public TriangleBlocks(int size){
		this.size=size;
		score=0;
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
			b.added=false;
			b.score=0;
			if(n==null){
				h.add(b);
			}
		}
		boolean moved = false;
		for(Box b:h){
			Box t=b;
			while(t!=null){
				moved|=t.move(d,next);
				if(moved){
					Log.i("(%d,%d) moved",t.index,t.value);
				}
				t=next?t.prev[d]:t.next[d];
			}
		}		
		for(Box b:data){
			this.score+=b.score;
		}
		Log.i("Score: %d", score);
		return moved;
	}
	
	public int[] getValues(){
		int[] values=new int[data.length];
		for(int i=0;i<data.length;++i){
			values[i]=data[i].value;
		}
		return values;
	}

	public boolean canMove(){
		for(Box b:data){
			if (b.value==0)return true;
			for(int i=0;i<3;++i){
				Box n,p;
				n=b.next[i];
				p=b.prev[i];
				if(n!=null){
					if(n.value==0){
						Log.i("%d can move next 0",b.index);
						return true;
					}
					if(n.value==b.value){
						Log.i("%d can move next %d",b.index, b.value);
						return true;
					}
				}
				if(p!=null){
					if(p.value==0){
						Log.i("%d can move prev %d",b.index, b.value);
						return true;
					}
					if(p.value==b.value){
						Log.i("%d can move prev %d",b.index, b.value);
						return true;
					}
				}
			}
		}
		return false;
	}

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
