package comur.example.SecuryCar;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

public class LockDevice implements Parcelable{
	enum lockState{lsLockOn, lsLockOff, lsLockUnknown}
	private String    name;	      //Atributo name fora do BluetoothDevice, pois permite mudar o nome
	private boolean   idConnected;
	private lockState idBloqueado;
	BluetoothDevice   btDevice;
	
	public LockDevice(BluetoothDevice bluetoothDevice){		
		this.name = bluetoothDevice.getName();
		this.idConnected = false;
		this.idBloqueado = lockState.lsLockUnknown;
		this.btDevice    = bluetoothDevice;
	}
	
	public void SetName(String name){
		this.name = name;
	}
	
	public String getname(){
		return this.name;
	}
	
	public String getmacAddress(){
		return this.btDevice.getAddress();
	}
	
	public boolean isConnected(){
		return idConnected;
	}
	
	public lockState getLockState(){
		return this.idBloqueado;
	}
	
	public BluetoothDevice getDevice(){
		return this.btDevice;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	

}
