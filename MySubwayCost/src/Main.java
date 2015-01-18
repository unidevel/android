import java.util.*;

public class Main
{
	public static void main(String[] args)
	{
		System.out.println("Hello World!");

		Scanner input = new Scanner(System.in);

		System.out.print("Enter price: ");
		double price = input.nextDouble();

		System.out.print("Enter times: ");
		int times = input.nextInt();
		System.out.println();
		
		double product = calc2(price, times);
		System.out.printf("\nYour cost is: %f", product);
	}
	
	public static double calc2(double price, int times)
	{
		double cost=0;
		double p = price;
		double pp[]={100,1,150,.8,400,.5};
		int left=times;
		for(int i=0;i<pp.length;i+=2){
			double m=pp[i]-0.001;
			p = pp[i+1]*price;
			int t = (int)((m-cost)/p)+1;
			if(left<=t){
				price = p;
				break;
			}
			double n=t*p;
			cost+=n;
			left-=t;
			System.out.printf("Price: %f, count: %d, cost: %f\n", p, t, cost);
		}
		if(left>0){
			double n=price*left;
			cost += n;
			System.out.printf("Price: %f, count: %d, cost: %f\n", p, left, cost);
		}
		return cost;
	}
	
	public static double calc(double price, int times)
	{
		double cost=0;
		double p = price;
		int t1 = (int)((100-0.001)/p)+1;
		int left=times;
		if(left<=t1){
			cost = p*left;
		}
		else{
			cost += p*t1;
			left -= t1;
			p=0.8*price;
			int t2=(int)((199.99-cost)/p)+1;
			if(left<=t2){
				cost += p*left;
			}
			else{
				cost += p*t2;
				left -= t2;
				
				p=0.5*price;
				int t3=(int)((399.99-cost)/p)+1;
				if(left<=t3){
					cost += p*left;
				}
				else{
					cost += p*t3;
					left -= t3;
					p=price;
					cost+=p*left;
				}
			}
		}
		return cost;
	}
}
