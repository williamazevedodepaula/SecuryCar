package comur.example.SecuryCar;


public class ItemListView {
	
	enum ItemType{itSingleLine,itDoubleLine}
	private ItemType itemType;
	private boolean ItemEnabled;
	private String texto;
	private String SubTexto;
	private int iconeRid;
	
	public ItemListView(){
		this("",-1);
	}
	
	public ItemListView(String texto, int iconeRid){
		this.texto = texto;
		this.iconeRid = iconeRid;
		this.itemType = ItemType.itSingleLine;
		this.SubTexto = "";
	}
	
	public ItemListView(String texto){
		this.texto = texto;
		this.itemType = ItemType.itSingleLine;
		this.SubTexto = "";
	}
		
	
	public ItemListView(String texto, String SubTexto){
		this.texto = texto;		
		this.SubTexto = SubTexto;
		this.itemType = ItemType.itDoubleLine;
	}
	
	
	public ItemListView(String texto, String SubTexto, int iconeRid){
		this.texto = texto;
		this.SubTexto = SubTexto;
		this.iconeRid = iconeRid;
		this.itemType = ItemType.itDoubleLine;
	}
	
	public ItemType getItemType(){
		return itemType;
	}
	
	public int getIconeRid(){
		return iconeRid;
	}
	
	public void setRiconeRid(int iconeRid){
		this.iconeRid = iconeRid;
	}
	
	public String getTexto(){
		return this.texto;
	}
	
	public String getSubTexto(){
		return this.SubTexto;
	}
	
	public void setTexto(String texto){
	   this.texto = texto;	
	}
	
	public void setSubTexto(String SubTexto){
		   this.SubTexto = texto;	
	}
		
	

}
