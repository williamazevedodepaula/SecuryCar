package comur.example.SecuryCar;

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterListView extends BaseAdapter{

	private LayoutInflater mInflater;
	private List<ItemListView> itens;
	
	
	public AdapterListView(Context context, List<ItemListView> itens){
		//Itens do ListView
		this.itens = itens;
		
		//Objeto responsável por pegar o layout do item
		mInflater = LayoutInflater.from(context);
		
		
	} 
	
	
	
	@Override
	public int getCount() {
		return itens.size();
	}

	@Override
	public Object getItem(int position) {
		return itens.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
	   ItemSuport itemHolder;
	   
	   //Se a view estiver nula (nunca criada), inflamos o layout nela
	   ItemListView teste = itens.get(position);
      // if(view == null){
    	   //Infla o layout para podermos pegar as views
    	   
    	   
    	   if(itens.get(position).getItemType() == ItemListView.ItemType.itSingleLine){ 
    	      view = mInflater.inflate(R.layout.settings_itens,null);
    	   }else if(itens.get(position).getItemType() == ItemListView.ItemType.itDoubleLine){
    		   view = mInflater.inflate(R.layout.settings_itens_2_lines,null);
    	   }
    	   
    	   
    	   //Cria um suporte, para não precisarmos inflar sempre as mesmas informações
    	   itemHolder = new ItemSuport();
    	   itemHolder.txtTitle   = ((TextView )view.findViewById(R.id.text     ));
    	   itemHolder.imgIcon    = ((ImageView)view.findViewById(R.id.imageView));
    	   itemHolder.txtSubText = ((TextView )view.findViewById(R.id.SubText  ));
    	   
    	   
    	   //Define os itens na view
    	   view.setTag(itemHolder);
    //   }else{
    	   //Se a view já existe pega os itens
    	//   itemHolder = (ItemSuport) view.getTag();
    //   }
       
       
       
       //Pega os dados da lista e define os valores nos itens
       ItemListView item= itens.get(position);
       itemHolder.txtTitle.setText(item.getTexto());
       itemHolder.imgIcon.setImageResource(item.getIconeRid());      
       if(itens.get(position).getItemType() == ItemListView.ItemType.itDoubleLine){
          itemHolder.txtSubText.setText(item.getSubTexto());
       }
 	   
       
       
       
    
       
		
       //Retorna a view com as informações       				
		return view;
	}
	//Classe de suporte para os itens do layout
	private class ItemSuport{
		ImageView imgIcon;
		TextView txtTitle;
		TextView txtSubText;
	}
	
	
   
}
