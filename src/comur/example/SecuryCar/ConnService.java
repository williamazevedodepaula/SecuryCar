package comur.example.SecuryCar;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import comur.example.SecuryCar.MainActivity.BTtransfer;
import comur.example.SecuryCar.MainActivity.ConnAction;
import comur.example.SecuryCar.MainActivity.ConnectStatus;

import android.app.IntentService;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class ConnService  extends IntentService{

	public ConnService() {
		super("ConnService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		LockDevice Device = (LockDevice) extras.getParcelable("device");
		 
		ConnectThread conn = new ConnectThread(Device.getDevice(),ConnAction.UNLK_DEV,"");
		conn.run();
		
	}
	
	
	
	
	
	
	
	
	
	
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
           
  
           //Realiza a conexão com o dispositivo. Este método bloqueia até suceder ou lançar excessão
          
           
           try {                      	
         	 _Socket.connect();         	 
         	        
          } catch (IOException connectException) {                      			            
             		        	                     	         	         
         	 try {         		
                 _Socket.close();
             } catch (IOException closeException) {}
         	            	 
         	 try {
				throw connectException;
			} catch (IOException e) {}
           	
            // return;
           }
 	    }
	}
    
  
}
