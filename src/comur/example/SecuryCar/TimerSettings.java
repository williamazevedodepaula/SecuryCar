package comur.example.SecuryCar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import comur.example.SecuryCar.MainActivity.ConnAction;
import comur.example.SecuryCar.MainActivity.ConnectStatus;
import comur.example.SecuryCar.MainActivity.ConnectThread;
import comur.example.SecuryCar.autenticacao.BTtransfer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;



public class TimerSettings extends Activity{

	ImageButton btHrsUp;
	ImageButton btHrsDown;
	ImageButton btMinUp;
	ImageButton btMinDown;
	ImageButton btSegUp;
	ImageButton btSegDown;
	
	EditText    edtHrs;
	EditText    edtMin;
	EditText    edtSeg;		
	
	ImageButton btTimeOk;
	
	double  doTimeToLock = 0;
	
	ConnectThread coneccao;
	BTtransfer transfer;	
	BluetoothAdapter mBluetoothAdapter;
	public Set<BluetoothDevice> pairedDevices;
	LockDevice Device;
	ProgressDialog progress;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_settings);        
        
        btHrsUp   = (ImageButton)findViewById(R.id.btHrsUp);
        btHrsDown = (ImageButton)findViewById(R.id.btHrsDown);
        btMinUp   = (ImageButton)findViewById(R.id.btMinUp);
        btMinDown = (ImageButton)findViewById(R.id.btMinDown);
        btSegUp   = (ImageButton)findViewById(R.id.btSegUp);
        btSegDown = (ImageButton)findViewById(R.id.btSegDown);
        
        edtHrs    = (EditText)findViewById(R.id.edtHrs);
        edtMin    = (EditText)findViewById(R.id.edtMin);
        edtSeg    = (EditText)findViewById(R.id.edtSeg);
        
        btTimeOk  = (ImageButton)findViewById(R.id.btTimeOk);
        
        btHrsUp.setOnClickListener(new TimeButtonClick());
        btHrsDown.setOnClickListener(new TimeButtonClick());
        btMinUp.setOnClickListener(new TimeButtonClick());
        btMinDown.setOnClickListener(new TimeButtonClick());
        btSegUp.setOnClickListener(new TimeButtonClick());
        btSegDown.setOnClickListener(new TimeButtonClick());
        
       
        
    /*    btHrsUp.setOnTouchListener(new TimeButtonTouch());
        btHrsDown.setOnTouchListener(new TimeButtonTouch());
        btMinUp.setOnTouchListener(new TimeButtonTouch());
        btMinDown.setOnTouchListener(new TimeButtonTouch());
        btSegUp.setOnTouchListener(new TimeButtonTouch());
        btSegDown.setOnTouchListener(new TimeButtonTouch());
        */
        
        edtHrs.setOnFocusChangeListener(new EditTextChange());
        edtMin.setOnFocusChangeListener(new EditTextChange());
        edtSeg.setOnFocusChangeListener(new EditTextChange());
        
        btTimeOk.setOnClickListener(new btOkClick());
        
        String address = getIntent().getExtras().getString("Device_Address");
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		pairedDevices = mBluetoothAdapter.getBondedDevices();//Busca dispositivos pareados
		for(BluetoothDevice device : pairedDevices){
        	if(address.equals( device.getAddress())){
        		//mmDevice = device;
        		
        		Device = new LockDevice(device);
        		break;
        	}
        	
        }
		progress = ProgressDialog.show(TimerSettings.this,MainActivity.WAIT,MainActivity.CONNECTING);
		coneccao   = new ConnectThread(Device.getDevice(),ConnAction.GET_LOCK_TIME); //Instancia a thread de conexão com a ação de desconectar do dispositivo    			 		
	       // transfer   = new BTtransfer(coneccao.getSocket());                 //Instancia a thread de comunicação
	    coneccao.start();
    }
	
	
	
	public class btOkClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			coneccao   = new ConnectThread(Device.getDevice(),ConnAction.SET_LOCK_TIME); //Instancia a thread de conexão com a ação de desconectar do dispositivo    			 		
		       // transfer   = new BTtransfer(coneccao.getSocket());                 //Instancia a thread de comunicação
		    coneccao.start();
		}
		
	}
	
	public class TimeButtonClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			int val=0;
								
			//Picker de horas
			if(v.getId() == btHrsUp.getId()){
				val = Integer.parseInt(edtHrs.getText().toString());
				if (val<99){
				   val++;
				   edtHrs.setText(Integer.toString(val));
				   if(val<10){
						edtHrs.setText("0"+Integer.toString(val));
					}
				}else{
					val = 0;									
					edtHrs.setText("00");					
				}				
				
			}else if(v.getId() == btHrsDown.getId()){
				val = Integer.parseInt(edtHrs.getText().toString());
				if (val>0){
				   val--;
				   edtHrs.setText(Integer.toString(val));	
				   if(val<10){
						edtHrs.setText("0"+Integer.toString(val));
					}
				}else{
					val = 99;
					edtHrs.setText(Integer.toString(val));										
				}								
			}			
			
			//Picker de Minutos
			if(v.getId() == btMinUp.getId()){
				val = Integer.parseInt(edtMin.getText().toString());
				if (val<59){
				   val++;
				   edtMin.setText(Integer.toString(val));
				   if(val<10){
						edtMin.setText("0"+Integer.toString(val));
					}
				}else{
					val = 0;										
					edtMin.setText("00");					
				}				
				
			}else if(v.getId() == btMinDown.getId()){
				val = Integer.parseInt(edtMin.getText().toString());
				if (val>0){
				   val--;
				   edtMin.setText(Integer.toString(val));	
				   if(val<10){
						edtMin.setText("0"+Integer.toString(val));
					}
				}else{
					val = 59;
					edtMin.setText(Integer.toString(val));															
				}								
			}	
			
			//Picker de Segundos
			if(v.getId() == btSegUp.getId()){
				val = Integer.parseInt(edtSeg.getText().toString());
				if (val<59){
				   val++;
				   edtSeg.setText(Integer.toString(val));
				   if(val<10){
						edtSeg.setText("0"+Integer.toString(val));
					}
				}else{
					val = 0;										
					edtSeg.setText("00");					
				}				
				
			}else if(v.getId() == btSegDown.getId()){
				val = Integer.parseInt(edtSeg.getText().toString());
				if (val>0){
				   val--;
				   edtSeg.setText(Integer.toString(val));	
				   if(val<10){
					   edtSeg.setText("0"+Integer.toString(val));					   
					}
				}else{
					val = 59;
					edtSeg.setText(Integer.toString(val));							
				}								
			}
			
		}
		
	}
	
	public class TimeButtonLongClick implements OnLongClickListener{

		@Override
		public boolean onLongClick(View v) {
			TimeButtonClick click;
			//while(v.isPressed()){
				try {
 				      Thread.sleep(100);
 				      click = new TimeButtonClick();
 				      click.onClick(v);
 			      } catch (InterruptedException e) {}
				
			//}
			return false;
		}
		
	}
	
	public class TimeButtonTouch implements OnTouchListener{

		@Override
		public boolean onTouch(View v, MotionEvent e) {
			//TimeButtonClick click;
			if(e.getAction() == MotionEvent.ACTION_UP){
				v.setPressed(false);			
			}else
				if(e.getAction() == MotionEvent.ACTION_DOWN){					
					v.setPressed(true);
						/*if(!v.isPressed()){
						try {
		 				      Thread.sleep(100);
		 				      click = new TimeButtonClick();
		 				      click.onClick(v);
		 			      } catch (InterruptedException e1) {}
						}*/
					
				}/*else{
					//if(e.getAction() == MotionEvent.ACTION_MOVE){
						try {
		 				      Thread.sleep(100);
		 				      click = new TimeButtonClick();
		 				      click.onClick(v);
		 			      } catch (InterruptedException e1) {}
					}*/
			return true;
		}
		
	}
	
	public class EditTextChange implements OnFocusChangeListener{


		@Override
		public void onFocusChange(View v, boolean arg1) {
			
			if(Integer.parseInt( (String) ((EditText)v).getText().toString())<0){
				((EditText)v).setText("00");
			}
			
			if((v.getId() == edtHrs.getId())&&(Integer.parseInt((String) ((EditText)v).getText().toString())>99)){
				((EditText)v).setText("99");
				
			}else{
				if(((v.getId() == edtMin.getId())||(v.getId() == edtSeg.getId()))&&
				   (Integer.parseInt( ((EditText)v).getText().toString() )>59)
				  )
				{
					((EditText)v).setText("59");
					
				}
			}
			
		}
		
	}
	
	
	
	  //Thread para realizar a conexão via bluetooth para 
    public class ConnectThread extends Thread{
    	private final BluetoothSocket _Socket;
        private final BluetoothDevice _Device; 
        private ConnAction            _Action;
        private String                _PassWord; 
        BTtransfer transfer;
        private String  str;
        
        
        private ConnectStatus Status = ConnectStatus.DISCONNECTED;
        
        public String getPassWord(){
        	return _PassWord;
        }
    	public boolean isAutenticated(){
    	  	return (Status == ConnectStatus.CONNECTED);
    	}
    	public ConnectStatus getStatus(){
    	   	return Status;
    	}    	 
    	public BluetoothSocket getSocket(){
    	   	return _Socket;
    	}
    	    		 	    	
    	public ConnectThread(BluetoothDevice Device, ConnAction action){          
    	   BluetoothSocket tmp = null;    	   
    	   _Device = Device;
    	   _Action = action; 
    	    	
    	    	
    		//Cria o socket cliente para conectar no dispositivo
    		try {    			   
    				Method m = _Device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
    				tmp = (BluetoothSocket) m.invoke(_Device,1);
    		} catch (IllegalArgumentException  e) {} 
    		  catch (IllegalAccessException    e) {} 
    		  catch (InvocationTargetException e) {} 
    		  catch (NoSuchMethodException     e) {}
    		
    		   _Socket = tmp;
    	}
    	    
    	public void run(){
    		   // Cancela a descoberta de dispositivos para não tornar a conexão lenta 	    
           

           //Realiza a conexão com o dispositivo. Este método bloqueia até suceder ou lançar excessão
           try {
        	 //  MainActivity.mBluetoothAdapter.cancelDiscovery();
         	 _Socket.connect();         	      	        
           } catch (IOException connectException) {                      			            		        	                     	         	         
         	 try {         		
                 _Socket.close();
             } catch (IOException closeException) {}
         	            	 
         	 try {
    			throw connectException;
    		 } catch (IOException e) {}
           	
             
           }
           transfer = new BTtransfer(_Socket);
                  		        

           if (_Action == MainActivity.ConnAction.GET_LOCK_TIME){
               
            	//  if(Status == ConnectStatus.CONNECTED){//Só envia o pedido de bloqueio/Desbloqueio se estiver conectado               		   
            		        
            		    str = MainActivity.COM_GET_TIME + MainActivity.END_FLAG;
       	                transfer.write(str.getBytes());        
       	                try {
       				      sleep(300);
       			        } catch (InterruptedException e) {}

//                   }
            	   String str1 = "";
            	   String str2 = "";
                   str = transfer.read();                    //Realiza a leitura da resposta do aplicativo cliente 
                                         
                   if ((str.length() > MainActivity.STATUS_DES.length())||(str.length() == MainActivity.STATUS_DES.length())){
                       str = new String(str.getBytes(),0,MainActivity.STATUS_DES.length());                 
                   }
                   if(str.equals(MainActivity.STATUS_DES)){
                 	  Status = ConnectStatus.DISCONNECTED;
                   }
                   if ((str.length() > MainActivity.TIME.length())||(str.length() == MainActivity.TIME.length())){
                       str1 = new String(str.getBytes(),0,MainActivity.TIME.length());
                       str2 = new String(str.getBytes(),MainActivity.TIME.length(),str.length()-str1.length());
                   }
                   if(str1.equals(MainActivity.TIME)){
                  	 doTimeToLock = Double.parseDouble(str2);
                  	Status = ConnectStatus.CONNECTED;
                   }
                      
               }else{
            	   if (_Action == MainActivity.ConnAction.SET_LOCK_TIME){
                       
                   	//  if(Status == ConnectStatus.CONNECTED){//Só envia o pedido de bloqueio/Desbloqueio se estiver conectado               		   
                   		        
                   		    str = MainActivity.COM_SET_TIME +LockTime(Integer.parseInt(edtHrs.getText().toString()),
                   		    										  Integer.parseInt(edtMin.getText().toString()),
                   		    										  Integer.parseInt(edtSeg.getText().toString()))+ MainActivity.END_FLAG;
              	                transfer.write(str.getBytes());        
              	                try {
              				      sleep(500);
              			        } catch (InterruptedException e) {}

//                          }
                   	   String str1 = "";
                   	   String str2 = "";
                          str = transfer.read();                    //Realiza a leitura da resposta do aplicativo cliente                          
                                                
                          if ((str.length() > MainActivity.STATUS_DES.length())||(str.length() == MainActivity.STATUS_DES.length())){
                              str1 = new String(str.getBytes(),0,MainActivity.STATUS_DES.length());                 
                          }
                          if(str1.equals(MainActivity.STATUS_DES)){
                        	  Status = ConnectStatus.DISCONNECTED;                        	  
                          }
                          if ((str.length() > MainActivity.TIME_TO_LOCK.length())||(str.length() == MainActivity.TIME_TO_LOCK.length())){
                              str1 = new String(str.getBytes(),0,MainActivity.TIME_TO_LOCK.length());
                              str2 = new String(str.getBytes(),MainActivity.TIME_TO_LOCK.length(),str.length()-str1.length());
                          }
                          if(str1.equals(MainActivity.TIME_TO_LOCK)){
                         	 
                         	Status = ConnectStatus.CONNECTED;
                         	str = MainActivity.COM_SAVE+MainActivity.END_FLAG;				 //Solicita o salvamento da senha
                    	       transfer.write(str.getBytes());        //Envia Pedido de conexão ao terminal do dispositivo
                               try {
                       		    sleep(300);
                       	       } catch (InterruptedException e) {
                       	    	   
                       	    	str = "";
                       	       }                    
                               str = transfer.read();
                          }
                             
                      }
               }
    	

                     	                               	                                         
                 
           runOnUiThread(new Runnable(){
    		     @Override
    		     public void run() {		    	 			       	 			   			   			   				   
    			      if(Status == ConnectStatus.CONNECTED){	
    			    	  
    			    	  if (_Action == MainActivity.ConnAction.GET_LOCK_TIME){
    			    		   
    			    		  FormatTime((int)doTimeToLock);
    			    		  progress.dismiss();
    			    	  }else
    			    		  if (_Action == MainActivity.ConnAction.SET_LOCK_TIME){
       			    		   
    			    			  Intent returnIntent = new Intent();        	    			    	  
    	    			    	  setResult(RESULT_OK,returnIntent);
    	    			    	  TimerSettings.this.finish();
        			    	  }
    			    	  
    			    	  cancel();
    			    	  transfer.finish();
    			      }else{
    			    	  ErrorMessage(MainActivity.CONNECT_ERROR);
    			    	  cancel();  //Fecha a conexão com o socket	                  
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
    
    public void ErrorMessage(CharSequence msg){
    	AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    	dialog.setMessage(msg);
    	dialog.setTitle(MainActivity.ERROR_TITLE);
    	dialog.setNeutralButton(MainActivity.OK, null);
    	dialog.show();
    	
    }
    
    
   public void FormatTime(int time){        
  	   int inHora = (time / 3600);
  	   
  	   if(inHora < 10){
  		   edtHrs.setText("0"+Integer.toString(inHora));
  	   }else
  		 edtHrs.setText(Integer.toString(inHora));
  	       	   
  	   int inMin = (time / 60) - (inHora*60);
  	   
  	   if(inMin<10){
  		 edtMin.setText("0"+Integer.toString(inMin));
  	   }
  	   else
  		 edtMin.setText(Integer.toString(inMin));
  	       	   
  	   int inSeg = time - (inHora*3600) - (inMin * 60);
  	   
  	   if (inSeg < 10){
  		 edtSeg.setText("0"+Integer.toString(inSeg));
  	   }else
  		 edtSeg.setText(Integer.toString(inSeg));
  	   
  	   
     }
   
   public int LockTime(int hrs, int min, int seg){
		return seg + (min * 60) + (hrs * 3600);
		
	}
	
}




