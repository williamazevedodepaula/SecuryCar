package comur.example.SecuryCar;

import android.app.Activity;

public class ConnectState extends Activity{
	//Indica se o dispositivo est� bloqueado (true) ou n�o (false)
	private boolean idStateLock  = true;
	public boolean getIdStateLock(){
		return idStateLock;
	}
	public void setIdStateLock(boolean id){
		idStateLock = id;
	}
	
	private  boolean idConected  = false;
	public boolean getIdConnected(){
		return idConected;
	}
	public void setIdConected(boolean id){
		idConected = id;
	}
	
	
   
}
