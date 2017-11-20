package com.xaqb.unlock.other;

import java.util.ArrayList;


public class Tarea
{
	protected ArrayList<TareaItem> FoItem=new ArrayList<TareaItem>();
	protected boolean FbOpen=false;
	public Tarea(int iLeft, int iRight, int iY, boolean bOpen)
	{
		FoItem.add(new TareaItem(iLeft,iRight,iY));
		if(bOpen) FbOpen=true;
	}
	public boolean add(int iLeft,int iRight,int iY,boolean bOpen)
	{
		int i;
		TareaItem oItem;
		for(i=0;i<FoItem.size();i++)
		{
			
			oItem=FoItem.get(i);
			if(oItem.isLink(iLeft, iRight, iY))  
				{
				  	  
				  FoItem.add(new TareaItem(iLeft,iRight,iY));
				  if(bOpen) FbOpen=true;
				  return true;
				}
				
		}
		return false;
	 }
	
	public boolean test(int iLeft,int iRight,int iY)
	{
		int i;
		TareaItem oItem;
		for(i=0;i<FoItem.size();i++)
		{
		 	oItem=FoItem.get(i);
			if(oItem.isLink(iLeft, iRight, iY)) return true;  
	    }
		return false;
	 }
	
	public void merge(Tarea oItem)
	{
		int i;
		for(i=0;i<oItem.FoItem.size();i++)
		 FoItem.add(oItem.FoItem.get(i));
		oItem.FoItem.clear();
		if(oItem.FbOpen) FbOpen=true;
	}
	
	
	public int getTop()
	{
		int iTop=0;
		if(FoItem.size()>0) iTop=FoItem.get(0).FiY;
		int i;
		TareaItem oItem;
		for(i=1;i<FoItem.size();i++)
		{
			oItem=FoItem.get(i);
			if(oItem.FiY<iTop) iTop=oItem.FiY;
		}
		return iTop;
	}
	public int getBottom()
	{
		int iBottom=0;
		if(FoItem.size()>0) iBottom=FoItem.get(0).FiY;
		int i;
		TareaItem oItem;
		for(i=1;i<FoItem.size();i++)
		{
			oItem=FoItem.get(i);
			if(oItem.FiY>iBottom) iBottom=oItem.FiY;
		}
		return iBottom;
	}
	
	public int allSize()
	{
		int i,iSize=0;
		TareaItem oItem;
		for(i=0;i<FoItem.size();i++)
		{
		  oItem=FoItem.get(i);
		  iSize+=(oItem.FiRight-oItem.FiLeft+1);
		}
		return iSize;
	}
	public boolean isOpen()
	{
		return FbOpen;
	}
}