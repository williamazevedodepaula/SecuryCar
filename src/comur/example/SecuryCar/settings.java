package comur.example.SecuryCar;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;



public class settings  extends Activity{
	
	private String[] MenuItens;
	private ListView ConfigList;
	private TextView  txtMenuTitle;
	private ImageView imgMenuTitle;
	
	private String DVICE_SETTINGS;
	private String APP_SETTINGS;
	private String APP_ABOUT;
	private String CONFIGURATIONS;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 		
        setContentView(R.layout.settings);

        ConfigList    = (ListView  ) findViewById(R.id.ConfigList);	
		txtMenuTitle  = (TextView  ) findViewById(R.id.txtMenuTitle);
		imgMenuTitle  = (ImageView ) findViewById(R.id.imgMenuTitle);
		
        List<ItemListView> list = new ArrayList<ItemListView>();
        
        String APPNAME = this.getString(R.string.app_name);
        
        CONFIGURATIONS = this.getString(R.string.Configurations);
		DVICE_SETTINGS = this.getString(R.string.Device_Settings);
		APP_SETTINGS   = this.getString(R.string.App_Settings   )+" "+APPNAME;
		APP_ABOUT      = this.getString(R.string.sobre          )+" "+APPNAME;
		
		
		
		String CallerItem = getIntent().getExtras().getString("CallerItem");		
		String CallerText = getIntent().getExtras().getString("CallerText");
		
		
		//Verifica qual opção do menu da MainActivity chamou esta
	    if(CallerItem.equals(getString(R.string.action_settings))){
		   
	    	imgMenuTitle.setImageResource(R.drawable.ic_menu_settings);
		    txtMenuTitle.setText(CallerText);
		    
		    MenuItens = new String[] {DVICE_SETTINGS,
		    						  APP_SETTINGS,	                  
	                  };
		    
		    list.add(new ItemListView(DVICE_SETTINGS/*   ,R.drawable.ic_menu_settings2*/));
		    list.add(new ItemListView(APP_SETTINGS/*,R.drawable.ic_app_config2 */       ));
	    }else if(CallerItem.equals(getString(R.string.menu_help))){
	    	
	    	MenuItens = new String[] {APP_ABOUT
	                  };
	    	
	    	imgMenuTitle.setImageResource(R.drawable.ic_ajuda);
		    txtMenuTitle.setText(CallerText);
	    	list.add(new ItemListView(APP_ABOUT, R.drawable.ic_about ));
	    }
		
		
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,MenuItens);
		AdapterListView adapter = new AdapterListView(getBaseContext(),list);
		
		//ConfigList    = (ListView  ) findViewById(R.id.ConfigList);		
		ConfigList.setAdapter(adapter);
		ConfigList.setOnItemClickListener(new ConfigListItemClick());
						

    }
	
	
   public class ConfigListItemClick implements OnItemClickListener{
		     
	@Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    // TODO Auto-generated method stub
	    final ItemListView item = (ItemListView) parent.getItemAtPosition(position);
	    
	    if (item.getTexto().equals(DVICE_SETTINGS)){
	    	
	    	Intent configs_n2 = new Intent(settings.this, settings_N2.class);
	    	configs_n2.putExtra("CallerItem",getString(R.string.Device_Settings));	    	
	    	configs_n2.putExtra("CallerText",DVICE_SETTINGS);
	        settings.this.startActivityForResult(configs_n2, 0);	  
	        
	    	
	    }else
        if (item.getTexto().equals(APP_SETTINGS)){
	    	
	    	Intent configs_n2 = new Intent(settings.this, settings_N2.class);
	    	configs_n2.putExtra("CallerItem",getString(R.string.App_Settings));
	    	configs_n2.putExtra("CallerText",APP_SETTINGS);
	        settings.this.startActivityForResult(configs_n2, 0);	  
	        
	    	
	    }
	    
	    	
	    	
      }
   }
	
	

}
