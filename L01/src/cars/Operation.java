package cars;

import java.util.HashMap;

public class Operation {

	HashMap<String, String> database = new HashMap<String, String>();
	
	public byte[] regist(String plateNumber, String ownerName) {
		byte[] sbuf = new byte[256];
		String owner = database.get(plateNumber);
		if(owner == null) {
			database.put(plateNumber, ownerName);
			sbuf = Integer.toString(database.size()).getBytes();
		}else {
			sbuf = "-1".getBytes();
		}
				
		return sbuf;
	}
	
	public byte[] lookup(String plateNumber) {
		byte[] sbuf = new byte[256];
		String owner = database.get(plateNumber);
		
		if(owner == null) {
			sbuf = "NOT_FOUND".getBytes();
		}else {
			sbuf = owner.getBytes();
		}
				
		return sbuf;
	}

}
