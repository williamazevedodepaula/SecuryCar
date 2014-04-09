package comur.example.SecuryCar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.UUID;

import comur.example.SecuryCar.R.drawable;



import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	
	private Handler handler = new Handler() {
	    public void handleMessage(Message message) {
	      Object path = message.obj;
	      if (message.arg1 == RESULT_OK && path != null) {
	        Toast.makeText(MainActivity.this,
	            "TESTE" + path.toString(), Toast.LENGTH_LONG)
	            .show();
	      } else {
	        Toast.makeText(MainActivity.this, "failed.",
	            Toast.LENGTH_LONG).show();
	      }

	    };
	  };
	
	
	public enum ConnAction {VERIF_STATUS,          //Varificar status do dispositivo
		                    CON_INI_AUTHENTICATE,  //Testar a Conexão e abrir tela de Autenticação
		                    CONN_DEV,              //Conectr ao dispositivo de trava (Conexão ao terminal do dispositivo)
		                    DATA_TRANS,            //Conectar para transmitir dados (Se o dispositivo estiver conectado)
		                    DESCON_DEV,            //Desconectar do dispositivo de trava (Desconectar do Terminal do dispositivo);
		                    LOCK_DEV,              //Ativar trava do dispositivo
		                    UNLK_DEV,              //Desativar trava do dispositivo
		                    GET_LOCK_TIME,         //Buscar tempo para bloqueio do dispositivo
		                    SET_LOCK_TIME		   //Altera o tempo para bloqueio do dispositivo	
	};
		                    
	public enum ConnectStatus {DISCONNECTED,       //Aguardando conexão (desconectado do terminal)
		                       CONNECTED,          //Autenricado (Conectado ao terminal) 
		                       INVALID}            //Dispositivo inválido
	
	public static final int REQUEST_ENABLE_BT = 1;
	public static final int SENHA_DISPOSITIVO = 2;	
	public static final int VISIBLE           = 1;
	public static final int INVISIBLE         = 0;
	public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	
	
	public static final CharSequence OK                  = "OK";	
	public static final CharSequence CONECTADO           = "Conectado";
	public static final CharSequence DESCONECTADO        = "Desconectado";
	public static final CharSequence CONNECTING          = "Conectando...";
	public static final CharSequence DISCONNECTING       = "Desconectando...";
	public static final CharSequence WAIT                = "Aguarde";
	
	public static final CharSequence ERROR_TITLE         = "Erro";
	public static final CharSequence DISP_INV            = "Dispositivo Inválido";
	public static final CharSequence NAO_HA_DISP_PAR     = "Não há dispositivos pareados";
	public static final CharSequence ERROR_CLOSE_CONN    = "Ocorreu um erro ao tentar fechar a conexão";
	public static final CharSequence CONNECT_ERROR       = "Não foi possível conectar ao dispositivo selecionado.";
	public static final CharSequence AUTENTICATION_FAIL  = "Autenticacao Falhou";	
	public static final CharSequence SENHA_INV           = "Senha Inválida";
	public static final CharSequence OPERATION_FAIL      = "Operação Falhou";
			
	
//Comandos do terminal do dispositivo
	public static final String END_FLAG                  = "%"; //Flag de fim de string
	public static final String COM_CONNECT               = "connect";
	public static final String COM_DISCONNECT            = "disconnect";
	public static final String COM_VERIF                 = "get conn status";
	public static final String COM_UNLK                  = "set lock off";
	public static final String COM_LOCK                  = "set lock on";
	public static final String COM_GET_TIME              = "get time";
	public static final String COM_SET_TIME              = "set time";
	public static final String COM_SET_PASS 			 = "set pass";
	public static final String COM_SAVE				     = "save";
	
	
