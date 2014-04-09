package comur.example.SecuryCar;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import comur.example.SecuryCar.MainActivity.ConnAction;
import comur.example.SecuryCar.MainActivity.ConnectStatus;
import comur.example.SecuryCar.MainActivity.btLockClick;
import comur.example.SecuryCar.R.drawable;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;



public class autenticacao extends Activity{

	//Declara objetos
	private Button btOK;
	private Button btCancel;
	private TextView txtInfSenha;
	private EditText edtSenha;
	
	private ConnectThread AutConeccao;
	


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.autenticacao);
        
        
        //Spinner com dispositivos pareados
        btOK        = (Button  ) findViewById(R.id.btOK);
        btCancel    = (Button  ) findViewById(R.id.btCancel);
        edtSenha    = (EditText) findViewById(R.id.edtSenha);
        txtInfSenha = (TextView) findViewById(R.id.txtInfSenha);
        
        
        btOK.setOnClickListener    (new btOKClick    ());
        btCancel.setOnClickListener(new btCancelClick());                    

        
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    
    //======================================================================================================
    
    public class btOKClick implements OnClickListener{
    	
    	
    	@Override
    	public void onClick(View V){    	   	    	        		
    		
    		AutConeccao = new ConnectThread(MainActivity.getDevice(),edtSenha.getText().toString());
    		AutConeccao.start();
    		
    		
    				
    		
    	}
    	
    }
    public class btCancelClick implements OnClickListener{
    	
    	
    	
    	@Override
    	public void onClick(View V){
    		    		
    		Intent returnIntent = new Intent();    		
    		setResult(RESULT_CANCELED,returnIntent);
    		autenticacao.this.finish();
    		
    	}
    	
    	
    }
    
    public void ErrorMessage(CharSequence msg){
    	AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    	dialog.setMessage(msg);
    	dialog.setTitle(MainActivity.ERROR_TITLE);
    	dialog.setNeutralButton(MainActivity.OK, null);
    	dialog.show();
    	
    }
    
    
    //===============================================================================================================
    
   
    
  //Thread para realizar a conexão via bluetooth para 
    public class ConnectThread extends Thread{
    	private final BluetoothSocket _Socket;
        private final BluetoothDevice _Device;        
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
    	    		 	    	
    	public ConnectThread(BluetoothDevice Device, String PassWord){          
    	   BluetoothSocket tmp = null;    	   
    	   _Device = Device;
    	   _PassWord = PassWord;
    	    	
    	    	
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
           MainActivity.mBluetoothAdapter.cancelDiscovery();

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
           transfer = new BTtransfer(_Socket);
                  		  
          
           
           if(Status == ConnectStatus.DISCONNECTED){  //Só envia o pedido de conexão se o mesmo não tiver sido enviado ainda
    	      str = MainActivity.COM_CONNECT + _PassWord + MainActivity.END_FLAG;//pedido de Conexão + senha + flag
    	      transfer.write(str.getBytes());        //Envia Pedido de conexão ao terminal do dispositivo
    	      try {
    		    sleep(300);
    	      } catch (InterruptedException e) {}   	              
           }
           str = transfer.read();                    //Realiza a leitura da resposta do aplicativo cliente 
               
           String str1 = "";
           String str2 = "";
           if ((str.length() > MainActivity.AUTENTICATION_FAIL.length())||(str.length() == MainActivity.AUTENTICATION_FAIL.length())){
                 str1 = new String(str.getBytes(),0,MainActivity.AUTENTICATION_FAIL.length());                                                       
           }
           if ((str.length() > MainActivity.STATUS_CON.length())||(str.length() == MainActivity.STATUS_CON.length())){
                   str2 = new String(str.getBytes(),0,MainActivity.STATUS_CON.length());                                                   
           }
           
           if(str1.equals(MainActivity.AUTENTICATION_FAIL)){
        	       Status = ConnectStatus.DISCONNECTED;
           }else
           if(str2.equals(MainActivity.STATUS_CON)){
        	       Status = ConnectStatus.CONNECTED;
           }
        	  
        	                               	                                         
                 
           runOnUiThread(new Runnable(){
    		     @Override
    		     public void run() {		    	 			       	 			   			   			   				   
    			      if(Status == ConnectStatus.CONNECTED){	
    			    	  Intent returnIntent = new Intent();    
    			    	  returnIntent.putExtra("password",_PassWord);
    			    	  setResult(RESULT_OK,returnIntent);
    			    	  autenticacao.this.finish(); 
    			    	  cancel();
    			    	  transfer.finish();
    			      }else{
    			    	  ErrorMessage(MainActivity.SENHA_INV);
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
    
}

