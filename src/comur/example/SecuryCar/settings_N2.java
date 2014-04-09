package comur.example.SecuryCar;

import java.util.ArrayList;
import java.util.List;

import comur.example.SecuryCar.settings.ConfigListItemClick;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;



public class settings_N2  extends Activity{
	
	private String[]  MenuItens;	
	private ListView  ConfigList;
	private TextView  txtMenuTitle;
	private ImageView imgMenuTitle;
		
	//Configurações do dispositivo
	private String TIME_TO_LOCK;
	private String CHANGE_PASSWORD;
	private String ACCESS_PERS;	
	
	
	AdapterListView adapter;
	
	
	//Configuração do App
	private String DEVICE_LIST;
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 		
        setContentView(R.layout.settings);

		String APPNAME  = this.getString(R.string.app_name       );
		TIME_TO_LOCK    = this.getString(R.string.Time_To_Lock   );
		CHANGE_PASSWORD = this.getString(R.string.Change_Password);
		ACCESS_PERS     = this.getString(R.string.Access_Pers    );
		DEVICE_LIST     = this.getString(R.string.Device_List    );
		
		ConfigList    = (ListView  ) findViewById(R.id.ConfigList);	
		txtMenuTitle  = (TextView  ) findViewById(R.id.txtMenuTitle);
		imgMenuTitle  = (ImageView ) findViewById(R.id.imgMenuTitle);
		
		
		String CallerItem = getIntent().getExtras().getString("CallerItem");		
		String CallerText = getIntent().getExtras().getString("CallerText");
		List<ItemListView> list = new ArrayList<ItemListView>();
		
		//Verifica qual opção da Activity "settings" chamou esta
		if(CallerItem.equals(getString(R.string.Device_Settings))){
			
			//Se o caller for a opção "Configurações do Dispositivo", carrega os itens correspondentes
			imgMenuTitle.setImageResource(R.drawable.ic_menu_settings);
			txtMenuTitle.setText(CallerText);
			list.add(new ItemListView(TIME_TO_LOCK   ,R.drawable.ic_time_config));
			list.add(new ItemListView(CHANGE_PASSWORD,R.drawable.ic_key        ));
			//list.add(new ItemListView(ACCESS_PERS    ,R.drawable.ic_user_list  ));
	
		}else
        if(CallerItem.equals(getString(R.string.App_Settings))){
			
			//Se o caller for a opção "Configurações do SecuryCar", carrega os itens correspondentes			
        	imgMenuTitle.setImageResource(R.drawable.ic_menu_settings);
        	txtMenuTitle.setText(CallerText);
			list.add(new ItemListView(DEVICE_LIST   ,R.drawable.ic_lista_device));
			
		}else{
					
			MenuItens = new String[] {""
            };
		}
			
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,MenuItens);
		
		
		
		adapter = new AdapterListView(getBaseContext(),list);
		
		
		
		ConfigList    = (ListView  ) findViewById(R.id.ConfigList);	
		txtMenuTitle  = (TextView  ) findViewById(R.id.txtMenuTitle);
		imgMenuTitle  = (ImageView ) findViewById(R.id.imgMenuTitle);
		
		ConfigList.setAdapter(adapter);
		ConfigList.setOnItemClickListener(new ConfigListItemClick());
						

    }
	
	
	 public class ConfigListItemClick implements OnItemClickListener{
	     
			@Override
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    // TODO Auto-generated method stub
			    final ItemListView item = (ItemListView) parent.getItemAtPosition(position);
			    
			    if (item.getTexto().equals(TIME_TO_LOCK)){
			    	
			    	Intent configs_n2 = new Intent(settings_N2.this, TimerSettings.class);
			    	configs_n2.putExtra("Device_Address",MainActivity.getDevice().getAddress());
			    	settings_N2.this.startActivityForResult(configs_n2, 0);			    	
			        
			    	
			    }
			    if (item.getTexto().equals(CHANGE_PASSWORD)){
			    	
			    	Intent changePass = new Intent(settings_N2.this, TrocaSenha.class);
			    	changePass.putExtra("Device_Address",MainActivity.getDevice().getAddress());
			    	settings_N2.this.startActivityForResult(changePass, 0);			    	
			        
			    	
			    }
			    
			    	
			    	
		      }
		   }
	
	
	
	

}