//Mensagens de resposta do dispositivo
	public static final String WAINTING_PASSWORD         = "Aguardando Senha...";
	public static final String STATUS_CON                = "Conectado";
	public static final String STATUS_DES                = "Desconectado";
	public static final String STATUS_BLCK               = "Bloqueado";
	public static final String STATUS_DESBLCK            = "Desbloqueado";
	public static final String TIME                      = "Tempo: ";
	public static final String TIME_TO_LOCK              = "Tempo para bloqueio:";
	public static final String PASS_CHANGED_OK     = "Senha alterada com sucesso";
	
	
	private BluetoothSocket mmSocket;
 //   private static BluetoothDevice mmDevice;
    static BluetoothAdapter mBluetoothAdapter;
    ProgressDialog progress;
    private  ConnectThread coneccao;	
	private  BTtransfer transfer;
	private double doTimeToLock = 0;
	private int inChronCount = 0;
	
	
	private Spinner      spDevices;
	private TextView     txtError_Message;
	private TextView     txtLock;
	private TextView     txtUnLock;
	private TextView     txtStatus;
	private TextView     txtLabelStatus;
	private ImageButton  btLock;
	private ImageView    imgClock;
	private Chronometer  crChronometer;
	private TextView     lblChronTime;
	private ImageButton  btAtualiza;

	public Set<BluetoothDevice> pairedDevices;	
	public ArrayAdapter spAdapter;
	public static LockDevice Device;
	
	//Indica se o dispositivo está bloqueado (true) ou não (false)
	private boolean idStateLock = false;
	public  boolean idConected  = false;

	
	private CharSequence UltSelected = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        
        spAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item);
        
        //Spinner com dispositivos pareados
        txtError_Message = (TextView) findViewById(R.id.txtError_Message);
        spDevices        = (Spinner) findViewById(R.id.spDevices);
        spDevices.setAdapter(spAdapter);
        btLock           = (ImageButton)findViewById(R.id.btLock);
        btAtualiza       = (ImageButton)findViewById(R.id.btAtualiza);
        txtLock          = (TextView) findViewById(R.id.txtLock);
        txtUnLock        = (TextView) findViewById(R.id.txtUnLock);
        txtLabelStatus   = (TextView) findViewById(R.id.txtLabelStatus);
        txtStatus        = (TextView) findViewById(R.id.txtStatus);
        imgClock         = (ImageView) findViewById(R.id.imgClock);
        crChronometer    = (Chronometer) findViewById(R.id.crChronometer);
        lblChronTime     = (TextView)findViewById(R.id.lblChronTime);  
        
        
        btLock.setOnClickListener(new btLockClick());
        btAtualiza.setOnClickListener(new btAtualizaClick());
        spDevices.setOnItemSelectedListener(new spDevicesChange());
        crChronometer.setOnChronometerTickListener(new onTick());
        
         
        if(mBluetoothAdapter == null){
        	
        	txtError_Message.setText("Dispositivo não suporta bluetooth");
        	txtError_Message.setVisibility(View.VISIBLE);
        	
        
        }else{
        	
        	if(!mBluetoothAdapter.isEnabled()){
        		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        		startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
        		
        		
        	}
        	
        	
        }
        
    }
    
    @Override
    protected void onActivityResult(int requestCode, int ResultCode, Intent Data){
    	
    	//Se o resultado for da requisição de Ativar o Bluetooth
    	if( requestCode == REQUEST_ENABLE_BT){
    		/*
    		 * Se ativar o Bluetooth, oculta mensagem de erro ("Sem dispositivos pareados")
    		 * e atualiza a lista de dispositivos pareados 
    		 */
    		if(ResultCode == RESULT_OK){
    		  
        	   txtError_Message.setText("");
               txtError_Message.setVisibility(View.INVISIBLE);
        	
        	   ListaPareados();
    		}else{
    			//Se não ativar, sai da aplicação 
    			finish();
    		}
    	}else
    		
    	if(requestCode == SENHA_DISPOSITIVO){
    		
    		if(ResultCode == RESULT_OK){
    			    			    			    			    			
     /*			coneccao   = new ConnectThread(mmDevice,ConnAction.CONN_DEV,Data.getExtras().getString("password")); //Instancia a thread de conexão com a ação de conectar no dispositivo (autenticacao)	
		        transfer   = new BTtransfer(coneccao.getSocket());            //Instancia a thread de comunicação		        
		        coneccao.start();                                             //Inicia a Thread de conexão
		        */
    			idConected = true;		
    			RefreshStatus();  
    		}else{
    			idConected = false;    			
    			spDevices.setSelection(0);
        		RefreshStatus();    			
    		}

    		    
    	}
    }

    
    
    @Override
    protected void onStart(){    	
    	ListaPareados();   //Atualiza a lilsta de dispositivos pareados
    	if(!idStateLock){	
            btLock.setImageResource(drawable.ic_lock);
            txtLock.setVisibility(View.VISIBLE);
            txtUnLock.setVisibility(View.INVISIBLE);
         }else{
            btLock.setImageResource(drawable.ic_unlock);
            txtLock.setVisibility(View.INVISIBLE);
            txtUnLock.setVisibility(View.VISIBLE);
         }
    	DisplayCronometer(false,0);
    }

      @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    } 
    
      
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch(item.getItemId()){
    		case R.id.action_settings:
    			Intent configs = new Intent(MainActivity.this, settings_N2.class);
    			configs.putExtra("CallerItem",getString(R.string.Device_Settings));
    			configs.putExtra("CallerText",getString(R.string.Device_Settings));
    	        MainActivity.this.startActivityForResult(configs, 0);	  
    	        return true;    	        
    		case R.id.menu_Help:
    			Intent configsHelp = new Intent(MainActivity.this, settings.class);
    			configsHelp.putExtra("CallerItem",getString(R.string.menu_help));
    			configsHelp.putExtra("CallerText",getString(R.string.menu_help));
    	        MainActivity.this.startActivityForResult(configsHelp, 0);    	        
    	        return true;    				    	    	    
    	    default:
    	    	return super.onOptionsItemSelected(item); 
    			
    	}
    }
    
    //======================================================================================================
    
    public class btLockClick implements OnClickListener{
    	
    	
    	@Override
    	public void onClick(View V){
    		
    		if(idStateLock){
	           coneccao   = new ConnectThread(Device.getDevice(),ConnAction.UNLK_DEV,""); //Instancia a thread de conexão com a ação de desconectar do dispositivo	
    		}else{
    			coneccao   = new ConnectThread(Device.getDevice(),ConnAction.LOCK_DEV,""); //Instancia a thread de conexão com a ação de desconectar do dispositivo    			
    		}
	        transfer   = new BTtransfer(coneccao.getSocket());                 //Instancia a thread de comunicação
	        coneccao.start();                                                  //Inicia a Thread de conexão	            		    		    		    		    		    			    			    		
    	}
    	
    }
    
	
    public class btAtualizaClick implements OnClickListener{
    	

    	
    	@Override
    	public void onClick(View V){
    		Intent intent = new Intent(MainActivity.this,ConnService.class);
    		Messenger messenger = new Messenger(handler);
    	    intent.putExtra("device", Device);   
    	    intent.setData(Uri.parse("http://www.vogella.com/index.html"));
    	    startService(intent);          		    		    		    		    		    			    			    		
    	}
    	
    }
    
    public class spDevicesChange implements OnItemSelectedListener{
    	

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3)  {
			
			
			txtError_Message.setText("");
        	txtError_Message.setVisibility(View.INVISIBLE);        	
        	
        	
			if(spDevices.getSelectedItemPosition()!=0){
			   			   			  			   
			    //Encontra o Device pareado, comparando o nome e o MAC address dos devices pareados
				//Com o que está presente no spDevices
		        for(BluetoothDevice device : pairedDevices){
		        	if(spDevices.getSelectedItem().toString().equals( (device.getName()+"\n"+device.getAddress()))){
		        		//mmDevice = device;
		        		
		        		Device = new LockDevice(device);
		        		break;
		        	}
		        	
		        }		
		        
		        //Cria o diálogo "Aguarde/ Conectando...". O diálogo é fechado ao final da conexão, através do método "runOnUiThread"
		        progress = ProgressDialog.show(MainActivity.this,WAIT,CONNECTING);
		        
		        coneccao   = new ConnectThread(Device.getDevice(),ConnAction.CON_INI_AUTHENTICATE,""); //Instancia a thread de conexão com a ação de conectar no dispositivo (autenticacao)	
		        transfer   = new BTtransfer(coneccao.getSocket());            //Instancia a thread de comunicação
		        coneccao.start();                                             //Inicia a Thread de conexão
		        
				
			}else{
				if(idConected){
					
					progress = ProgressDialog.show(MainActivity.this,WAIT,DISCONNECTING);
			        
			        coneccao   = new ConnectThread(Device.getDevice(),ConnAction.DESCON_DEV,""); //Instancia a thread de conexão com a ação de desconectar do dispositivo	
			        transfer   = new BTtransfer(coneccao.getSocket());                 //Instancia a thread de comunicação
			        coneccao.start();                                                  //Inicia a Thread de conexão			        
					
				}else{
				
				   idConected = false;
				   RefreshStatus();
				}
			}
			
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
    	
    }
    
    
    public class onTick implements OnChronometerTickListener{

		@Override
		public void onChronometerTick(Chronometer chronometer) {
			inChronCount = inChronCount - 1;
			if ((inChronCount > 0)||(inChronCount == 0)){
			   lblChronTime.setText(FormatTime(inChronCount));			   
			}else{
				DisplayCronometer(false,0);
			}						
		}
    	
    }
    //===============================================================================================================
    
    public static BluetoothDevice getDevice(){
    	return Device.getDevice();
    }
    
    public void ListaPareados(){
    	super.onStart();
    	//(!mBluetoothAdapter.isEnabled());
    	pairedDevices = mBluetoothAdapter.getBondedDevices();//Busca dispositivos pareados
    	//Verifica se há dispositivo pareado
    	spAdapter.clear();
    	spAdapter.add("");
    	if(pairedDevices.size()>0){
    		//Percorre todos os dispositivos pareados
    		for(BluetoothDevice device : pairedDevices){
    			spAdapter.add(device.getName()+"\n"+device.getAddress());//Adiciona dados do dispositivo ao spinner
    		}
    		
    		
    	}else{
    		
    		txtError_Message.setText(NAO_HA_DISP_PAR);
        	txtError_Message.setVisibility(View.VISIBLE);
    	}
    	
    }
    
    public void ErrorMessage(CharSequence msg){
    	AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    	dialog.setMessage(msg);
    	dialog.setTitle(ERROR_TITLE);
    	dialog.setNeutralButton(OK, null);
    	dialog.show();
    	
    }
    
    public void RefreshStatus(){
    	
    	btLock.setEnabled(idConected);
    	if(!idStateLock){
            btLock.setImageResource(drawable.ic_lock);
            txtLock.setVisibility(View.VISIBLE);
            txtUnLock.setVisibility(View.INVISIBLE);
         }else{
            btLock.setImageResource(drawable.ic_unlock);
            txtLock.setVisibility(View.INVISIBLE);
            txtUnLock.setVisibility(View.VISIBLE);
         }
    	
    	if(idConected){
    		txtStatus.setText(CONECTADO);
    		txtStatus.setTextColor(Color.parseColor("#008000"));
    		
    	}else{
    		txtStatus.setText(DESCONECTADO);
    		txtStatus.setTextColor(Color.parseColor("#FF0000"));
    	}
    	    	    	    	
    }
    
   
    
    
    
    
    //Thread para realizar a conexão via bluetooth para 
    public class ConnectThread extends Thread{
    	private final BluetoothSocket _Socket;
        private final BluetoothDevice _Device;
        private ConnAction            _Action;
        private String                _PassWord;
        private final BluetoothDevice ConnDevice;
        private boolean finished;
        BTtransfer transfer;
        private String  str;
        
        private ConnectStatus Status = ConnectStatus.DISCONNECTED;
        
        public String getPassWord(){
        	return _PassWord;
        }
        public void setPassWord(String newPassWord){
        	_PassWord = newPassWord;
        }
        public void setAction(ConnAction newAction){
        	_Action = newAction;
        }
 	    public boolean isAutenticated(){
 	    	return (Status == ConnectStatus.CONNECTED);
 	    }
 	    public ConnectStatus getStatus(){
 	    	return Status;
 	    }
 	    public void setStatus(ConnectStatus new_status){
 	    	Status = new_status;
 	    }
 	    public BluetoothDevice getDevice(){
 	    	return ConnDevice;
 	    }
 	 
    
 	    public BluetoothSocket getSocket(){
 	    	return _Socket;
 	    }
 	    		 	    	
 	    public ConnectThread(BluetoothDevice Device, ConnAction action, String PassWord){
            ConnDevice = Device;
 	    	BluetoothSocket tmp = null;
 	    	_Action = action;
 	    	_Device = Device;
 	    	_PassWord = PassWord;
 	    	
 	    	
 		   //Cria o socket cliente para conectar no dispositivo
 		   try {
 			   
 				Method m = _Device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
 				tmp = (BluetoothSocket) m.invoke(_Device,1);
 		   } catch (IllegalArgumentException e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 	       } catch (IllegalAccessException e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 		   } catch (InvocationTargetException e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 		   } catch (NoSuchMethodException e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 		   }
 		   _Socket = tmp;
 	    }
 	    
 	    public void run(){
 		   // Cancela a descoberta de dispositivos para não tornar a conexão lenta 	    
           mBluetoothAdapter.cancelDiscovery();
  
           //Realiza a conexão com o dispositivo. Este método bloqueia até suceder ou lançar excessão
          
           
           try {                      	
         	 _Socket.connect();         	 
         	 idConected = true;       
          } catch (IOException connectException) {                      			            
             idConected = false;		        	                     	         	         
         	 try {         		
                 _Socket.close();
             } catch (IOException closeException) {}
         	            	 
         	 try {
				throw connectException;
			} catch (IOException e) {}
           	
            // return;
           }
           transfer = new BTtransfer(_Socket);
           
           
  		  
          if (_Action == MainActivity.ConnAction.CONN_DEV){
           
        	  if(Status == ConnectStatus.DISCONNECTED){  //Só envia o pedido de conexão se o mesmo não tiver sido enviado ainda
   	              str = COM_CONNECT + _PassWord + END_FLAG;
   	              transfer.write(str.getBytes());        //Envia Pedido de conexão ao terminal do dispositivo
   	              try {
   				      sleep(300);
   			      } catch (InterruptedException e) {}   	              
               }
               str = transfer.read();                    //Realiza a leitura da resposta do aplicativo cliente 
               
               String str1;
               String str2;
               if ((str.length() > AUTENTICATION_FAIL.length())||(str.length() == AUTENTICATION_FAIL.length())){
                     str1 = new String(str.getBytes(),0,AUTENTICATION_FAIL.length());
                     
                     Status = ConnectStatus.DISCONNECTED;
                     idConected = false;
               }
               if ((str.length() > STATUS_CON.length())||(str.length() == STATUS_CON.length())){
                   str1 = new String(str.getBytes(),0,STATUS_CON.length());
                   
                   Status = ConnectStatus.CONNECTED;
                   idConected = true;
               }
               
        	  
        	                               	                                         
           }else if ((_Action == MainActivity.ConnAction.VERIF_STATUS)||(_Action == MainActivity.ConnAction.CON_INI_AUTHENTICATE)){
        	   
        	   
    	        str = COM_VERIF+END_FLAG;
    	        transfer.write(str.getBytes());        //Envia Pedido de conexão ao terminal do dispositivo
    	        try {
    			     sleep(300);
    			} catch (InterruptedException e) {}
    	                        
                str = transfer.read(); 
                
                String str1 = str;
                String str2 = str;
                //Realiza a leitura da resposta do aplicativo cliente 
                if ((str.length() > STATUS_CON.length())||(str.length() == STATUS_CON.length())){
                      str1 = new String(str.getBytes(),0,STATUS_CON.length());
                }
                if ((str.length() > STATUS_DES.length())||(str.length() == STATUS_DES.length())){
                    str2 = new String(str.getBytes(),0,STATUS_DES.length());
                }
                if(str1.equals(STATUS_CON)){
                	Status = ConnectStatus.CONNECTED;
                }else if(str2.equals(STATUS_DES)){
                	Status = ConnectStatus.DISCONNECTED;
                }else{
                	Status = ConnectStatus.INVALID;
                }
        	   
                
        	   
           }else if (_Action == MainActivity.ConnAction.DESCON_DEV){
               
         	  if(idConected){  //Só envia o pedido de desconexão se o mesmo não tiver sido enviado ainda
    	              str = COM_DISCONNECT + END_FLAG;
    	              transfer.write(str.getBytes());        //Envia Pedido de desconexão ao terminal do dispositivo
    	              try {
    				      sleep(300);
    			      } catch (InterruptedException e) {}   	              
                }
                str = transfer.read();                    //Realiza a leitura da resposta do aplicativo cliente 
                

                if ((str.length() > STATUS_DES.length())||(str.length() == STATUS_DES.length())){
                    str = new String(str.getBytes(),0,STATUS_DES.length());                 
                }
                if(str.equals(STATUS_DES)){
                    Status = ConnectStatus.DISCONNECTED;
                    idConected = false;
                }
                	
                
         	  
         	                               	                                         
            }else if ((_Action == MainActivity.ConnAction.LOCK_DEV)||(_Action == MainActivity.ConnAction.UNLK_DEV)){
                
           	  if(idConected){                //Só envia o pedido de bloqueio/Desbloqueio se estiver conectado
           		   // if(!idStateLock){         //Só envia o pedido de bloqueio se o dispositivo estiver desbloqueado
           		        
           		      str = COM_GET_TIME + END_FLAG;
           		
                	  String str1 = "";
         	          String str2 = "";               
                      transfer.write(str.getBytes());        
	                  try {
			 	         sleep(300);
			          } catch (InterruptedException e) {}
	            
	                  str = transfer.read();                    //Realiza a leitura da resposta do aplicativo cliente
                                      
                      if ((str.length() > STATUS_DES.length())||(str.length() == STATUS_DES.length())){
                          str = new String(str.getBytes(),0,STATUS_DES.length());                 
                      }
                      if(str.equals(STATUS_DES)){
              	        idConected = false;
                      }
                      if ((str.length() > TIME.length())||(str.length() == TIME.length())){
                          str1 = new String(str.getBytes(),0,TIME.length());
                          str2 = new String(str.getBytes(),TIME.length(),str.length()-TIME.length());
                      }
                      if(str1.equals(TIME)){
                    	 doTimeToLock = Double.parseDouble(str2);
                     	 idConected = true;
                      }
           		      if(idConected){
           		    	 double time = 300;  
           		         if (_Action == MainActivity.ConnAction.LOCK_DEV){
      	                    str = COM_LOCK + END_FLAG;
      	                    time = time + (doTimeToLock * 1000); 
           		         }else{
           		            str = COM_UNLK + END_FLAG;
           		         }
      	                 transfer.write(str.getBytes());        
      	                 try {
      	                	 
      	                	    if (_Action == MainActivity.ConnAction.LOCK_DEV){
      	                	    runOnUiThread(new Runnable(){

      	      			            @Override
      	      			            public void run() {
      	      			        	   DisplayCronometer(true, (doTimeToLock));
      	      			            }
      	                	    });
      	                	 }
      	                	
      	                	 
      				         sleep((int)time);
      			         } catch (InterruptedException e) {}
           		      }
           		    //}
                  }
                  str = transfer.read();                    //Realiza a leitura da resposta do aplicativo cliente 
                  
                  if(_Action == MainActivity.ConnAction.LOCK_DEV){
                     if ((str.length() > STATUS_BLCK.length())||(str.length() == STATUS_BLCK.length())){
                         str = new String(str.getBytes(),0,STATUS_BLCK.length());                 
                     }
                     if(str.equals(STATUS_BLCK)){
                	     idStateLock = true;
                     }
                  }else if(_Action == MainActivity.ConnAction.UNLK_DEV){
                      if ((str.length() > STATUS_DESBLCK.length())||(str.length() == STATUS_DESBLCK.length())){
                          str = new String(str.getBytes(),0,STATUS_DESBLCK.length());                 
                      }
                      if(str.equals(STATUS_DESBLCK)){
                 	     idStateLock = false;
                      }
                   }
                  	
                  
           	  
           	                               	                                         
              }else if (_Action == MainActivity.ConnAction.GET_LOCK_TIME){
                  
               	  if(idConected){                //Só envia o pedido de bloqueio/Desbloqueio se estiver conectado               		   
               		        
               		        str = COM_GET_TIME + END_FLAG;
          	                transfer.write(str.getBytes());        
          	                try {
          				      sleep(300);
          			        } catch (InterruptedException e) {}

                      }
               	      String str1 = "";
               	      String str2 = "";
                      str = transfer.read();                    //Realiza a leitura da resposta do aplicativo cliente 
                                            
                      if ((str.length() > STATUS_DES.length())||(str.length() == STATUS_DES.length())){
                          str = new String(str.getBytes(),0,STATUS_DES.length());                 
                      }
                      if(str.equals(STATUS_DES)){
                    	  idConected = false;
                      }
                      if ((str.length() > TIME.length())||(str.length() == TIME.length())){
                          str1 = new String(str.getBytes(),0,TIME.length());
                          str1 = new String(str.getBytes(),TIME.length(),str.length());
                      }
                      if(str1.equals(TIME)){
                     	 doTimeToLock = Double.parseDouble(str2);
                      	 idConected = true;
                      }
                         
                  }
           
          
          runOnUiThread(new Runnable(){

			     @Override
			     public void run() {
			    	 			       	 
				   progress.dismiss();
				   
				   if (_Action == MainActivity.ConnAction.CONN_DEV){
					   
					  transfer.finish();
				      cancel();  //Fecha a conexão com o socket
				      if(Status == ConnectStatus.CONNECTED){
				    	  idConected = true;
				      }else{
				    	  ErrorMessage(SENHA_INV);				    	  
		                  idConected = false;
				      }					   					   
				   }else if (_Action == MainActivity.ConnAction.CON_INI_AUTHENTICATE){
				        if(Status == ConnectStatus.DISCONNECTED){
				        	   Intent password = new Intent(MainActivity.this, autenticacao.class);
					           MainActivity.this.startActivityForResult(password, SENHA_DISPOSITIVO);
					           transfer.finish();
					           cancel();
					           
				        }else{
				        	transfer.finish();
		                	cancel();  //Fecha a conexão com o socket
		                	idConected = false;
	  					    ErrorMessage(DISP_INV);
	  					    spDevices.setSelection(0);
	  		         	    RefreshStatus();
				        }				        				        
				   }else if (_Action == MainActivity.ConnAction.DESCON_DEV){
					   transfer.finish();
					   cancel();
					   progress.dismiss();
					   RefreshStatus();
				   }else if ((_Action == MainActivity.ConnAction.LOCK_DEV)||(_Action == MainActivity.ConnAction.UNLK_DEV)){
					     if(!idStateLock){
			                btLock.setImageResource(drawable.ic_lock);
			                txtLock.setVisibility(View.VISIBLE);
			                txtUnLock.setVisibility(View.INVISIBLE);
			                RefreshStatus();
			             }else{
			                btLock.setImageResource(drawable.ic_unlock);
			                txtLock.setVisibility(View.INVISIBLE);
			                txtUnLock.setVisibility(View.VISIBLE);
			                idConected = true;
			                RefreshStatus();
			             }
					     transfer.finish();
					     cancel();
			       }else if (_Action == MainActivity.ConnAction.GET_LOCK_TIME){					     						     
			    	         RefreshStatus();
			    	         transfer.finish();
						     cancel();
			       }
			    }
           });
        
          
          
 	    }  
 	    

        public void cancel(){
        	  try{
        		  _Socket.close();
        	  }catch(IOException e){}
        	  
        }
          
          
 		
 	}//fim ConnectThread                       
    
    public class BTtransfer{
    	private final BluetoothSocket _Socket;
    	private final InputStream     _InStream;
    	private final OutputStream    _OutStream;
    	
    	public BTtransfer(BluetoothSocket socket){
    		_Socket = socket;
    		InputStream tmpIn = null;
    		OutputStream tmpOut = null;
    		
    		try{
    		   tmpIn  = socket.getInputStream();
    		   tmpOut = socket.getOutputStream();
    		}catch(IOException e){}
    		_InStream = tmpIn;
    		_OutStream = tmpOut;
    	}
    	
    	
    	public String read(){
    		
    		byte[] buffer = new byte[1024];    		
    		int bytes;
    		String _string = "";
    		
    		try{
    			bytes = _InStream.read(buffer);
    			
    			 _string = new String(buffer,0,bytes);
        		
    		}catch(IOException e){    		
    			//return "";
    			e.printStackTrace();
    			
    		}
			return _string;
			    		    		    		
    	}
    	
    	
    	public void write(byte[] bytes){
    		try{
    			_OutStream.write(bytes);
    		}catch(IOException e){
    			idConected = false;   
    			try {
					_Socket.close();
				} catch (IOException e1) {}
    		}
    	}
    	
    	public void finish(){
    		try{
    			_OutStream.close();
    			_InStream.close();
    		}catch(IOException e){}    		
    	}
    	
    	
    	
    	
    	
    }//Fim de BTtransfer
    
   
    public void DisplayCronometer(boolean boActive, double time){
    	if(boActive){
    	   imgClock.setVisibility(View.VISIBLE);    	 
    	   lblChronTime.setVisibility(View.VISIBLE);
    	   
    	   lblChronTime.setText(FormatTime((int)time));
    	   inChronCount = (int) time;
    	   crChronometer.start();
    	   
    	   
    	}else{
    	   imgClock.setVisibility(View.INVISIBLE);     	   
     	   lblChronTime.setVisibility(View.INVISIBLE);
     	   crChronometer.stop();
     	 
    	}
    } 
    
    String FormatTime(int time){
       int dotime = time / 86400;
 	   int inHora = (time / 3600);
 	   String stHora;
 	   if(inHora < 10){
 		   stHora = "0"+Integer.toString(inHora);
 	   }else
 	       stHora = Integer.toString(inHora);
 	       	   
 	   int inMin = (time / 60) - (inHora*60);
 	   String stMin;
 	   if(inMin<10){
 		   stMin = "0"+Integer.toString(inMin);
 	   }
 	   else
 		   stMin = Integer.toString(inMin);
 	       	   
 	   int inSeg = time - (inHora*3600) - (inMin * 60);
 	   String stSeg;
 	   if (inSeg < 10){
 		   stSeg = "0"+Integer.toString(inSeg);
 	   }else
 		   stSeg = Integer.toString(inSeg);
 	   
 	   return (stHora+":"+stMin+":"+stSeg);
    }
       
  
    
}



