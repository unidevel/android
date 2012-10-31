package com.unidevel.tools.unlocker;

public class HoldDetector extends AbstractDetector
{
	int mode;
	int state;
	float matchValue;
	float value;
	float delta;
	HoldComparator xtor,ytor;

	public HoldDetector()
	{
	//	this.xtor=new HoldComparator(-90.0f, 90.0f);
	//	this.ytor=new HoldComparator(-180.0f, 180.0f);
		this.xtor=new HoldComparator(-180.0f, 0.0f);
		this.ytor=new HoldComparator(-270.0f, 90.0f);
	}

	public void setCondition(int mode, float value, float delta)
	{
		setMode(mode);
		this.matchValue = value;
		this.delta = delta;
	}

	public void setMode(int mode)
	{
		this.mode = mode;
	}
	
	public void input(float x, float y, float z)
	{
		if(state<0){
			updateStamp();
			return;
		}
		HoldComparator ctor,mtor;
		float value,fval;
		if(mode==0){
			value=x;
			fval=y;
			ctor=xtor;
			mtor=ytor;
		}
		else{
			value=y;
			fval=x;
			ctor=ytor;
			mtor=xtor;
		}
		if(!ctor.match(fval,0.0f,10.0f)){
			state=0;
			updateStamp();
			return;
		}
		else{
			if(!mtor.match(value,matchValue,delta)){
				state=0;
				updateStamp();
				return;
			}
			else{
				if(state==0){
					state=1;
				}
				else {
					state=-1;
				}
			}
		}
		
	}

	public boolean isMatch()
	{
		return state==1;
	}

	public boolean isInRange(){
		return state==-1||isMatch();
	}
}
